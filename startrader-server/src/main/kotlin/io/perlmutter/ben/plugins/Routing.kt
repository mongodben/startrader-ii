package io.perlmutter.ben.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.perlmutter.ben.routes.*
import org.litote.kmongo.coroutine.CoroutineDatabase

fun Application.configureRouting(database: CoroutineDatabase) {
    routing {
        getAllShipsRoute(database)
        getUserByIdRoute(database)
        getStarshipByIdRoute(database)
        getSpeciesRoute(database)
        getAllShipTypesRoute(database)
        getAllUniqueShips(database)
        getShipsByClass(database)
        createUserRoute(database)
        loginUserRoute(database)
        authenticate("auth-jwt") {
            fundUserCreditsRoute(database)
            createStarshipRoute(database) // TODO: validate functionality..right now written but not tested
        }

    }
}
