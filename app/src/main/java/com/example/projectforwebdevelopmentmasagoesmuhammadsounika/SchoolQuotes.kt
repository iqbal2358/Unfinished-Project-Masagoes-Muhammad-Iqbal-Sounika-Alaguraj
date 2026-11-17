package com.example.projectforwebdevelopmentmasagoesmuhammadsounika

import kotlinx.coroutines.delay

object SchoolQuotes {
    // TODO [Part B]: Define a list of string items (campus tips, student quotes, etc.)
    private val quotes = listOf(
        // Add your campus quotes here
    )

    suspend fun fetchRandomQuote(): String {
        // TODO [Part B]: Add a small delay and return a random quote from the list
        return "Default Campus Tip"
    }
}
