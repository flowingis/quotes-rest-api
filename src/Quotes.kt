package it.flowing

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.MalformedJsonException
import java.lang.Exception
import java.lang.reflect.Type

class Quotes {
    class Quote(val id: Int, val text: String, val author: String, val tags: List<String>)

    private class TempQuote(val text: String, val author: String, val unparsedTags: String)

    private val sheets = Sheets()

    companion object {
        private const val SPREADSHEET_ID = "1cE183aiFTxAcz2zaIh-Jl9zsHSOBa9kenBZ5yv7k6as"
        private const val RANGE = "Foglio1!A2:D"
    }

    private fun parseTags(unparsedTags: String): List<String> {
        val regex = Regex("\\bname :\\s+(\\w+)",RegexOption.MULTILINE)
        return regex
            .findAll(unparsedTags)
            .toList()
            .map { m -> m.groupValues[1] }
    }

    fun list(): List<Quote> {
        val rows = sheets.getValues(
            SPREADSHEET_ID,
            RANGE
        )

        return rows
            .filter { l -> l.isNotEmpty()}
            .map{ l -> TempQuote(
                l[0].toString(),
                l[1].toString(),
                l[3].toString()
            ) }
            .mapIndexed() { i: Int, tempQuote: TempQuote -> Quote(i, tempQuote.text,tempQuote.author,parseTags(tempQuote.unparsedTags) )}
    }

    fun authors(): List<String> {
        return list()
            .map { quote -> quote.author }
            .distinct()
            .sorted()
    }

    fun tags(): List<String> {
        return list()
            .map { quote -> quote.tags }
            .flatten()
            .distinct()
            .sorted()
    }
}