package com.example.shelfie.view

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.shelfie.model.BookItem
import com.example.shelfie.viewmodel.BooksViewModel
@Composable
fun BookLists(navController: NavController, viewModel: BooksViewModel, book: BookItem) {
    var menuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 8.dp)
    ) {
        if (book.volumeInfo.imageLinks?.thumbnail != null) {
            val isbn13Identifier = book.volumeInfo.industryIdentifiers?.find { it.type == "ISBN_13" }
            val isbn13 = isbn13Identifier?.identifier
            Log.d("BookDetailsScreen", "ISBN-13: $isbn13")
            val url = "https" + book.volumeInfo.imageLinks.thumbnail.substring(4)
            LazyLoadingImage(
                imageUrl = url,
                contentDescription = book.volumeInfo.title,
                modifier = Modifier
                    .width(150.dp)
                    .clickable { navController.navigate("bookDetails/$isbn13") }
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        }
        Column (
            modifier = Modifier
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val isbn13Identifier = book.volumeInfo.industryIdentifiers?.find { it.type == "ISBN_13" }
            val isbn13 = isbn13Identifier?.identifier
            Text(
                text = book.volumeInfo.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { navController.navigate("bookDetails/${isbn13}") }
            )
            val authorsText = book.volumeInfo.authors?.joinToString(", ") ?: "Unknown"
            Text(
                text = authorsText,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.clickable { navController.navigate("bookDetails/${isbn13}") }
            )
            Box {
                Button(onClick = { menuExpanded = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Book to shelf")
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
        }
    }
}