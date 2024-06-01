package com.example.shelfie.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shelfie.R
import com.example.shelfie.ui.theme.DarkPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val photos by rememberSaveable { mutableStateOf(List(10) { it }) }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.fillMaxWidth()
                    .height(50.0.dp),

                containerColor = DarkPurple,
                contentColor = Color.White,
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(Icons.Filled.Check, contentDescription = "Localized description")
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Localized description",
                        )
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Localized description",
                        )
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Localized description",
                        )
                    }
                }
            )
        },
        /*floatingActionButton = { //OVO CE TREBAT ZA LIBRARY SCREEN MOZDA
            FloatingActionButton(onClick = { presses++ }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }*/
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                Image(
                    painter = painterResource(id = R.drawable.smallerlogo),
                    contentDescription = "logo",
                    modifier = Modifier.size(150.dp),
                    alignment = Alignment.Center
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                modifier = Modifier.padding(8.dp),
                text = "New Releases",
                textAlign = TextAlign.Right
            )
            LazyRow (
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ){
                items(photos, key = { it }) {
                    Surface(
                        tonalElevation = 3.dp,
                        modifier = Modifier.size(150.dp)
                    ) {}
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Reccommendations",
                textAlign = TextAlign.Right
            )
            LazyRow (
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ){
                items(photos, key = { it }) {
                    Surface(
                        tonalElevation = 3.dp,
                        modifier = Modifier.size(150.dp)
                    ) {}
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Fantasy",
                textAlign = TextAlign.Right
            )
            LazyRow (
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ){
                items(photos, key = { it }) {
                    Surface(
                        tonalElevation = 3.dp,
                        modifier = Modifier.size(150.dp)
                    ) {}
                }
            }
        }
    }
}
