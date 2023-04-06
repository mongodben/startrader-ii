package io.perlmutter.ben.routes

import com.mongodb.client.model.Aggregates
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.perlmutter.ben.models.Starship
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.json

fun Route.getAllShipsRoute(database: CoroutineDatabase) {
    get("/ships/all") {
        println("hello from ships/all route!")

        val starships = database.getCollection<Starship>("starships")

        val pipeline = listOf(
            Aggregates.lookup("ship_types", "ship_type_id", "_id", "ship_type"),
            Aggregates.lookup("users", "user_id", "_id", "user"),
            Aggregates.addFields(
                Aggregates.field("ship_type", Aggregates.arrayElemAt(Aggregates.field("ship_type"), 0)),
                Aggregates.field("user", Aggregates.arrayElemAt(Aggregates.field("user"), 0)),
            ),
        )

        val allShips = starships.aggregate<Any>(pipeline)

        val shipsList = allShips.toList().json
        println("SHIPS ARE:: $shipsList")
//        throw Exception("Bad!")
        call.response.status(HttpStatusCode.OK)
        call.respondText(shipsList)

    }
}