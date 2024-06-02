package com.example.shelfie.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shelfie.ui.theme.DarkPurple
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.example.shelfie.model.BookItem
import com.example.shelfie.viewmodel.BooksViewModel


@Composable
fun LibraryScreen(navController: NavController, viewModel: BooksViewModel) {
    val photos by rememberSaveable { mutableStateOf(List(10) { it }) }
    var expanded by remember { mutableStateOf(false) }
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            TopBarWithMenu(
                navController = navController,
                title = "All Books",
                expanded = expanded,
                onExpandedChange = { expanded = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                val rows = viewModel.allBooks.chunked(3) // Podijeli knjige u grupe od po tri
                items(rows.size) { rowIndex ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                            .padding(10.dp, 0.dp)
                    ) {
                        val rowItems = rows[rowIndex]
                        for (book in rowItems) {
                            val url: StringBuilder = StringBuilder(book.volumeInfo.imageLinks?.thumbnail)
                            url.insert(4, "s")
                            LazyLoadingImage(
                                imageUrl = url.toString(),
                                contentDescription = book.volumeInfo.title,
                                modifier = Modifier.height(150.dp)
                                    .width(120.dp)
                                    .padding(10.dp, 0.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        if (rowItems.size < 3) {
                            // Dodajte prazne kutije ako je manje od tri stavke u redu
                            for (i in 1..(3 - rowItems.size)) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .background(color = DarkPurple)
                            .height(30.dp),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

        }
    }
}