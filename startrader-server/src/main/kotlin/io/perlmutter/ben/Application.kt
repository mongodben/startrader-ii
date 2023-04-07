package io.perlmutter.ben

import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.perlmutter.ben.models.ErrorResponse
import io.perlmutter.ben.plugins.configureHTTP
import io.perlmutter.ben.plugins.configureMonitoring
import io.perlmutter.ben.plugins.configureRouting
import io.perlmutter.ben.plugins.configureSecurity
import io.ktor.serialization.kotlinx.json.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.json
import org.litote.kmongo.reactivestreams.KMongo

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)

}

data class Test (
    val foo: String
)

fun Application.module() {
    val dotenv = dotenv();
    val mongodbConnectionUri = dotenv["MONGODB_CONNECTION_URI"]
    val databaseName = dotenv["DATABASE_NAME"]
    val kmongoClient = KMongo.createClient(connectionString = mongodbConnectionUri).coroutine
    val database = kmongoClient.getDatabase(databaseName)

    install(ContentNegotiation) {
        json()
    }
    configureMonitoring()
    configureHTTP()
    configureSecurity()
    configureRouting(database)

    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, status ->
            val msg = ErrorResponse(status.value, "${status.value}: Resource Not Found").json
            call.respondText(text = msg, status = status)
        }
        exception<Throwable> { call, cause ->
            val msg = ErrorResponse(500, "500: $cause").json
            call.respondText(text = msg, status = HttpStatusCode.InternalServerError)
        }
    }

}
