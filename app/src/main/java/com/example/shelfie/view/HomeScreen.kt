package com.example.shelfie.view

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.shelfie.R
import com.example.shelfie.model.BookSearchResponse
import com.example.shelfie.viewmodel.HomeViewModel

@Composable
fun HomeScreen(navController: NavController) {
    val homeViewModel: HomeViewModel = viewModel()
    val fantasyBooks by homeViewModel.fantasyBooks.collectAsState()
    val classicsBooks by homeViewModel.classicsBooks.collectAsState()
    val romanceBooks by homeViewModel.romanceBooks.collectAsState()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                LogoImage()
            }
            Spacer(modifier = Modifier.height(20.dp))
            BookCategorySection("Fantasy", fantasyBooks, navController)
            Spacer(modifier = Modifier.height(20.dp))
            BookCategorySection("Classics", classicsBooks, navController)
            Spacer(modifier = Modifier.height(20.dp))
            BookCategorySection("Romance", romanceBooks, navController)
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun BookCategorySection(category: String, bookSearchResponse: BookSearchResponse?, navController: NavController) {
    Text(
        modifier = Modifier.padding(8.dp),
        text = category,
        textAlign = TextAlign.Right,
        fontSize = 20.sp
    )
    Spacer(modifier = Modifier.height(10.dp))
    if (bookSearchResponse != null && bookSearchResponse.items.isNotEmpty()) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(10.dp, 0.dp)
        ) {
            items(bookSearchResponse.items) { book ->
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
                            Text("No Image Available", textAlign = TextAlign.Center)
                        }
                        Toast.makeText(LocalContext.current, "No image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
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
