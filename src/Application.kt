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
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


class Info(_uptime: OffsetDateTime) {
    val uptime = _uptime.format(DateTimeFormatter.ISO_DATE_TIME)
}

val INFO_INSTANCE = Info(OffsetDateTime.now())

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
        get("/") {
            call.respond(INFO_INSTANCE)
        }

        get("/quotes") {
            val query =
            call.respond(qutoes.list())
        }

        get("/random") {
            call.respond(qutoes.list().random())
        }
    }
}

