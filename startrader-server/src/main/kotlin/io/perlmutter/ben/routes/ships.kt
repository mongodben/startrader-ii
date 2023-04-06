package io.perlmutter.ben.routes

import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Field
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.perlmutter.ben.models.ErrorResponse
import io.perlmutter.ben.models.Starship
import io.perlmutter.ben.models.StarshipType
import io.perlmutter.ben.models.User
import org.bson.BsonObjectId
import org.bson.Document
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.id.toId
import org.litote.kmongo.json

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