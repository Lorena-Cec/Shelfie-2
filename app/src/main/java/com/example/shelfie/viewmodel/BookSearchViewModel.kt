package com.example.shelfie.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelfie.BuildConfig
import com.example.shelfie.R
import com.example.shelfie.api.RetrofitClient
import com.example.shelfie.model.BookSearchResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookSearchViewModel : ViewModel() {
    private val _bookResponse = MutableLiveData<BookSearchResponse?>()
    val bookResponse: MutableLiveData<BookSearchResponse?>
        get() = _bookResponse
    private val apiKey = BuildConfig.API_KEY
    fun searchBooks(query: String, context: Context) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.searchBooks(query = query, apiKey = apiKey, maxResults = 10)
            }
            if (response.isSuccessful) {
                val bookResponse = response.body()
                if (bookResponse?.items == null) {
                    Toast.makeText(context, "No books found", Toast.LENGTH_SHORT).show()
                }
                val filteredBooks = bookResponse?.items?.filter { book ->
                    val industryIdentifiers = book.volumeInfo.industryIdentifiers
                    industryIdentifiers?.any { it.type == "ISBN_13" } == true
                }
                if (filteredBooks != null) {
                    _bookResponse.value = bookResponse.copy(items = filteredBooks)
                } else {
                    _bookResponse.value = bookResponse?.copy(items = emptyList())
                }
            } else {
                Log.d("BookSearchViewModel", "Error searching books")
                Toast.makeText(context, "No books found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun clearBookResponse() {
        _bookResponse.value = null
    }
}
