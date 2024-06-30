package com.example.shelfie.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.shelfie.R
import com.example.shelfie.viewmodel.BookFirebaseViewModel
import com.example.shelfie.viewmodel.BookSearchViewModel


@Composable
fun SearchScreen(navController: NavController, viewModel: BookFirebaseViewModel) {
    var query by remember { mutableStateOf("") }
    val bookSearchViewModel: BookSearchViewModel = viewModel()
    val bookResponse by bookSearchViewModel.bookResponse.observeAsState()
    val context = LocalContext.current
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.padding(20.dp, 40.dp, 20.dp, 10.dp),
                text = "Explore the World of Books",
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                lineHeight = 40.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = query,
                    onValueChange = { newQuery ->
                        query = newQuery
                        if (newQuery.isEmpty()) {
                            bookSearchViewModel.clearBookResponse()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    placeholder = { Text(text = "Search for books") },
                    singleLine = true,
                )
                IconButton(
                    onClick = {
                        if (query.isNotBlank()) {
                            bookSearchViewModel.searchBooks(query, context)
                        } else {
                            Toast.makeText(context, "Enter a search query", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
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
                                    BookLists(navController = navController, viewModel = viewModel, book = book)
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
    val painter: Painter = if (imageUrl.isEmpty()) {
        painterResource(id = R.drawable.cover)
    } else {
        rememberAsyncImagePainter(imageUrl)
    }
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
    )
}
