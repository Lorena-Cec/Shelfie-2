package com.example.shelfie.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class QuotesViewModel : ViewModel() {
    private val db = Firebase.firestore

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
                Log.e("QuotesViewModel", "Error fetching favorite quotes", e)
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
                            Log.d("QuotesViewModel", "Favorite quotes updated successfully!")
                            fetchFavoriteQuotes() // Opcionalno: osvježi listu nakon dodavanja citata
                        }
                        .addOnFailureListener { e ->
                            Log.e("QuotesViewModel", "Error updating favorite quotes", e)
                        }
                }
            }.addOnFailureListener { e ->
                Log.e("QuotesViewModel", "Error fetching user document", e)
            }
        }
    }

    fun removeQuote(quote: String, bookTitle: String, pageNumber: String) {
        Log.d("QuotesViewModel", "Removing quote")
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
                                Log.d("QuotesViewModel", "Quote successfully removed!")
                            }
                            .addOnFailureListener { e ->
                                Log.w("QuotesViewModel", "Error removing quote", e)
                            }
                    }
                }
            }
        }
    }
}
