package it.flowing.dto

import it.flowing.repositories.Quotes

class QuoteDTO(val text: String?, val author: String?, val tags: List<String>?) {
    fun toQuote(): Quotes.Quote?{
        if(text == null){
            return null
        }

        if(author == null) {
            return null
        }

        if(tags == null){
            return null
        }

        if(tags.isEmpty()){
            return null
        }

        return Quotes.Quote(
            id = null,
            text = text,
            author = author,
            tags = tags
        )
    }
}