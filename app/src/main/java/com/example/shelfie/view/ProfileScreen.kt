package com.example.shelfie.view

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.shelfie.R
import com.example.shelfie.ui.theme.DarkPurple
import com.example.shelfie.ui.theme.LightPurple
import com.google.firebase.firestore.FirebaseFirestore
import com.example.shelfie.viewmodel.BooksViewModel

@Composable
fun ProfileScreen(navController: NavController) {

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
            Box(modifier = Modifier
                .fillMaxWidth()
                .background(color = DarkPurple)
                .height(80.dp),
                contentAlignment = Alignment.TopStart
            ){
                Text(
                    text = "My Profile",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, top = 25.dp),
                    textAlign = TextAlign.Start,
                    fontSize = 24.sp,
                    color = Color.White
                )
            }

            ProfileImage(viewModel())

            Text(
                text = "My favorite books",
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 0.dp)
            ) {
                items(photos, key = { it }) {
                    Surface(
                        tonalElevation = 3.dp,
                        modifier = Modifier
                            .size(150.dp)
                            .padding(10.dp, 0.dp)
                    ) {
                        // Ovdje možete dodati logiku za prikaz omiljenih knjiga
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = DarkPurple)
                    .height(30.dp),
            ) {}

            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}

@Composable
fun ProfileImage(viewModel: BooksViewModel){
    val imageUri = rememberSaveable { mutableStateOf("") }
    val painter = rememberImagePainter(
        if (imageUri.value.isEmpty())
            R.drawable.profile
        else
            imageUri.value
    )
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()
    ) {uri: Uri? ->
        uri?.let{
            imageUri.value = it.toString()
            viewModel.uploadImage(it)
        }
    }

    Column(
        modifier = Modifier
            .padding(end = 30.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = CircleShape,
            modifier = Modifier
                .size(120.dp)
                .offset(y = (-40).dp)
                .align(Alignment.End),
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .wrapContentSize()
                    .size(120.dp)
                    .clickable { launcher.launch("image/*") },
                contentScale = ContentScale.Crop
            )
        }
    }
}