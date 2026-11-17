package com.example.projectforwebdevelopmentmasagoesmuhammadsounika

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CartItem(
    val event: Event,
    val quantity: Int
)

class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // Secondary text state (satisfies Prof Bâton's hint without using Retrofit)
    private val _campusTip = MutableStateFlow("Welcome to UniEvents! Explore campus activities below.")
    val campusTip: StateFlow<String> = _campusTip.asStateFlow()

    private val seatReductions = mutableMapOf<String, Int>()

    init {
        fetchData()
    }

    fun addToCart(event: Event, quantity: Int) {
        _cartItems.update { current ->
            val index = current.indexOfFirst { it.event.id == event.id }
            if (index >= 0) {
                current.toMutableList().apply {
                    val existing = this[index]
                    this[index] = existing.copy(quantity = existing.quantity + quantity)
                }
            } else {
                current + CartItem(event, quantity)
            }
        }
    }

    fun updateCartQuantity(event: Event, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(event)
        } else {
            _cartItems.update { current ->
                current.map {
                    if (it.event.id == event.id) it.copy(quantity = newQuantity) else it
                }
            }
        }
    }

    fun removeFromCart(event: Event) {
        _cartItems.update { current ->
            current.filterNot { it.event.id == event.id }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun checkoutCart() {
        val currentEvents = _events.value
        val updatedEvents = currentEvents.map { event ->
            val cartMatch = _cartItems.value.find { it.event.id == event.id }
            if (cartMatch != null) {
                val newSeats = maxOf(0, event.availableSeats - cartMatch.quantity)
                seatReductions[event.id] = (seatReductions[event.id] ?: 0) + cartMatch.quantity
                event.copy(availableSeats = newSeats)
            } else {
                event
            }
        }
        _events.value = updatedEvents
        clearCart()
    }

    fun fetchData() {        viewModelScope.launch {
            try {
                // TODO [Part A]: Use RetrofitClient to fetch the events list from the API service
                // Example: val eventsList = RetrofitClient.apiService.getEvents()
                // _events.value = eventsList
                
                // TODO [Part B]: Call your non-Retrofit text fetching method to update _campusTip.value
                // Example: _campusTip.value = SchoolQuotes.fetchRandomQuote()

            } catch (_: Exception) {
                _events.value = emptyList()
                _campusTip.value = "Unable to load campus bulletin at this time."
            }
        }
    }
}
