package com.example.shelfie.viewmodel

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class BooksViewModel : ViewModel() {
    var books: List<VolumeInfo> by mutableStateOf(emptyList())
    private val _bookResponse = MutableLiveData<BookSearchResponse>()

    private val _currentlyReading = MutableStateFlow<List<BookItem>>(emptyList())
    val currentlyReading: StateFlow<List<BookItem>> = _currentlyReading

    private val _booksRead = MutableStateFlow<List<BookItem>>(emptyList())
    val booksRead: StateFlow<List<BookItem>> = _booksRead

    private val _booksToRead = MutableStateFlow<List<BookItem>>(emptyList())
    val booksToRead: StateFlow<List<BookItem>> = _booksToRead

    private val _myPhysicalBooks = MutableStateFlow<List<BookItem>>(emptyList())
    val myPhysicalBooks: StateFlow<List<BookItem>> = _myPhysicalBooks

    private val _favorites = MutableStateFlow<List<BookItem>>(emptyList())
    val favorites: StateFlow<List<BookItem>> = _favorites

    val bookResponse: LiveData<BookSearchResponse>
        get() = _bookResponse
    fun searchBooks(query: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.searchBooks(query = query, apiKey = "AIzaSyAHMUBEm1_QCPLzS46-Z_knen7isgxqCvM", maxResults = 10)
            }
            if (response.isSuccessful) {
                val bookResponse = response.body()
                val filteredBooks = bookResponse?.items?.filter { book ->
                    val industryIdentifiers = book.volumeInfo.industryIdentifiers
                    industryIdentifiers.any { it.type == "ISBN_13" }
                }
                _bookResponse.value = bookResponse?.copy(items = filteredBooks ?: emptyList())
            } else {
                Log.d(TAG, "Error updating document")
            }
        }
    }



    private val db = Firebase.firestore
    private suspend fun fetchBooksFromFirestore(userId: String, onBooksFetched: (List<BookItem>, List<BookItem>, List<BookItem>, List<BookItem>) -> Unit) {
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
            Log.e("BooksViewModel", "Error fetching books from Firestore", e)
        }
    }

    fun fetchBooks() {
        val currentUser = Firebase.auth.currentUser
        currentUser?.let { user ->
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    fetchBooksFromFirestore(user.uid) { currentlyReadingBooks, readBooks, toBeReadBooks, myPhysicalBooksList ->
                        _currentlyReading.value = currentlyReadingBooks
                        _booksRead.value = readBooks
                        _booksToRead.value = toBeReadBooks
                        _myPhysicalBooks.value = myPhysicalBooksList
                    }
                }
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

    fun addBookToCategory(book: BookItem, category: String) {
        when (category) {
            "Read" -> {
                _booksRead.value += book
                updateFirestoreCategory(category, book)
            }
            "ToBeRead" -> {
                _booksToRead.value += book
                updateFirestoreCategory(category, book)
            }
            "MyPhysicalBooks" -> {
                _myPhysicalBooks.value += book
                updateFirestoreCategory(category, book)
            }
            "CurrentlyReading" -> {
                _currentlyReading.value += book
                updateFirestoreCategory(category, book)
            }
            else -> throw IllegalArgumentException("Unsupported category: $category")
        }
    }

        fun removeBookFromCategory(book: BookItem, category: String) {
            when (category) {
                "Read" -> {
                    _booksRead.value -= book
                    updateFirestoreCategoryRemove(category, book)
                }
                "ToBeRead" -> {
                    _booksToRead.value -= book
                    updateFirestoreCategoryRemove(category, book)
                }
                "MyPhysicalBooks" -> {
                    _myPhysicalBooks.value -= book
                    updateFirestoreCategoryRemove(category, book)
                }
                "CurrentlyReading" -> {
                    _currentlyReading.value -= book
                    updateFirestoreCategoryRemove(category, book)
                }
                else -> throw IllegalArgumentException("Unsupported category: $category")
            }
        }

        private fun updateRemoveFirestore(category: String, updatedBooks: List<BookItem>) {
            val currentUser = Firebase.auth.currentUser
            currentUser?.let { user ->
                val userRef = db.collection("users").document(user.uid)
                userRef.update(category, updatedBooks.map { it.toMap() })
                    .addOnSuccessListener {
                        Log.d(TAG, "Category updated successfully in Firestore")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error updating category in Firestore", e)
                    }
            }
        }

    private fun updateFirestoreCategoryRemove(category: String, book: BookItem?) {
        val currentUser = Firebase.auth.currentUser
        currentUser?.let {
            val userRef = db.collection("users").document(it.uid)
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val currentBooks = document.get(category) as? List<Map<String, Any>> ?: emptyList()
                    val updatedBooks = currentBooks.toMutableList()

                    if (book != null) {
                        updatedBooks.removeIf { map ->
                            val title = (map["volumeInfo"] as? Map<*, *>)?.get("title") as? String
                            Log.d(TAG, "Books $title")
                            title == book.volumeInfo.title
                        }
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

    private val _favoriteQuotes = MutableStateFlow<List<Map<String, String>>>(emptyList())
    val favoriteQuotes: StateFlow<List<Map<String, String>>> get() = _favoriteQuotes

    fun fetchFavoriteQuotes() {
        val currentUser = Firebase.auth.currentUser
        currentUser?.let { user ->
            val userRef = db.collection("users").document(user.uid)
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val quotes = document.get("favoriteQuotes") as? List<Map<String, String>> ?: emptyList()
                    _favoriteQuotes.value = quotes
                }
            }.addOnFailureListener { e ->
                Log.e(TAG, "Error fetching favorite quotes", e)
            }
        }
    }

    fun addFavoriteQuote(quote: String, bookTitle: String, pageNumber: String) {
        val currentUser = Firebase.auth.currentUser
        currentUser?.let { user ->
            val userRef = db.collection("users").document(user.uid)
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val currentQuotes = document.get("favoriteQuotes") as? List<Map<String, String>> ?: emptyList()
                    val newQuote = mapOf(
                        "quote" to quote,
                        "bookTitle" to bookTitle,
                        "pageNumber" to pageNumber
                    )
                    val updatedQuotes = currentQuotes.toMutableList().apply { add(newQuote) }
                    userRef.update("favoriteQuotes", updatedQuotes)
                        .addOnSuccessListener {
                            Log.d(TAG, "Favorite quotes updated successfully!")
                            fetchFavoriteQuotes() // Opcionalno: osvježi listu nakon dodavanja citata
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error updating favorite quotes", e)
                        }
                }
            }.addOnFailureListener { e ->
                Log.e(TAG, "Error fetching user document", e)
            }
        }
    }

    fun removeQuote(quote: String, bookTitle: String, pageNumber: String) {
        Log.d("","Usao")
        val currentUser = Firebase.auth.currentUser
        currentUser?.let { user ->
            val userRef = db.collection("users").document(user.uid)
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val quotes = document.get("favoriteQuotes") as? MutableList<Map<String, String>>
                    quotes?.let {
                        val quoteToRemove = mapOf(
                            "quote" to quote,
                            "bookTitle" to bookTitle,
                            "pageNumber" to pageNumber
                        )
                        it.remove(quoteToRemove)
                        userRef.update("favoriteQuotes", it)
                            .addOnSuccessListener {
                                Log.d(TAG, "Quote successfully removed!")
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error removing quote", e)
                            }
                    }
                }
            }
        }
    }


    fun addToFavorites(book: BookItem) {
        val updatedFavorites = _favorites.value.toMutableList()
        updatedFavorites.add(book)
        _favorites.value = updatedFavorites
        updateFirestoreFavorites()
    }

    fun removeFromFavorites(book: BookItem) {
        val updatedFavorites = _favorites.value.filter { it.volumeInfo.title != book.volumeInfo.title }
        _favorites.value = updatedFavorites
        updateFirestoreFavorite(updatedFavorites)
    }

    fun fetchFavorites() {
        val currentUser = Firebase.auth.currentUser
        currentUser?.let { user ->
            val userRef = db.collection("users").document(user.uid)
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val favorites = document.get("Favorites") as? List<Map<String, Any>> ?: emptyList()
                    _favorites.value = favorites.map { it.toBookItem() }
                }
            }.addOnFailureListener { e ->
                // Handle failure
            }
        }
    }
    private fun updateFirestoreFavorite(updatedFavorites: List<BookItem>) {
        val currentUser = Firebase.auth.currentUser
        currentUser?.let { user ->
            val userRef = db.collection("users").document(user.uid)
            userRef.update("Favorites", updatedFavorites.map { it.toMap() })
                .addOnSuccessListener {
                    Log.d(TAG, "Favorites updated successfully in Firestore")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error updating favorites in Firestore", e)
                }
        }
    }

    private fun updateFirestoreFavorites() {
        val currentUser = Firebase.auth.currentUser
        currentUser?.let { user ->
            val userRef = db.collection("users").document(user.uid)
            userRef.update("Favorites", _favorites.value.map { it.toMap() })
                .addOnSuccessListener {
                    // Handle success
                }
                .addOnFailureListener { e ->
                    // Handle failure
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

