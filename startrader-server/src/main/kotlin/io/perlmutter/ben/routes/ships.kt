package io.perlmutter.ben.routes

import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Field
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.perlmutter.ben.models.ErrorResponse
import io.perlmutter.ben.models.Starship
import io.perlmutter.ben.models.StarshipType
import io.perlmutter.ben.models.User
import kotlinx.serialization.Serializable
import org.bson.BsonObjectId
import org.bson.Document
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.id.toId
import org.litote.kmongo.`in`
import org.litote.kmongo.json
import java.util.*

fun Route.getAllShipsRoute(database: CoroutineDatabase) {
    get("/ships/all") {
        println("hello from ships/all route!")

        val starships = database.getCollection<Starship>("starships")

        val pipeline = listOf(
            Aggregates.lookup("ship_types", "ship_type_id", "_id", "ship_type"),
            Aggregates.lookup("users", "owner_user_id", "_id", "user"),
            Aggregates.addFields(
                Field("ship_type", Document("\$arrayElemAt", listOf("\$ship_type", 0))),
                Field("user", Document("\$arrayElemAt", listOf("\$user", 0)))
            )
        )

        val allShips = starships.aggregate<Any>(pipeline)

        val shipsList = allShips.toList().json
        println("SHIPS ARE:: $shipsList")
        call.response.status(HttpStatusCode.OK)
        call.respondText(shipsList)

    }
}


fun Route.getStarshipByIdRoute(database: CoroutineDatabase) {
    get("/ships/{shipId}") {
        val shipId = BsonObjectId(ObjectId(call.parameters["shipId"]))

        if (shipId != null) {
            val starships = database.getCollection<Starship>("starships")

            val pipeline = listOf(
                Aggregates.match(Starship::key eq shipId.value.toId()),
                Aggregates.lookup("ship_types", "ship_type_id", "_id", "ship_type"),
                Aggregates.lookup("users", "owner_user_id", "_id", "user"),
                Aggregates.addFields(
                    Field("ship_type", Document("\$arrayElemAt", listOf("\$ship_type", 0))),
                    Field("user", Document("\$arrayElemAt", listOf("\$user", 0)))
                ),
                Aggregates.limit(1)
            )

            val results = starships.aggregate<Any>(pipeline).toList()

            if (results.isNotEmpty()) {
                val starship = results[0]
                call.respond(HttpStatusCode.OK, starship.json)
            } else {
                // Handle the case when the starship is not found
                call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse(HttpStatusCode.NotFound.value, "Starship not found").json
                )
            }
        } else {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(HttpStatusCode.BadRequest.value, "Invalid starship ID").json
            )
        }
    }
}

fun Route.getAllShipTypesRoute(database: CoroutineDatabase) {
    get("/ships/shiptypes") {
        val shipTypes = database.getCollection<StarshipType>("ship_types")
        val allShipTypes = shipTypes.find().toList()
        val response = mapOf("ship_types" to allShipTypes).json

        call.respond(HttpStatusCode.OK, response)
    }
}

fun Route.getAllUniqueShips(database: CoroutineDatabase){
    get("/ships/uniques") {
        val starshipsTypes = database.getCollection<StarshipType>("ship_types")
        val starships = database.getCollection<Starship>("starships")

        val uniqueShipTypes = starshipsTypes.find(StarshipType::unique eq true).toList().map { it.key }

        val pipeline = listOf(
            Aggregates.match(Starship::ship_type_id `in`  uniqueShipTypes),
            Aggregates.lookup("ship_types", "ship_type_id", "_id", "ship_type"),
            Aggregates.lookup("users", "owner_user_id", "_id", "user"),
            Aggregates.addFields(
                Field("ship_type", Document("\$arrayElemAt", listOf("\$ship_type", 0))),
                Field("user", Document("\$arrayElemAt", listOf("\$user", 0)))
            ),
        )
        val results = starships.aggregate<Any>(pipeline).toList()

        val response = mapOf("star_ships" to results).json

        call.respond(HttpStatusCode.OK, response)
    }
}

fun Route.getShipsByClass(database: CoroutineDatabase) {
    get("/ships/class/{shipclass}") {
        val shipClass = call.parameters["shipclass"]
        val starships = database.getCollection<Starship>("starships")
        val shipTypes = database.getCollection<StarshipType>("ship_types")

        val pipeline = listOf(
            Aggregates.lookup("ship_types", "ship_type_id", "_id", "ship_type"),
            Aggregates.lookup("users", "owner_user_id", "_id", "user"),
            Aggregates.addFields(
                Field("ship_type", Document("\$arrayElemAt", listOf("\$ship_type", 0))),
                Field("user", Document("\$arrayElemAt", listOf("\$user", 0)))
            ),
            Aggregates.match(Document("ship_type.starship_class", shipClass))
        )

        val results = starships.aggregate<Any>(pipeline).toList()

        val response = mapOf("star_ships" to results).json

        call.respond(HttpStatusCode.OK, response)
    }
}

fun Route.createStarshipRoute(database: CoroutineDatabase) {
    post("/create") {
        val data = call.receive<CreateStarshipRequest>()

        val principal = call.principal<JWTPrincipal>()
        val username = principal!!.payload.getClaim("username").asString()

        val user = database.getCollection<User>("users")
            .findOne(User::email eq username)
        if (user == null) {
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(HttpStatusCode.NotFound.value, "You cannot make a starship for user '$username' because user doesn't exist.").json
            )
            return@post
        }


        if(user.email != username){
            call.respond(
                HttpStatusCode.Unauthorized,
                ErrorResponse(HttpStatusCode.Unauthorized.value, "User $username unauthorized to create starships for ${user.email}").json
            )
            return@post
        }

        val starship = Starship(
            ship_type_id = data.ship_type,
            custom_name = data.custom_name,
            sale_price = data.sale_price,
            light_years_traveled = data.lightyears_traveled,
            owner_user_id = data.owner,
            for_sale = data.for_sale,
            seller_comment = data.seller_comment,
            post_date = Date()
        )
        database.getCollection<Starship>("starships").insertOne(starship)
        call.respond(HttpStatusCode.OK, mapOf("starship" to starship.json))

    }
}

@Serializable
data class CreateStarshipRequest(
    val ship_type: Id<StarshipType>,
    val custom_name: String,
    val sale_price: Float,
    val lightyears_traveled: Int,
    val owner: Id<User>,
    val for_sale: Boolean,
    val seller_comment: String
)