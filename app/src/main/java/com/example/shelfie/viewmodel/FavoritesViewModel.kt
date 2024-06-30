package com.example.shelfie.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelfie.model.BookItem
import com.example.shelfie.model.ImageLinks
import com.example.shelfie.model.IndustryIdentifier
import kotlinx.coroutines.launch
import  com.example.shelfie.model.VolumeInfo
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class FavoritesViewModel : ViewModel() {
    private val db = Firebase.firestore

    private val _favorites = MutableStateFlow<List<BookItem>>(emptyList())
    val favorites: StateFlow<List<BookItem>> = _favorites

    fun fetchFavorites() {
        val currentUser = Firebase.auth.currentUser
        currentUser?.let { user ->
            viewModelScope.launch {
                try {
                    val document = db.collection("users").document(user.uid).get().await()
                    if (document.exists()) {
                        val favorites = document.get("Favorites") as? List<Map<String, Any>> ?: emptyList()
                        _favorites.value = favorites.map { it.toBookItem() }
                    }
                } catch (e: Exception) {
                    Log.e("FavoritesViewModel", "Error fetching favorites", e)
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
        updateFirestoreFavorites()
    }

    private fun updateFirestoreFavorites() {
        val currentUser = Firebase.auth.currentUser
        currentUser?.let { user ->
            val userRef = db.collection("users").document(user.uid)
            userRef.update("Favorites", _favorites.value.map { it.toMap() })
                .addOnSuccessListener {
                    Log.d("FavoritesViewModel", "Favorites updated successfully in Firestore")
                }
                .addOnFailureListener { e ->
                    Log.e("FavoritesViewModel", "Error updating favorites in Firestore", e)
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
