package com.example.shelfie.view


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shelfie.R
import com.example.shelfie.ui.theme.DarkPurple
import com.example.shelfie.ui.theme.LightPurple
import com.example.shelfie.viewmodel.MainViewModel

@Composable
fun LoginScreen(viewModel : MainViewModel) {
    var email by remember {
        mutableStateOf("Email/Username")
    }
    var password by remember {
        mutableStateOf("Password")
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = "Sign in",
                color = DarkPurple,
                fontSize = 30.sp
                )
            Spacer(modifier = Modifier.height(40.dp))
            TextField(
                value = email,
                onValueChange = { email = it },
                )
            Spacer(modifier = Modifier.height(40.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(onClick = { /*TODO: Send data to Firebase and go to HomeScreen*/ },
                modifier = Modifier.size(width = 300.dp, height = 60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LightPurple),
                shape = RoundedCornerShape(50)
            ) {
                Text(text = "Sign in", fontSize = 20.sp)
            }
        }
    }
}