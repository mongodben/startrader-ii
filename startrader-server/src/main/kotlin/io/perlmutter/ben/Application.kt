package io.perlmutter.ben

import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.perlmutter.ben.plugins.configureHTTP
import io.perlmutter.ben.plugins.configureMonitoring
import io.perlmutter.ben.plugins.configureRouting
import io.perlmutter.ben.plugins.configureSecurity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.coroutine.coroutine
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
    println("DB NAME:: $databaseName")
    val kmongoClient = KMongo.createClient(connectionString = mongodbConnectionUri).coroutine
    val database = kmongoClient.getDatabase(databaseName)
    val col = database.getCollection<Test>("test")
    val res = col.find()
    runBlocking(Dispatchers.IO) {
        val resList = res.toList()
        println("RES:: $resList")
    }

    configureMonitoring()
    configureHTTP()
    configureSecurity()
    configureRouting()

}
