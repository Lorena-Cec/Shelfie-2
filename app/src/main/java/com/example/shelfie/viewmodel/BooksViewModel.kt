package com.example.shelfie.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.shelfie.api.RetrofitClient
import com.example.shelfie.api.GoogleBooksApi
import com.example.shelfie.model.BookItem
import com.example.shelfie.model.BookSearchResponse
import com.example.shelfie.model.ImageLinks
import com.example.shelfie.model.IndustryIdentifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import  com.example.shelfie.model.VolumeInfo
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore

class BooksViewModel : ViewModel() {
    private val api = RetrofitClient.apiService
    var books: List<VolumeInfo> by mutableStateOf(emptyList())
    var errorMessage: String by mutableStateOf("")
    private val _bookResponse = MutableLiveData<BookSearchResponse>()

    private val _allBooks = mutableStateListOf<BookItem>()
    val allBooks: List<BookItem> = _allBooks

    private val _booksRead = mutableStateListOf<BookItem>()
    val booksRead: List<BookItem> = _booksRead

    private val _booksToRead = mutableStateListOf<BookItem>()
    val booksToRead: List<BookItem> = _booksToRead

    private val _myPhysicalBooks = mutableStateListOf<BookItem>()
    val myPhysicalBooks: List<BookItem> = _myPhysicalBooks

    private val _currentlyReading = mutableStateListOf<BookItem>()
    val currentlyReading: List<BookItem> = _currentlyReading

    val bookResponse: LiveData<BookSearchResponse>
        get() = _bookResponse
    fun searchBooks(query: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.searchBooks(query = query, apiKey = "AIzaSyAHMUBEm1_QCPLzS46-Z_knen7isgxqCvM", maxResults = 10)
            }
            if (response.isSuccessful) {
                _bookResponse.value = response.body()
            } else {
                // Obrada neuspješnog zahtjeva
            }
        }
    }
    fun randomBooks(query: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.searchBooks(query = query, apiKey = "AIzaSyAHMUBEm1_QCPLzS46-Z_knen7isgxqCvM", maxResults = 20)
            }
            if (response.isSuccessful) {
                _bookResponse.value = response.body()
            } else {
                // Obrada neuspješnog zahtjeva
            }
        }
    }

    private val db = Firebase.firestore



    // Dodajte ostale kategorije knjiga ako su potrebne

    fun addBookToCategory(book: BookItem, category: String) {
        when (category) {
            "Read" -> {
                _booksRead.add(book)
                _allBooks.add(book)
                updateFirestoreCategory(category, _booksRead)
            }
            "ToBeRead" -> {
                _booksToRead.add(book)
                _allBooks.add(book)
                updateFirestoreCategory(category, _booksToRead)
            }
            "MyPhysicalBooks" -> {
                _myPhysicalBooks.add(book)
                _allBooks.add(book)
                updateFirestoreCategory(category, _myPhysicalBooks)
            }
            "CurrentlyReading" -> {
                _currentlyReading.add(book)
                _allBooks.add(book)
                updateFirestoreCategory(category, _currentlyReading)
            }
            else -> throw IllegalArgumentException("Unsupported category: $category")
        }
    }

    private fun updateFirestoreCategory(category: String, books: List<BookItem>) {
        val currentUser = Firebase.auth.currentUser
        currentUser?.let {
            val userRef = db.collection("users").document(it.uid)
            userRef.update(category, books)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully updated!")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error updating document", e)
                }
        }
    }
}

