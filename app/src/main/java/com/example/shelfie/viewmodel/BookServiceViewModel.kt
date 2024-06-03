package com.example.shelfie.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelfie.api.RetrofitClient
import com.example.shelfie.model.BookItem
import com.example.shelfie.model.BookSearchResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookServiceViewModel: ViewModel() {
    /*suspend fun getBookByISBN(isbn: String): BookItem? {
        val response = bookService.getBookByISBN(isbn)
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }*//*
    private val _bookResponse = MutableLiveData<BookItem>()
    fun getBookByISBN(query: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.getBookByISBN(isbn = query, apiKey = "AIzaSyAHMUBEm1_QCPLzS46-Z_knen7isgxqCvM")
            }
            if (response.isSuccessful) {
                _bookResponse.value = response.body()
            } else {
            }
        }
    }*/
}
