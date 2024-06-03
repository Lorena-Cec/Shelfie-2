package com.example.shelfie.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class BooksViewModel : ViewModel() {
    private val api = RetrofitClient.apiService
    var books: List<VolumeInfo> by mutableStateOf(emptyList())
    var errorMessage: String by mutableStateOf("")
    private val _bookResponse = MutableLiveData<BookSearchResponse>()

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

    /*fun getBookByISBN(isbn: String?): BookItem? {
        if (isbn.isNullOrEmpty()) {
            return null
        }
        val bookResponse = _bookResponse.value
        return bookResponse?.items?.find { item ->
            item.volumeInfo.industryIdentifiers.any { it.identifier == isbn }
        }
    }*/

    private val db = Firebase.firestore
    private suspend fun fetchBooksFromFirestore(userId: String, onBooksFetched: (List<BookItem>, List<BookItem>, List<BookItem>, List<BookItem>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        try {
            val document = db.collection("users").document(userId).get().await()
            if (document.exists()) {
                val currentlyReading = document.get("CurrentlyReading") as? List<Map<String, Any>> ?: emptyList()
                val read = document.get("Read") as? List<Map<String, Any>> ?: emptyList()
                val toBeRead = document.get("ToBeRead") as? List<Map<String, Any>> ?: emptyList()
                val myPhysicalBooks = document.get("MyPhysicalBooks") as? List<Map<String, Any>> ?: emptyList()

                val currentlyReadingBooks = currentlyReading.map { it.toBookItem() }
                val readBooks = read.map { it.toBookItem() }
                val toBeReadBooks = toBeRead.map { it.toBookItem() }
                val myPhysicalBooksList = myPhysicalBooks.map { it.toBookItem() }

                onBooksFetched(currentlyReadingBooks, readBooks, toBeReadBooks, myPhysicalBooksList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun fetchBooks(userId: String) {
        viewModelScope.launch {
            fetchBooksFromFirestore(userId) { currentlyReadingBooks, readBooks, toBeReadBooks, myPhysicalBooksList ->
                _currentlyReading.addAll(currentlyReadingBooks)
                _booksRead.addAll(readBooks)
                _booksToRead.addAll(toBeReadBooks)
                _myPhysicalBooks.addAll(myPhysicalBooksList)
            }
        }
    }

    private fun Map<String, Any>.toBookItem(): BookItem {
        val volumeInfoMap = this["volumeInfo"] as? Map<String, Any> ?: return BookItem(VolumeInfo("", listOf(), null, listOf(), 0, null, null,""))
        val title = volumeInfoMap["title"] as? String ?: ""
        val authors = volumeInfoMap["authors"] as? List<String> ?: listOf()
        val imageLinksMap = volumeInfoMap["imageLinks"] as? Map<String, String>
        val imageLinks = imageLinksMap?.let { ImageLinks(it["smallThumbnail"] ?: "", it["thumbnail"] ?: "") }
        val industryIdentifiersList = volumeInfoMap["industryIdentifiers"] as? List<Map<String, String>>
        val industryIdentifiers = industryIdentifiersList?.map { IndustryIdentifier(it["type"] ?: "", it["identifier"] ?: "") } ?: listOf()
        val pageCount = volumeInfoMap["pageCount"] as? Int ?: 0
        val categories = volumeInfoMap["categories"] as? List<String>
        val averageRating = volumeInfoMap["averageRating"] as? Double
        val description = volumeInfoMap["description"]  as? String ?: ""

        return BookItem(
            volumeInfo = VolumeInfo(
                title = title,
                authors = authors,
                imageLinks = imageLinks,
                industryIdentifiers = industryIdentifiers,
                pageCount = pageCount,
                categories = categories,
                averageRating = averageRating,
                description = description
            )
        )
    }

    // Dodajte ostale kategorije knjiga ako su potrebne

    fun addBookToCategory(book: BookItem, category: String) {
        when (category) {
            "Read" -> {
                _booksRead.add(book)
                updateFirestoreCategory(category, book)
            }
            "ToBeRead" -> {
                _booksToRead.add(book)
                updateFirestoreCategory(category, book)

            }
            "MyPhysicalBooks" -> {
                _myPhysicalBooks.add(book)
                updateFirestoreCategory(category, book)

            }
            "CurrentlyReading" -> {
                _currentlyReading.add(book)
                updateFirestoreCategory(category, book)

            }
            else -> throw IllegalArgumentException("Unsupported category: $category")
        }
    }

    private fun updateFirestoreCategory(category: String, book: BookItem) {
        val currentUser = Firebase.auth.currentUser
        currentUser?.let {
            val userRef = db.collection("users").document(it.uid)
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val currentBooks = document.get(category) as? List<Map<String, Any>> ?: emptyList()
                    val updatedBooks = currentBooks.toMutableList().apply {
                        add(book.toMap())
                    }
                    userRef.update(category, updatedBooks)
                        .addOnSuccessListener {
                            Log.d(TAG, "DocumentSnapshot successfully updated!")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error updating document", e)
                        }
                }
            }.addOnFailureListener { e ->
                Log.w(TAG, "Error getting document", e)
            }
        }
    }


    private fun BookItem.toMap(): Map<String, Any> {
        val volumeInfoMap = mutableMapOf<String, Any>(
            "title" to volumeInfo.title,
            "authors" to volumeInfo.authors,
            "pageCount" to volumeInfo.pageCount,
            "industryIdentifiers" to volumeInfo.industryIdentifiers.map {
                mapOf("type" to it.type, "identifier" to it.identifier)
            }
        )
        volumeInfo.imageLinks?.let {
            volumeInfoMap["imageLinks"] = mapOf(
                "smallThumbnail" to it.smallThumbnail,
                "thumbnail" to it.thumbnail
            )
        }
        volumeInfo.categories?.let {
            volumeInfoMap["categories"] = it
        }
        volumeInfo.averageRating?.let {
            volumeInfoMap["averageRating"] = it
        }

        return mapOf("volumeInfo" to volumeInfoMap)
    }
}

