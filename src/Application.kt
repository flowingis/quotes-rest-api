package it.flowing

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.client.*
import io.ktor.client.engine.jetty.*
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.basic
import io.ktor.request.receive
import it.flowing.config.Credentials
import it.flowing.dto.QuoteDTO
import it.flowing.repositories.Quotes
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


class Info(_uptime: OffsetDateTime) {
    val uptime = _uptime.format(DateTimeFormatter.ISO_DATE_TIME)
}

fun main(args: Array<String>): Unit = io.ktor.server.jetty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val quotes = Quotes()
    val INFO_INSTANCE = Info(OffsetDateTime.now())
    val credentials = Credentials.load()

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        allowCredentials = true
        anyHost()
    }

    install(Authentication) {
        basic("myBasicAuth") {
            realm = "Ktor Server"
            validate { if (it.name == credentials.user && it.password == credentials.password) UserIdPrincipal(it.name) else null }
        }
    }

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
            call.respond(quotes.search(
                query = query,
                maxSize = maxSize,
                author = author,
                tag = tag,
                count = count
            ))
        }

        get("/tags") {
            call.respond(quotes.tags())
        }

        get("/authors") {
            call.respond(quotes.authors())
        }

        get("/random") {
            val query = call.request.queryParameters["q"] ?: ""
            val author = call.request.queryParameters["author"] ?: ""
            val tag = call.request.queryParameters["tag"] ?: ""
            val maxSize = call.request.queryParameters["max-size"]?.toIntOrNull() ?: 0
            call.respond(quotes.random(
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

            val quote = quotes.find(id)

            if(quote != null){
                call.respond(quote)
                return@get
            }

            call.respond(HttpStatusCode.NotFound)
        }

        authenticate("myBasicAuth") {
            post("/quotes") {
                val dto = call.receive<QuoteDTO>()
                val maybeQuote = dto.toQuote()
                if(maybeQuote == null){
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                val insertedQuote = quotes.insert(maybeQuote)

                call.respond(insertedQuote)

            }
        }
    }
}

