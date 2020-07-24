package it.flowing.config

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URISyntaxException

class Credentials(val password: String, val user: String) {

    companion object {
        private const val CREDENTIAL_PATH = "/credentials.json"
        private val gson = Gson()

        @Throws(IOException::class, URISyntaxException::class)
        fun load(): Credentials {
            val inputStream =
                Credentials::class.java.getResourceAsStream(CREDENTIAL_PATH) ?: throw IllegalStateException()

            return gson.fromJson(
                JsonReader(InputStreamReader(inputStream)),
                Credentials::class.java
            )
        }
    }
}