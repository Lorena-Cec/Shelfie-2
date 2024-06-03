package com.example.shelfie.model

// Book.kt
data class BookSearchResponse(
    val items: List<BookItem>
)

data class BookItem(
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String,
    val authors: List<String>,
    val imageLinks: ImageLinks?,
    val industryIdentifiers: List<IndustryIdentifier>,
    val pageCount: Int,
    val categories: List<String>?,
    val averageRating: Double?,
    val description: String
)

data class IndustryIdentifier(
    val type: String,
    val identifier: String
)

data class ImageLinks(
    val smallThumbnail: String,
    val thumbnail: String
)