package com.example.shelfie.view

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.shelfie.viewmodel.BooksViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, viewModel: BooksViewModel) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    val bookResponse by viewModel.bookResponse.observeAsState()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
        // Ostatak vašeg koda...
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.padding(20.dp, 40.dp, 10.dp, 10.dp),
                text = "Explore the World of Books",
                textAlign = TextAlign.Left,
                fontSize = 30.sp,
                lineHeight = 40.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    placeholder = { Text(text = "Search for books") },
                    singleLine = true,
                )
                IconButton(
                    onClick = {
                        viewModel.searchBooks(query)
                    },
                    modifier = Modifier.padding(end = 16.dp) // Dodajemo padding samo s desne strane ikone
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }
            if (query.isEmpty()) {
                viewModel.randomBooks("subject:fiction")
                bookResponse?.let { response ->
                    if (response.items.isNotEmpty()) {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 100.dp)
                        ) {
                            items(response.items) { book ->
                                Box(
                                    modifier = Modifier
                                        .padding(8.dp)
                                ) {
                                    if (book.volumeInfo.imageLinks != null) {
                                        val url: StringBuilder = StringBuilder(book.volumeInfo.imageLinks?.thumbnail)
                                        url.insert(4, "s")
                                        LazyLoadingImage(
                                            imageUrl = url.toString(),
                                            contentDescription = book.volumeInfo.title,
                                            modifier = Modifier.width(150.dp) // Postavljanje širine slike na 150 dp
                                                .height(200.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(query != ""){
                bookResponse?.let { response ->
                    if (response.items.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            items(response.items) { book ->
                                Box(
                                    modifier = Modifier
                                        .padding(8.dp)
                                ) {
                                    BookLists(viewModel = viewModel, book = book)
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
fun LazyLoadingImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val painter: Painter = rememberAsyncImagePainter(imageUrl)
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
    )
}
