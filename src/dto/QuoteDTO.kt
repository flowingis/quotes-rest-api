package it.flowing.dto

import it.flowing.repositories.Quotes

class QuoteDTO(private val text: String = "", private val author: String = "", private val tags: List<String> = listOf()) {
    class Validation(val quote: Quotes.Quote?, val errors: List<String>)

    fun toQuote(): Validation{
        var list = listOf<String>()

        if(text.isEmpty()){
            list = list.plus("Text is mandatory")
        }

        if(author.isEmpty()) {
            list = list.plus("Author is mandatory")
        }

        if(tags.isEmpty()){
            list = list.plus("At least one tag is mandatory")
        }

        if(list.isEmpty()){
            val quote = Quotes.Quote(
                id = null,
                text = text,
                author = author,
                tags = tags
            );
            return Validation(quote,list)
        }

        return Validation(null,list)
    }
}