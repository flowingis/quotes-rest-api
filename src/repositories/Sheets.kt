package it.flowing.repositories

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import java.io.FileNotFoundException
import java.io.IOException
import java.security.GeneralSecurityException

class Sheets {

    private val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()
    private val SCOPES = listOf(SheetsScopes.SPREADSHEETS)
    private val CREDENTIALS_FILE_PATH = "/service.json"
    private val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
    private val service = Sheets
        .Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
        .setApplicationName("Application Name")
        .build()


    @Throws(IOException::class)
    private fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential {
        val fileStream = Sheets::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
            ?: throw FileNotFoundException("Resource not found: $CREDENTIALS_FILE_PATH")
        return GoogleCredential.fromStream(fileStream).createScoped(SCOPES)
    }

    @Throws(GeneralSecurityException::class, IOException::class)
    fun getValues(
        spreadsheetId: String,
        range: String
    ): List<List<Any>> {
        val response = service.spreadsheets().values()[spreadsheetId, range]
            .execute()

        return response.getValues()
    }

    fun writeValues(
        spreadsheetId: String,
        range: String,
        rows: List<List<String>>
    ) {
        val body: ValueRange = ValueRange().setValues(rows)
        service
            .spreadsheets()
            .values()
            .update(spreadsheetId, range, body)
            .setValueInputOption("USER_ENTERED")
            .execute()
    }

    fun appendValues(
        spreadsheetId: String,
        range: String,
        rows: List<List<String>>
    ) {
        val body: ValueRange = ValueRange().setValues(rows)
        service
            .spreadsheets()
            .values()
            .append(spreadsheetId, range, body)
            .setValueInputOption("USER_ENTERED")
            .execute()
    }
}