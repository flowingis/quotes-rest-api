package it.flowing.repositories

class Quotes {
    data class Quote(val id: Int?, val text: String, val author: String, val tags: List<String>)

    private val sheets = Sheets()

    companion object {
        private const val START_ROW = 2
        private const val SPREADSHEET_ID = "1cE183aiFTxAcz2zaIh-Jl9zsHSOBa9kenBZ5yv7k6as"
        private const val RANGE = "Citazioni!A$START_ROW:M"
    }

    fun list(): List<Quote> {
        val rows = sheets.getValues(
            SPREADSHEET_ID,
            RANGE
        )

        return rows
            .filter { l -> l.isNotEmpty()}
            .map { l -> l.map { e -> e.toString() } }
            .mapIndexed { i: Int, row ->
                Quote(
                    id = START_ROW + i,
                    text = row[0],
                    author = row[1],
                    tags = row.drop(2)
                )
            }
    }

    fun find(id: Int): Quote? {
        check(id > 0)
        return list().find { quote -> quote.id == id }
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

    fun random(query: String, maxSize: Int, author: String, tag: String): Quote {
        return search(
            query = query,
            maxSize = maxSize,
            author = author,
            tag = tag,
            count = Int.MAX_VALUE
        ).random()
    }

    private fun nextId(): Int {
        val lastId = list()
            .map { q -> q.id }
            .filterNotNull()
            .max() ?: 0

        return lastId + 1;
    }

    fun insert(q: Quote): Quote {
        val id = nextId()

        val row = listOf(listOf(q.text,q.author),q.tags).flatten()
        var range = "Citazioni!A$id"

        sheets.appendValues(
            SPREADSHEET_ID,
            range,
            listOf(row)
        )

        return q.copy(id = id)
    }
}