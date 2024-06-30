package com.example.shelfie.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginRegisterViewModel : ViewModel() {
    fun signIn(context: Context, email: String, password: String, navController: NavController) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Prijavljeno uspješno
                    Toast.makeText(context, "Logged in successfully", Toast.LENGTH_SHORT).show()
                    navController.navigate("home_screen") {
                        popUpTo("login_screen") { inclusive = true }
                    }
                } else {
                    // Prijavljivanje neuspješno
                    Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
    fun register(context: Context, username: String, email: String, password: String, passwordConfirm: String, navController: NavController) {
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
}