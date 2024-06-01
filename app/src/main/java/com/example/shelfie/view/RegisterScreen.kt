package com.example.shelfie.view

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shelfie.R
import com.example.shelfie.ui.theme.DarkPurple
import com.example.shelfie.ui.theme.LightPurple
import com.example.shelfie.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegisterScreen(navController: NavController) {
    var username by remember {
        mutableStateOf("Username")
    }
    var email by remember {
        mutableStateOf("Email")
    }
    var password by remember {
        mutableStateOf("Password")
    }
    var passwordConfirm by remember {
        mutableStateOf("Confirm Password")
    }
    val context = LocalContext.current
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
            TextField(
                value = passwordConfirm,
                onValueChange = { passwordConfirm = it },
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(onClick = { /*TODO: Send data to Firebase and go to HomeScreen*/
                register(context, username, email, password, passwordConfirm, navController)},
                modifier = Modifier.size(width = 300.dp, height = 60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LightPurple),
                shape = RoundedCornerShape(50)
            ) {
                Text(text = "Sign up", fontSize = 20.sp)
            }
        }
    }
}


private fun register(context: Context, username: String, email: String, password: String, passwordConfirm: String, navController: NavController) {
    if (password != passwordConfirm) {
        Toast.makeText(context, "Password mismatch. Try again.", Toast.LENGTH_SHORT).show()
        return
    }

    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = task.result?.user?.uid
                if (userId != null) {
                    val user = hashMapOf(
                        "username" to username,
                        "email" to email
                    )
                    FirebaseFirestore.getInstance().collection("users").document(userId)
                        .set(user)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Registered successfully", Toast.LENGTH_SHORT).show()
                            navController.navigate("home_screen")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Register", "Error saving user data", e)
                            Toast.makeText(context, "Failed to save user data", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "User ID is null", Toast.LENGTH_SHORT).show()
                    Log.e("Register", "User ID is null after successful registration")
                }
            } else {
                Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
                Log.e("Register", "Registration failed", task.exception)
            }
        }
        .addOnFailureListener { exception ->
            Log.e("Register", "Registration failed with exception", exception)
            Toast.makeText(context, "Registration failed: ${exception.message}", Toast.LENGTH_LONG).show()
        }
}