package com.example.shelfie.viewmodel

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelfie.R
import com.example.shelfie.model.BookItem
import com.example.shelfie.model.ImageLinks
import com.example.shelfie.model.IndustryIdentifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import  com.example.shelfie.model.VolumeInfo
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class BookFirebaseViewModel : ViewModel() {
    private val db = Firebase.firestore

    private val _currentlyReading = MutableStateFlow<List<BookItem>>(emptyList())
    val currentlyReading: StateFlow<List<BookItem>> = _currentlyReading

    private val _booksRead = MutableStateFlow<List<BookItem>>(emptyList())
    val booksRead: StateFlow<List<BookItem>> = _booksRead

    private val _booksToRead = MutableStateFlow<List<BookItem>>(emptyList())
    val booksToRead: StateFlow<List<BookItem>> = _booksToRead

    private val _myPhysicalBooks = MutableStateFlow<List<BookItem>>(emptyList())
    val myPhysicalBooks: StateFlow<List<BookItem>> = _myPhysicalBooks

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
            Log.e("FirestoreBooksViewModel", "Error fetching books from Firestore", e)
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

    fun addBookToCategory(book: BookItem, category: String, context: Context) {
        val currentUser = Firebase.auth.currentUser
        currentUser?.let { user ->
            viewModelScope.launch {
                fetchBooksFromFirestore(user.uid) { currentlyReadingBooks, readBooks, toBeReadBooks, myPhysicalBooksList ->
                    val bookAlreadyInCategory = when (category) {
                        "Read" -> readBooks.contains(book)
                        "ToBeRead" -> toBeReadBooks.contains(book)
                        "MyPhysicalBooks" -> myPhysicalBooksList.contains(book)
                        "CurrentlyReading" -> currentlyReadingBooks.contains(book)
                        else -> throw IllegalArgumentException("Unsupported category: $category")
                    }
                    if (bookAlreadyInCategory) {
                        Toast.makeText(context, "Book already in $category", Toast.LENGTH_SHORT).show()
                    } else {
                        when (category) {
                            "Read" -> {
                                _booksRead.value += book
                                updateFirestoreCategory(category, book)
                                Toast.makeText(context, "Added book to Read Books", Toast.LENGTH_SHORT).show()
                            }
                            "ToBeRead" -> {
                                _booksToRead.value += book
                                updateFirestoreCategory(category, book)
                                Toast.makeText(context, "Added book to To Be Read Books", Toast.LENGTH_SHORT).show()
                            }
                            "MyPhysicalBooks" -> {
                                _myPhysicalBooks.value += book
                                updateFirestoreCategory(category, book)
                                Toast.makeText(context, "Added book to My Physical Books", Toast.LENGTH_SHORT).show()
                            }
                            "CurrentlyReading" -> {
                                _currentlyReading.value += book
                                updateFirestoreCategory(category, book)
                                Toast.makeText(context, "Added book to Currently Reading Books", Toast.LENGTH_SHORT).show()
                            }
                            else -> throw IllegalArgumentException("Unsupported category: $category")
                        }
                    }
                }
            }
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
                            Log.d("FirestoreBooksViewModel", "DocumentSnapshot successfully updated!")
                        }
                        .addOnFailureListener { e ->
                            Log.w("FirestoreBooksViewModel", "Error updating document", e)
                        }
                }
            }.addOnFailureListener { e ->
                Log.w("FirestoreBooksViewModel", "Error getting document", e)
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
                            Log.d("FirestoreBooksViewModel", "Books $title")
                            title == book.volumeInfo.title
                        }
                    }

                    userRef.update(category, updatedBooks)
                        .addOnSuccessListener {
                            Log.d("FirestoreBooksViewModel", "DocumentSnapshot successfully updated!")
                        }
                        .addOnFailureListener { e ->
                            Log.w("FirestoreBooksViewModel", "Error updating document", e)
                        }
                }
            }.addOnFailureListener { e ->
                Log.w("FirestoreBooksViewModel", "Error getting document", e)
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

