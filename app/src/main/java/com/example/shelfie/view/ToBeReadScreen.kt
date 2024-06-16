package com.example.shelfie.view

import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.example.shelfie.model.BookItem
import com.example.shelfie.ui.theme.LightPurple
import com.example.shelfie.viewmodel.BooksViewModel


@Composable
fun ToBeReadScreen(navController: NavController, booksViewModel: BooksViewModel = viewModel()) {
    var showDialog by remember { mutableStateOf(false) }
    val booksToRead by booksViewModel.booksToRead.collectAsState()

    LaunchedEffect(Unit) {
        booksViewModel.fetchBooks("L7aX4ZDOL9bxiBpIla1mooU9Qwu1")
    }

    var expanded by remember { mutableStateOf(false) }
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true },
                containerColor = DarkPurple) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Book",
                    tint = Color.White
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            TopBarWithMenu(
                navController = navController,
                title = "To Be Read",
                expanded = expanded,
                onExpandedChange = { expanded = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                val rows = booksToRead.chunked(3)
                items(rows.size) { rowIndex ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                            .padding(10.dp, 0.dp)
                    ) {
                        val rowItems = rows[rowIndex]
                        for (book in rowItems) {
                            val isbn13Identifier = book.volumeInfo.industryIdentifiers?.find { it.type == "ISBN_13" }
                            val isbn13 = isbn13Identifier?.identifier
                            val url: StringBuilder = StringBuilder(book.volumeInfo.imageLinks?.thumbnail)
                            url.insert(4, "s")
                            LazyLoadingImage(
                                imageUrl = url.toString(),
                                contentDescription = book.volumeInfo.title,
                                modifier = Modifier.height(150.dp)
                                    .width(120.dp)
                                    .clickable { navController.navigate("toBeReadDetails/${isbn13}") }
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
            if (showDialog) {
                val context = LocalContext.current
                val cameraIcon: Painter = painterResource(context.resources.getIdentifier("camera", "drawable", context.packageName))

                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = "Add books via:", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                    buttons = {
                        Column(
                            modifier = Modifier.width(250.dp)
                                .padding(top = 25.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(onClick = {
                                showDialog = false
                                navController.navigate("search_screen")
                            },
                                modifier = Modifier.width(200.dp)
                                    .height(65.dp)
                                    .padding(bottom = 15.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LightPurple
                                )
                            ) {
                                Text("Search", modifier = Modifier.padding(end = 9.dp), fontSize = 17.sp)
                                Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
                                Spacer(modifier = Modifier.width(8.dp))

                            }
                            Button(onClick = {
                                showDialog = false
                                navController.navigate("barcode_screen")
                            },
                                modifier = Modifier.width(200.dp)
                                    .height(70.dp)
                                    .padding(bottom = 20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LightPurple
                                ),

                                ) {
                                Text("Barcode", modifier = Modifier.padding(end = 12.dp), fontSize = 17.sp)
                                Icon(painter = cameraIcon, contentDescription = "Camera Icon", modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))

                            }
                        }
                    }
                )
            }
        }
    }
}
