package io.perlmutter.ben.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.perlmutter.ben.routes.getAllShipsRoute
import org.litote.kmongo.coroutine.CoroutineDatabase

fun Application.configureRouting(database: CoroutineDatabase) {
    routing {
        getAllShipsRoute(database)
    }
}
