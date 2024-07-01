package com.example.shelfie.view

import androidx.compose.foundation.Image
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.shelfie.R
import com.example.shelfie.model.BookItem
import com.example.shelfie.ui.theme.LightPurple
import com.example.shelfie.viewmodel.BookFirebaseViewModel


@Composable
fun ReadScreen(navController: NavController, category: String, booksViewModel: BookFirebaseViewModel = viewModel()) {
    var showDialog by remember { mutableStateOf(false) }

    val books by when (category) {
        "Read" -> booksViewModel.booksRead.collectAsState()
        "MyPhysicalBooks" -> booksViewModel.myPhysicalBooks.collectAsState()
        "ToBeRead" -> booksViewModel.booksToRead.collectAsState()
        "CurrentlyReading" -> booksViewModel.currentlyReading.collectAsState()
        else -> remember { mutableStateOf(emptyList<BookItem>()) }
    }

    var title = ""
    when (category) {
        "Read" -> title = "Read Books"
        "MyPhysicalBooks" -> title = "My Physical Books"
        "ToBeRead" -> title = "To Be Read Books"
        "CurrentlyReading" -> title = "Currently Reading Books"
        else -> title = ""
    }

    LaunchedEffect(Unit) {
        booksViewModel.fetchBooks()
    }

    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) },
        floatingActionButton = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(16.dp) // Prilagodite padding po potrebi
                    .border(1.dp, Color.White, shape = CircleShape) // Postavite bijeli border
            ) {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = DarkPurple,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Book",
                        tint = Color.White
                    )
                }
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
                title = title,
                expanded = expanded,
                onExpandedChange = { expanded = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                val rows = books.chunked(3)
                items(rows.size) { rowIndex ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp, 0.dp)
                    ) {
                        val rowItems = rows[rowIndex]
                        for (book in rowItems) {
                            if (book.volumeInfo.imageLinks?.thumbnail != null) {
                                val isbn13Identifier = book.volumeInfo.industryIdentifiers.find { it.type == "ISBN_13" }
                                val isbn13 = isbn13Identifier?.identifier
                                val url = "https" + book.volumeInfo.imageLinks.thumbnail.substring(4)
                                LazyLoadingImage(
                                    imageUrl = url.toString(),
                                    contentDescription = book.volumeInfo.title,
                                    modifier = Modifier
                                        .height(150.dp)
                                        .width(120.dp)
                                        .clickable { navController.navigate("readDetails/${isbn13}/${category}") }
                                        .padding(10.dp, 0.dp),
                                    contentScale = ContentScale.Crop
                                )

                            }
                            else {
                                val isbn13Identifier = book.volumeInfo.industryIdentifiers.find { it.type == "ISBN_13" }
                                val isbn13 = isbn13Identifier?.identifier
                                Box(
                                    modifier = Modifier
                                        .width(120.dp)
                                        .height(150.dp)
                                        .clickable { navController.navigate("readDetails/${isbn13}/${category}") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.coverr),
                                        contentDescription = book.volumeInfo.title,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Text(
                                        text = book.volumeInfo.title,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(end=10.dp),
                                        color = Color.White,
                                        lineHeight = 16.sp,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }


                        }
                        if (rowItems.size < 3) {
                            for (i in 1..(3 - rowItems.size)) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = DarkPurple)
                            .height(30.dp),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = {
                        Text(
                            text = "Add books via:",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    buttons = {
                        Column(
                            modifier = Modifier
                                .width(250.dp)
                                .padding(top = 25.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                onClick = {
                                    showDialog = false
                                    navController.navigate("search_screen")
                                },
                                modifier = Modifier
                                    .width(200.dp)
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
                            Button(
                                onClick = {
                                    showDialog = false
                                    navController.navigate("barcode_screen")
                                },
                                modifier = Modifier
                                    .width(200.dp)
                                    .height(70.dp)
                                    .padding(bottom = 20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LightPurple
                                )
                            ) {
                                Text("Barcode", modifier = Modifier.padding(end = 12.dp), fontSize = 17.sp)
                                Icon(
                                    painter = painterResource(context.resources.getIdentifier("camera", "drawable", context.packageName)),
                                    contentDescription = "Camera Icon",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }
                )
            }
        }
    }
}

