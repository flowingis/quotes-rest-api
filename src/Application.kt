package it.flowing

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.client.*
import io.ktor.client.engine.jetty.*
import io.ktor.http.HttpStatusCode
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
            val query = call.request.queryParameters["q"] ?: ""
            val author = call.request.queryParameters["author"] ?: ""
            val tag = call.request.queryParameters["tag"] ?: ""
            val count = call.request.queryParameters["count"]?.toIntOrNull() ?: Int.MAX_VALUE
            val maxSize = call.request.queryParameters["max-size"]?.toIntOrNull() ?: 0
            call.respond(qutoes.search(
                query = query,
                maxSize = maxSize,
                author = author,
                tag = tag,
                count = count
            ))
        }

        get("/tags") {
            call.respond(qutoes.tags())
        }

        get("/authors") {
            call.respond(qutoes.authors())
        }

        get("/random") {
            val query = call.request.queryParameters["q"] ?: ""
            val author = call.request.queryParameters["author"] ?: ""
            val tag = call.request.queryParameters["tag"] ?: ""
            val maxSize = call.request.queryParameters["max-size"]?.toIntOrNull() ?: 0
            call.respond(qutoes.random(
                query = query,
                maxSize = maxSize,
                author = author,
                tag = tag
            ))
        }

        get("/quotes/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: 0

            if(id <= 0){
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val quote = qutoes.find(id)

            if(quote != null){
                call.respond(quote)
                return@get
            }

            call.respond(HttpStatusCode.NotFound)
        }
    }
}

