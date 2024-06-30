package com.example.shelfie.view

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shelfie.ui.theme.DarkPurple
import com.example.shelfie.ui.theme.LightPurple
import com.example.shelfie.viewmodel.LoginRegisterViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegisterScreen(navController: NavController) {
    var username by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var passwordConfirm by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    val viewModel: LoginRegisterViewModel = viewModel()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = "Register",
                color = DarkPurple,
                fontSize = 30.sp
            )
            Spacer(modifier = Modifier.height(40.dp))
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") }
            )
            Spacer(modifier = Modifier.height(40.dp))
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") }
            )
            Spacer(modifier = Modifier.height(40.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(40.dp))
            TextField(
                value = passwordConfirm,
                onValueChange = { passwordConfirm = it },
                label = { Text("Confirm password") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(onClick = { /*TODO: Send data to Firebase and go to HomeScreen*/
                viewModel.register(context, username, email, password, passwordConfirm, navController)},
                modifier = Modifier.size(width = 300.dp, height = 60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LightPurple),
                shape = RoundedCornerShape(50)
            ) {
                Text(text = "Sign up", fontSize = 20.sp)
            }
        }
    }
}

