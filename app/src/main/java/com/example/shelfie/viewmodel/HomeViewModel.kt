package com.example.shelfie.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelfie.R
import com.example.shelfie.api.RetrofitClient
import com.example.shelfie.model.BookSearchResponse
import com.example.shelfie.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {
    private val _fantasyBooks = MutableStateFlow<BookSearchResponse?>(null)
    val fantasyBooks: StateFlow<BookSearchResponse?> = _fantasyBooks

    private val _classicsBooks = MutableStateFlow<BookSearchResponse?>(null)
    val classicsBooks: StateFlow<BookSearchResponse?> = _classicsBooks

    private val _romanceBooks = MutableStateFlow<BookSearchResponse?>(null)
    val romanceBooks: StateFlow<BookSearchResponse?> = _romanceBooks
    private val apiKey = BuildConfig.API_KEY
    init {
        fetchBooks("fantasy", _fantasyBooks)
        fetchBooks("classics", _classicsBooks)
        fetchBooks("romance", _romanceBooks)
    }

    private fun fetchBooks(category: String, state: MutableStateFlow<BookSearchResponse?>) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.searchBooks(
                    query = "subject:$category",
                    apiKey = apiKey,
                    maxResults = 10
                )
            }
            if (response.isSuccessful) {
                val bookSearchResponse = response.body()
                if (bookSearchResponse != null && bookSearchResponse.items.isNotEmpty()) {
                    val filteredBooks = bookSearchResponse.items.filter { book ->
                        val industryIdentifiers = book.volumeInfo.industryIdentifiers
                        industryIdentifiers != null && industryIdentifiers.find { it.type == "ISBN_13" }?.identifier != null
                    }
                    val sortedBooks = filteredBooks.sortedByDescending { it.volumeInfo.averageRating }
                    state.value = bookSearchResponse.copy(items = sortedBooks)
                }
            } else {
                Log.e("HomeViewModel", "Error fetching $category books")
            }
        }
    }
}
