package com.example.shelfie.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import com.example.shelfie.R
import com.example.shelfie.ui.theme.DarkPurple


@Composable
fun ProfileScreen(navController: NavController) {
    var readingGoal by remember { mutableStateOf("") }
    val photos by rememberSaveable { mutableStateOf(List(10) { it }) }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Box(modifier = Modifier.fillMaxWidth()
                .background(color = DarkPurple)
                .height(70.dp),
                contentAlignment = Alignment.TopStart
            ){
                Text(
                    text = "My Profile",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp, 16.dp),
                    textAlign = TextAlign.Start,
                    fontSize = 24.sp
                )
            }
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .offset(y = (-40).dp)
                    .padding(end = 30.dp)
                    .align(Alignment.End),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "profileimage",
                    modifier = Modifier.size(140.dp),
                    alignment = Alignment.TopEnd
                )
            }

            Text(
                text = "My favorite books",
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

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
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(color = DarkPurple)
                    .height(30.dp),
            ) {}

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Reading goal:",
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = readingGoal,
                onValueChange = { /* Postavi cilj čitanja u bazi podataka */ },
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            // Ovdje možete dodati kružni graf koji prikazuje postotak postignutog cilja čitanja
        }
    }
}
