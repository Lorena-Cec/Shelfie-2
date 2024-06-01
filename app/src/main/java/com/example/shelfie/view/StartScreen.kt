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
import androidx.compose.runtime.Composable
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
fun StartScreen(viewModel : MainViewModel) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "logo",
                modifier = Modifier.size(250.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(onClick = { /*TODO: Signing in with Google */ },
                modifier = Modifier.size(width = 300.dp, height = 60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(2.dp, DarkPurple),
                shape = RoundedCornerShape(50)
                ) {
                Text(text = "Continue with Google", color = DarkPurple, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(40.dp))
            Button(onClick = { /*TODO: Register*/ },
                colors = ButtonDefaults.buttonColors(containerColor = DarkPurple),
                modifier = Modifier.size(width = 300.dp, height = 60.dp),
                shape = RoundedCornerShape(50),
                ) {
                Text(text = "Get started", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(40.dp))
            Button(onClick = { /*TODO: Login with email or Username*/ },
                modifier = Modifier.size(width = 300.dp, height = 60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LightPurple),
                shape = RoundedCornerShape(50)
            ) {
                Text(text = "I already have an Account", fontSize = 20.sp)
            }
        }
    }
}