package io.perlmutter.ben.routes

import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Field
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.Route
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.json
import io.perlmutter.ben.models.*
import io.perlmutter.ben.utils.*
import org.bson.BsonObjectId
import org.bson.Document
import org.bson.types.ObjectId
import org.litote.kmongo.id.toId
import kotlinx.serialization.Serializable

val passwordAuthentication = PasswordAuthentication()
fun Route.getUserByIdRoute(database: CoroutineDatabase) {
    get("/users/{userId}") {
        val userId = BsonObjectId(ObjectId(call.parameters["userId"]))

        if (userId != null) {
            val users = database.getCollection<User>("users")
            val starships = database.getCollection<Starship>("starships")
            val species = database.getCollection<Species>("species")

            val user = users.findOne(User::key eq userId.value.toId())
            if (user != null) {
                // aggregation query that finds all starships and their associated ship type for a given user
                val userStarships = starships.aggregate<Any>(
                    listOf(
                        Aggregates.match(Starship::owner_user_id eq userId.value.toId()),
                        Aggregates.lookup("ship_types", "ship_type_id", "_id", "ship_type"),
                        Aggregates.addFields(
                            Field("ship_type", Document("\$arrayElemAt", listOf("\$ship_type", 0)))
                        )
                    )
                ).toList()

                val (key, username, email, password, species) = user
                val returnObj = mapOf("key" to key, "username" to username, "email" to email, "password" to password, "species" to species, "starships" to userStarships)
                call.respond(HttpStatusCode.OK, returnObj.json)
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse(HttpStatusCode.NotFound.value, "User not found").json
                )
            }
        } else {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(HttpStatusCode.BadRequest.value, "Invalid user ID").json

            )
        }
    }
}

fun Route.getSpeciesRoute(database: CoroutineDatabase) {
    get("/users/species") {
        val speciesCollection = database.getCollection<Species>("species")
        val allSpecies = speciesCollection.find().toList()
        val species = mapOf("species" to allSpecies).json

        call.respond(HttpStatusCode.OK, species)
    }
}

fun Route.createUserRoute(database: CoroutineDatabase) {
    post("/users/create") {
        val data = call.receive<CreateUserRequest>()

        val existingUser = database
            .getCollection<User>("users")
            .findOne(User::email eq data.email)
        if (existingUser != null) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    HttpStatusCode.BadRequest.value, "User already exists"
                ).json
            )
            return@post
        }

        val newUser = User(
            name = data.name,
            email = data.email,
            password = passwordAuthentication.hash(data.password),
            species = data.species,
            bio = data.bio,
            faction = data.faction,
            credits = 0,
            user_image = data.user_image,
            force_points = 0
        )
        database.getCollection<User>("users").insertOne(newUser)

        val jwtToken = JwtManagement.createJwtToken(newUser.email)
        call.respond(HttpStatusCode.Created, mapOf("access_token" to jwtToken, "user" to newUser.json))

    }
}
@Serializable
data class CreateUserRequest(
    val name: String,
    val email: String,
    val password: String,
    val species: String,
    val bio: String = "",
    val faction: String = "Empire",
    val credits: Int = 150000,
    val user_image: String?,
    val force_points: Int = 0
)

fun Route.loginUserRoute(database: CoroutineDatabase) {
    post("/users/login") {
        val data = call.receive<LoginUserRequest>()

        val user = database
            .getCollection<User>("users")
            .findOne(User::email eq data.email)

        // Validate that user exits
        if (user == null) {
            call.respond(
                HttpStatusCode.UnprocessableEntity,
                ErrorResponse(
                    HttpStatusCode.UnprocessableEntity.value, "Email not found"
                ).json
            )
            return@post
        }

        // Validate that user password is correct
        val isCorrectPassword = passwordAuthentication.authenticate(data.password, user.password)
        if(!isCorrectPassword){
            call.respond(
                HttpStatusCode.UnprocessableEntity,
                ErrorResponse(
                    HttpStatusCode.UnprocessableEntity.value, "Incorrect password"
                ).json
            )
            return@post
        }
        val starships = database
            .getCollection<Starship>("starships")
            .find(Starship::owner_user_id eq user.key)
            .toList()
        val userWithStarships = user.toMap().plus("starships" to starships)
        val jwtToken = JwtManagement.createJwtToken(user.email)
        call.respond(
            HttpStatusCode.OK,
            mapOf("access_token" to jwtToken, "user" to userWithStarships).json
        )

    }
}

fun Route.fundUserCreditsRoute(database: CoroutineDatabase) {
    put("/users/fundcredits/{userId}") {
        lateinit var userId: BsonObjectId
        try{
            userId = call.parameters["userId"]?.let { BsonObjectId(ObjectId(it)) }!!
        } catch (e: Exception){
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(HttpStatusCode.BadRequest.value, "Invalid user id").json
            )
            return@put
        }


        val data = call.receive<FundCreditsRequest>()

        if(data.credits <= 0 || data.credits > 10000000){
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(HttpStatusCode.BadRequest.value, "Invalid credit amount").json
            )
            return@put
        }

        val user = database.getCollection<User>("users")
            .findOne(User::key eq userId.value.toId())
        if (user == null) {
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(HttpStatusCode.NotFound.value, "User not found").json
            )
            return@put
        }

        val principal = call.principal<JWTPrincipal>()
        val username = principal!!.payload.getClaim("username").asString()
        if(user.email != username){
            call.respond(
                HttpStatusCode.Unauthorized,
                ErrorResponse(HttpStatusCode.Unauthorized.value, "Unauthorized").json
            )
            return@put
        }

        val updatedUser = user.copy(credits = user.credits + data.credits)
        database.getCollection<User>("users")
            .updateOne(User::key eq userId.value.toId(), updatedUser)

        call.respond(HttpStatusCode.OK, mapOf("user" to updatedUser).json)
    }
}

@Serializable
data class FundCreditsRequest(
    val credits: Int
)


@Serializable
data class LoginUserRequest(
    val email: String,
    val password: String
)

