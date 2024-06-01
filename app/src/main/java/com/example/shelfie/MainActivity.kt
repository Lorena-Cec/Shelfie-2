package com.example.shelfie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.shelfie.ui.theme.ShelfieTheme
import com.example.shelfie.view.StartScreen
import com.example.shelfie.viewmodel.MainViewModel
import androidx.activity.viewModels
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shelfie.view.HomeScreen
import com.example.shelfie.view.LibraryScreen
import com.example.shelfie.view.LoginScreen
import com.example.shelfie.view.ProfileScreen
import com.example.shelfie.view.RegisterScreen
import com.example.shelfie.view.SearchScreen
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShelfieTheme {
                val currentUser = remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }
                val navController = rememberNavController()
                FirebaseAuth.getInstance().addAuthStateListener { auth ->
                    currentUser.value = auth.currentUser
                }

                NavHost(navController, startDestination = if (currentUser.value != null) "home_screen" else "start_screen") {
                    composable("home_screen") {
                        HomeScreen(navController = navController)
                    }
                    composable("start_screen") {
                        StartScreen(navController = navController)
                    }
                    composable("login_screen") {
                        LoginScreen(navController = navController)
                    }
                    composable("register_screen") {
                        RegisterScreen(navController = navController)
                    }
                    composable("search_screen") {
                        SearchScreen(navController = navController)
                    }
                    composable("library_screen") {
                        LibraryScreen(navController = navController)
                    }
                    composable("profile_screen") {
                        ProfileScreen(navController = navController)
                    }
                }
            }
        }
    }
}
