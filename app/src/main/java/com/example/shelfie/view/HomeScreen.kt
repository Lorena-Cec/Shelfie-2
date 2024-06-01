package com.example.shelfie.view

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shelfie.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val photos by rememberSaveable { mutableStateOf(List(10) { it }) }
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
        /*floatingActionButton = { //OVO CE TREBAT ZA LIBRARY SCREEN MOZDA
            FloatingActionButton(onClick = { presses++ }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }*/
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
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
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
                    .padding(10.dp, 0.dp)
            ) {
                items(photos, key = { it }) {
                    Surface(
                        tonalElevation = 3.dp,
                        modifier = Modifier.size(150.dp)
                            .padding(10.dp, 0.dp)
                    ) {
                        // Ovdje možete dodati logiku za prikaz omiljenih knjiga
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Reccommendations",
                textAlign = TextAlign.Right
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
                    .padding(10.dp, 0.dp)
            ) {
                items(photos, key = { it }) {
                    Surface(
                        tonalElevation = 3.dp,
                        modifier = Modifier.size(150.dp)
                            .padding(10.dp, 0.dp)
                    ) {
                        // Ovdje možete dodati logiku za prikaz omiljenih knjiga
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Fantasy",
                textAlign = TextAlign.Right
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
                    .padding(10.dp, 0.dp)
            ) {
                items(photos, key = { it }) {
                    Surface(
                        tonalElevation = 3.dp,
                        modifier = Modifier.size(150.dp)
                            .padding(10.dp, 0.dp)
                    ) {
                        // Ovdje možete dodati logiku za prikaz omiljenih knjiga
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
