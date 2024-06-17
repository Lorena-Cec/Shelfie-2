package com.example.shelfie.view

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.shelfie.R
import com.example.shelfie.model.BookItem
import com.example.shelfie.ui.theme.DarkPurple
import com.example.shelfie.ui.theme.LightPurple
import com.example.shelfie.ui.theme.LigtherPurple
import com.google.firebase.firestore.FirebaseFirestore
import com.example.shelfie.viewmodel.BooksViewModel

@Composable
fun ProfileScreen(navController: NavController, viewModel: BooksViewModel, onLogout: () -> Unit) {
    var quoteText by remember { mutableStateOf("") }
    var bookTitle by remember { mutableStateOf("") }
    var pageNumber by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    val favorites by viewModel.favorites.collectAsState()
    val favoriteQuotes by viewModel.favoriteQuotes.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchFavorites()
        viewModel.fetchFavoriteQuotes()
    }
        Scaffold(
            bottomBar = { BottomNavigationBar(navController = navController) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = DarkPurple)
                        .height(80.dp),
                    contentAlignment = Alignment.TopStart
                ){
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Profile",
                            modifier = Modifier
                                .padding(start = 30.dp),
                            textAlign = TextAlign.Start,
                            fontSize = 24.sp,
                            color = Color.White
                        )
                        Button(
                            onClick = {
                                onLogout()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LightPurple
                            ),
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Text("Logout")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "My favorite books",
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .padding(5.dp, 0.dp)
                ) {
                    items(favorites) { book ->
                        BookItemSurface(book = book, navController)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = DarkPurple)
                        .height(30.dp),
                ) {}

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "My favorite quotes",
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                )
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(favoriteQuotes) { quoteMap ->
                        val quote = quoteMap["quote"] ?: ""
                        val bookTitle = quoteMap["bookTitle"] ?: ""
                        val pageNumber = quoteMap["pageNumber"] ?: ""

                        QuoteItem(
                            quote = quote,
                            bookTitle = bookTitle,
                            pageNumber = pageNumber,
                            navController = navController
                        ) {
                            viewModel.removeQuote(quote, bookTitle, pageNumber)
                        }
                    }

                    item {
                        AddQuoteButton(onClick = { showAddDialog = true })
                    }
                }

            }
        }
    if (showAddDialog) {
        AddQuoteDialog(
            onDismiss = { showAddDialog = false },
            onSave = {
                if (quoteText.isNotBlank()) {
                    viewModel.addFavoriteQuote(quoteText, bookTitle, pageNumber)
                    showAddDialog = false
                    quoteText = ""
                    bookTitle = ""
                    pageNumber = ""
                }
            },
            quoteText = quoteText,
            onQuoteTextChanged = { quoteText = it },
            bookTitle = bookTitle,
            onBookTitleChanged = { bookTitle = it },
            pageNumber = pageNumber,
            onPageNumberChanged = { pageNumber = it }
        )
    }
    }


@Composable
fun BookItemSurface(book: BookItem, navController: NavController) {
    Surface(
        color = Color.White,
        modifier = Modifier
            .width(150.dp)
            .height(210.dp)
            .padding(5.dp, 0.dp)
    ) {
        val isbn13Identifier = book.volumeInfo.industryIdentifiers?.find { it.type == "ISBN_13" }
        val isbn13 = isbn13Identifier?.identifier
        val url: StringBuilder = StringBuilder(book.volumeInfo.imageLinks?.thumbnail)
        url.insert(4, "s")
        LazyLoadingImage(
            imageUrl = url.toString(),
            contentDescription = book.volumeInfo.title,
            modifier = Modifier
                .size(150.dp)
                .clickable { navController.navigate("readDetails/${isbn13}") },
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun QuoteItem(quote: String, bookTitle: String, pageNumber: String, navController: NavController, onRemoveQuote: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Remove quote?") },
            confirmButton = {
                Button(onClick = {
                    onRemoveQuote()
                    navController.navigate("profile_screen")
                    showDialog = false
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = LightPurple
                )) {
                    Text("Remove")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }, colors = ButtonDefaults.buttonColors(
                    containerColor = LightPurple
                )) {
                    Text("Cancel")
                }
            }
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(color = LigtherPurple, shape = RoundedCornerShape(10.dp))
            .clickable { showDialog = true },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "\"$quote\"",
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                color = Color.Black
            )
            if(bookTitle.isNotBlank() && pageNumber.isNotBlank()) {
                Text(
                    text = "- $bookTitle, page $pageNumber",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
            else if(bookTitle.isNotBlank()){
                Text(
                    text = "- $bookTitle",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
            else if(pageNumber.isNotBlank()){
                Text(
                    text = "- page $pageNumber",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun AddQuoteButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick) {
            Icon(Icons.Default.Add, contentDescription = "Add Quote")
        }
    }
}

@Composable
fun AddQuoteDialog(
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    quoteText: String,
    onQuoteTextChanged: (String) -> Unit,
    bookTitle: String,
    onBookTitleChanged: (String) -> Unit,
    pageNumber: String,
    onPageNumberChanged: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Quote") },
        text = {
            Column {
                TextField(
                    value = quoteText,
                    onValueChange = onQuoteTextChanged,
                    label = { Text("Quote") }
                )
                TextField(
                    value = bookTitle,
                    onValueChange = onBookTitleChanged,
                    label = { Text("Book") }
                )
                TextField(
                    value = pageNumber,
                    onValueChange = onPageNumberChanged,
                    label = { Text("Page") }
                )
            }
        },
        confirmButton = {
            Button(onClick = onSave, colors = ButtonDefaults.buttonColors(
                containerColor = LightPurple
            )) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(
                containerColor = LightPurple
            )) {
                Text("Cancel")
            }
        }
    )
}