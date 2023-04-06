package io.perlmutter.ben.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.perlmutter.ben.routes.getAllShipsRoute
import io.perlmutter.ben.routes.getStarshipByIdRoute
import io.perlmutter.ben.routes.getUserByIdRoute
import org.litote.kmongo.coroutine.CoroutineDatabase

fun Application.configureRouting(database: CoroutineDatabase) {
    routing {
        getAllShipsRoute(database)
        getUserByIdRoute(database)
        getStarshipByIdRoute(database)
    }
}
