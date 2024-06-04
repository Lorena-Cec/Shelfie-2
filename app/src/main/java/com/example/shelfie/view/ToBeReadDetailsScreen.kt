package com.example.shelfie.view

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shelfie.api.RetrofitClient
import com.example.shelfie.model.BookSearchResponse
import com.example.shelfie.ui.theme.DarkPurple
import com.example.shelfie.ui.theme.LightPurple
import com.example.shelfie.viewmodel.BooksViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToBeReadDetailsScreen(navController: NavController, isbn13: String) {

    var bookResponse by remember { mutableStateOf<BookSearchResponse?>(null) }
    LaunchedEffect(Unit) {
        val response = withContext(Dispatchers.IO) {
            RetrofitClient.apiService.searchBooks(
                query = "isbn:$isbn13",
                apiKey = "AIzaSyAHMUBEm1_QCPLzS46-Z_knen7isgxqCvM",
                maxResults = 1,
            )
        }
        Log.d("BookDetailsScreen", "Response body: ${response.body()}")
        if (response.isSuccessful) {
            bookResponse = response.body()
            Log.d("BookDetailsScreen", "ISBN-13: $bookResponse")
        }
        else{
            Log.d("BookDetailsScreen", "Fail ")
        }
    }
    bookResponse?.items?.firstOrNull()?.let { book ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Book Details", color = androidx.compose.ui.graphics.Color.White, fontSize = 20.sp)},
                    modifier = Modifier.fillMaxWidth(),
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate("toberead_screen") }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = androidx.compose.ui.graphics.Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = DarkPurple)
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp,120.dp,10.dp,10.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (book.volumeInfo.imageLinks != null) {
                    val url: StringBuilder = StringBuilder(book.volumeInfo.imageLinks?.thumbnail)
                    url.insert(4, "s")
                    Box(
                        modifier = Modifier
                            .width(200.dp)
                            .padding(0.dp,0.dp,0.dp,30.dp)
                            .height(270.dp)
                            .background(LightPurple, shape = RoundedCornerShape(5.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        LazyLoadingImage(
                            imageUrl = url.toString(),
                            contentDescription = book.volumeInfo.title,
                            modifier = Modifier.width(150.dp)
                                .height(220.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Text(
                    text = book.volumeInfo.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 2.dp)
                )

                val authorsText = if (book.volumeInfo.authors != null && book.volumeInfo.authors.isNotEmpty()) {
                    book.volumeInfo.authors.joinToString(", ")
                } else {
                    "Unknown"
                }
                Text(
                    text = authorsText,
                    fontStyle = FontStyle.Italic,
                    fontSize = 19.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(0.dp,0.dp,0.dp,30.dp)
                )

                Text(
                    text = book.volumeInfo.description,
                    fontStyle = FontStyle.Italic,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 18.dp)
                )

                var menuExpanded by remember { mutableStateOf(false) }
                val context = LocalContext.current
                val viewModel: BooksViewModel = viewModel()
                Box(
                    modifier = Modifier
                        .padding(0.dp,10.dp,0.dp,10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { menuExpanded = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LightPurple
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Book to Shelf", color = androidx.compose.ui.graphics.Color.White, fontSize = 15.sp)
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(onClick = {
                            menuExpanded = false
                            viewModel.addBookToCategory(book, "MyPhysicalBooks")
                            Toast.makeText(context, "Added book to My Physical Books", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("My Physical Books")
                        }
                        DropdownMenuItem(onClick = {
                            menuExpanded = false
                            viewModel.addBookToCategory(book, "Read")
                            Toast.makeText(context, "Added book to Read", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("Read")
                        }
                        DropdownMenuItem(onClick = {
                            menuExpanded = false
                            viewModel.addBookToCategory(book, "ToBeRead")
                            Toast.makeText(context, "Added book to To be Read", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("To Be Read")
                        }
                        DropdownMenuItem(onClick = {
                            menuExpanded = false
                            viewModel.addBookToCategory(book, "CurrentlyReading")
                            Toast.makeText(context, "Added book to Currently Reading", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("Currently Reading")

                        }
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
