package it.flowing

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.client.*
import io.ktor.client.engine.jetty.*

fun main(args: Array<String>): Unit = io.ktor.server.jetty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val qutoes = Quotes()
    install(ContentNegotiation) {
        gson {
        }
    }

    val client = HttpClient(Jetty) {
    }

    routing {
        get("/quotes") {
            call.respond(qutoes.list())
        }

        get("/random") {
            call.respond(qutoes.list().random())
        }
    }
}

