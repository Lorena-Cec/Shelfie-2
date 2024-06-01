package com.example.shelfie.model

data class Book(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val description: String = "",
    val coverURL: String = "",
    val status: BookStatus = BookStatus.TO_READ,
)

enum class BookStatus{
    TO_READ,
    READING,
    READ
}