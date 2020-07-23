package it.flowing

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

    fun search(
        query: String,
        maxSize: Int,
        author: String,
        tag: String,
        count: Int
    ) : List<Quote> {
        return list()
            .filter { quote -> if(query.isNotEmpty()) quote.text.contains(query,true) else true }
            .filter { quote -> if(maxSize > 0) quote.text.length <= maxSize else true }
            .filter { quote -> if(author.isNotEmpty()) quote.author.equals(author,true) else true }
            .filter { quote -> if(tag.isNotEmpty()) quote.tags.any { t -> t.equals(tag,true) } else true }
            .take(count)
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
            .mapIndexed { i: Int, tempQuote: TempQuote ->
                Quote(
                    id = i,
                    text = tempQuote.text,
                    author = tempQuote.author,
                    tags = parseTags(tempQuote.unparsedTags)
                )
            }
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