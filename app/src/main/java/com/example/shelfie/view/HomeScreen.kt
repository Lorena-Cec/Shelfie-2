package com.example.shelfie.view

import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shelfie.R
import com.example.shelfie.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.shelfie.model.BookSearchResponse
import com.example.shelfie.view.BookDetailsScreen
import com.example.shelfie.viewmodel.BooksViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
        // Ostatak vašeg koda...
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                LogoImage()
            }
            Text(
                modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 8.dp),
                text = "Fantasy",
                textAlign = TextAlign.Right,
                fontSize = 20.sp
            )

            var bookSearchResponse by remember { mutableStateOf<BookSearchResponse?>(null) }
            LaunchedEffect(Unit) {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.searchBooks(query = "subject:fantasy", apiKey = "AIzaSyAHMUBEm1_QCPLzS46-Z_knen7isgxqCvM", maxResults = 10)
                }
                if (response.isSuccessful) {
                    bookSearchResponse = response.body()
                    if (bookSearchResponse != null && bookSearchResponse!!.items.isNotEmpty()) {
                        val filteredBooks = bookSearchResponse!!.items.filter { book ->
                            val industryIdentifiers = book.volumeInfo.industryIdentifiers
                            industryIdentifiers != null && industryIdentifiers.find { it.type == "ISBN_13" }?.identifier != null
                        }
                        val sortedBooks = bookSearchResponse!!.items.sortedByDescending { it.volumeInfo.averageRating }
                        for (book in sortedBooks) {
                            val authors = book.volumeInfo.authors
                            val authorsText = if (authors != null && authors.isNotEmpty()) {
                                authors.joinToString(", ")
                            } else {
                                "Unknown"
                            }
                        }
                        bookSearchResponse = bookSearchResponse!!.copy(items = filteredBooks)
                    }
                } else {
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            if (bookSearchResponse != null && bookSearchResponse!!.items.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(10.dp, 0.dp)
                ) {
                    items(bookSearchResponse!!.items) { book ->
                        val isbn13Identifier = book.volumeInfo.industryIdentifiers.find { it.type == "ISBN_13" }
                        val isbn13 = isbn13Identifier?.identifier
                        Surface(
                            tonalElevation = 3.dp,
                            modifier = Modifier
                                .width(150.dp)
                                .height(200.dp)
                                .padding(5.dp, 0.dp)
                                .clickable {
                                    navController.navigate("bookDetails/${isbn13}")
                                }
                        ) {
                            // Prikaz naslovnice knjige
                            if (book.volumeInfo.imageLinks != null) {
                                val url: StringBuilder = StringBuilder(book.volumeInfo.imageLinks?.thumbnail)
                                url.insert(4, "s")
                                AsyncImage(
                                    model = url.toString(),
                                    contentDescription = book.volumeInfo.title,
                                    modifier = Modifier.size(150.dp),
                                    contentScale = ContentScale.Crop,
                                    error = painterResource(id = R.drawable.cover),
                                )
                            } else {
                                Box(
                                    modifier = Modifier.size(150.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No Image Available", textAlign = TextAlign.Center)
                                }
                                Toast.makeText(context, "No image", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Classics",
                textAlign = TextAlign.Right,
                fontSize = 20.sp
            )
            var bookClassicsResponse by remember { mutableStateOf<BookSearchResponse?>(null) }
            LaunchedEffect(Unit) {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.searchBooks(query = "subject:classics", apiKey = "AIzaSyAHMUBEm1_QCPLzS46-Z_knen7isgxqCvM", maxResults = 15)
                }
                if (response.isSuccessful) {
                    bookClassicsResponse = response.body()
                    if (bookClassicsResponse != null && bookClassicsResponse!!.items.isNotEmpty()) {
                        val filteredBooks = bookClassicsResponse!!.items.filter { book ->
                            val industryIdentifiers = book.volumeInfo.industryIdentifiers
                            industryIdentifiers != null && industryIdentifiers.find { it.type == "ISBN_13" }?.identifier != null
                        }
                        val sortedBooks = bookClassicsResponse!!.items.sortedByDescending { it.volumeInfo.averageRating }
                        for (book in sortedBooks) {
                            val authors = book.volumeInfo.authors
                            val authorsText = if (authors != null && authors.isNotEmpty()) {
                                authors.joinToString(", ")
                            } else {
                                "Unknown"
                            }
                        }
                        bookClassicsResponse = bookClassicsResponse!!.copy(items = filteredBooks)
                    }
                } else {
                    // Obrada neuspješnog zahtjeva
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            if (bookClassicsResponse?.items != null && bookClassicsResponse!!.items.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(10.dp, 0.dp)
                ) {
                    items(bookClassicsResponse!!.items) { book ->
                        val isbn13Identifier = book.volumeInfo.industryIdentifiers.find { it.type == "ISBN_13" }
                        val isbn13 = isbn13Identifier?.identifier
                        Surface(
                            tonalElevation = 3.dp,
                            modifier = Modifier
                                .width(150.dp) // Postavljanje širine slike na 150 dp
                                .height(200.dp)
                                .padding(5.dp, 0.dp)
                                .clickable {
                                navController.navigate("bookDetails/${isbn13}")
                            }
                        ) {
                            if (book.volumeInfo.imageLinks != null) {
                                val url: StringBuilder = StringBuilder(book.volumeInfo.imageLinks?.thumbnail)
                                url.insert(4, "s")
                                AsyncImage(
                                    model = url.toString(),
                                    contentDescription = book.volumeInfo.title,
                                    modifier = Modifier.size(150.dp),
                                    contentScale = ContentScale.Crop,
                                    error = painterResource(id = R.drawable.cover),
                                )
                            } else {
                                Box(
                                    modifier = Modifier.size(150.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No Image Available", textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                }
            }
            var bookRomanceResponse by remember { mutableStateOf<BookSearchResponse?>(null) }
            LaunchedEffect(Unit) {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.searchBooks(query = "subject:romance", apiKey = "AIzaSyAHMUBEm1_QCPLzS46-Z_knen7isgxqCvM", maxResults = 10)
                }
                if (response.isSuccessful) {
                    bookRomanceResponse = response.body()
                    if (bookRomanceResponse != null && bookRomanceResponse!!.items.isNotEmpty()) {
                        val filteredBooks = bookRomanceResponse!!.items.filter { book ->
                            val industryIdentifiers = book.volumeInfo.industryIdentifiers
                            industryIdentifiers != null && industryIdentifiers.find { it.type == "ISBN_13" }?.identifier != null
                        }
                        val sortedBooks = bookRomanceResponse!!.items.sortedByDescending { it.volumeInfo.averageRating }
                        for (book in sortedBooks) {
                            val authors = book.volumeInfo.authors
                            val authorsText = if (authors != null && authors.isNotEmpty()) {
                                authors.joinToString(", ")
                            } else {
                                "Unknown"
                            }
                        }
                        bookRomanceResponse = bookRomanceResponse!!.copy(items = filteredBooks)
                    }
                } else {
                    // Obrada neuspješnog zahtjeva
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Romance",
                textAlign = TextAlign.Right,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            if (bookRomanceResponse != null && bookRomanceResponse!!.items.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 0.dp)
                ) {
                    items(bookRomanceResponse!!.items) { book ->
                        val isbn13Identifier = book.volumeInfo.industryIdentifiers.find { it.type == "ISBN_13" }
                        val isbn13 = isbn13Identifier?.identifier
                        Surface(
                            tonalElevation = 3.dp,
                            modifier = Modifier
                                .width(150.dp) // Postavljanje širine slike na 150 dp
                                .height(200.dp)
                                .padding(5.dp, 0.dp)
                                .clickable {
                                    navController.navigate("bookDetails/${isbn13}")}
                        ) {
                            // Prikaz naslovnice knjige
                            if (book.volumeInfo.imageLinks != null) {
                                val url: StringBuilder = StringBuilder(book.volumeInfo.imageLinks?.thumbnail)
                                url.insert(4, "s")
                                LazyLoadingImage(
                                    imageUrl = url.toString(),
                                    contentDescription = book.volumeInfo.title,
                                    modifier = Modifier.size(150.dp), // Postavljanje fiksnih dimenzija slike
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier.size(150.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painterResource(id = R.drawable.nourl),
                                        contentDescription = book.volumeInfo.title,
                                        modifier = Modifier
                                            .width(150.dp) // Postavljanje širine slike na 150 dp
                                            .height(200.dp), // Postavljanje fiksnih dimenzija slike
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }
            }

            var bookDramaResponse by remember { mutableStateOf<BookSearchResponse?>(null) }
            LaunchedEffect(Unit) {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.searchBooks(query = "subject:drama", apiKey = "AIzaSyAHMUBEm1_QCPLzS46-Z_knen7isgxqCvM", maxResults = 15)
                }
                if (response.isSuccessful) {
                    bookDramaResponse = response.body()
                    if (bookDramaResponse != null && bookDramaResponse!!.items.isNotEmpty()) {
                        val filteredBooks = bookDramaResponse!!.items.filter { book ->
                            val industryIdentifiers = book.volumeInfo.industryIdentifiers
                            industryIdentifiers != null && industryIdentifiers.find { it.type == "ISBN_13" }?.identifier != null
                        }
                        val sortedBooks = bookDramaResponse!!.items.sortedByDescending { it.volumeInfo.averageRating }
                        for (book in sortedBooks) {
                            val authors = book.volumeInfo.authors
                            val authorsText = if (authors != null && authors.isNotEmpty()) {
                                authors.joinToString(", ")
                            } else {
                                "Unknown"
                            }
                        }
                        bookDramaResponse = bookDramaResponse!!.copy(items = filteredBooks)
                    }
                } else {
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Drama",
                textAlign = TextAlign.Right,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(20.dp))

            if (bookDramaResponse != null && bookDramaResponse!!.items.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(10.dp, 0.dp)
                ) {
                    items(bookDramaResponse!!.items) { book ->
                        val isbn13Identifier = book.volumeInfo.industryIdentifiers.find { it.type == "ISBN_13" }
                        val isbn13 = isbn13Identifier?.identifier

                        Surface(
                            tonalElevation = 3.dp,
                            modifier = Modifier
                                .width(150.dp)
                                .height(200.dp)
                                .padding(5.dp, 0.dp)
                                .clickable {
                                    navController.navigate("bookDetails/${isbn13}")
                                }
                        ) {
                            if (book.volumeInfo.imageLinks != null) {
                                val url: StringBuilder = StringBuilder(book.volumeInfo.imageLinks?.thumbnail)
                                url.insert(4, "s")
                                AsyncImage(
                                    model = url.toString(),
                                    contentDescription = book.volumeInfo.title,
                                    modifier = Modifier.size(150.dp),
                                    contentScale = ContentScale.Crop,
                                    error = painterResource(id = R.drawable.cover),
                                )
                            } else {
                                Box(
                                    modifier = Modifier.size(150.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painterResource(id = R.drawable.nourl),
                                        contentDescription = book.volumeInfo.title,
                                        modifier = Modifier
                                            .width(150.dp) // Postavljanje širine slike na 150 dp
                                            .height(200.dp), // Postavljanje fiksnih dimenzija slike
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun LogoImage() {
    val context = LocalContext.current
    val isDarkMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    val logoImageRes = if (isDarkMode) R.drawable.logolight else R.drawable.logodark

    Image(
        painter = painterResource(id = logoImageRes),
        contentDescription = "Logo",
        modifier = Modifier.size(200.dp),
        alignment = Alignment.Center
    )
}
