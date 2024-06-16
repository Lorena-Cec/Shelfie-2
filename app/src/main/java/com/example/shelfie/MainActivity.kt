package com.example.shelfie

import BarcodeScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.shelfie.ui.theme.ShelfieTheme
import com.example.shelfie.view.StartScreen
import com.example.shelfie.viewmodel.MainViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shelfie.model.BookItem
import com.example.shelfie.view.BookDetailsScreen
import com.example.shelfie.view.CurrentlyReadingDetailsScreen
import com.example.shelfie.view.CurrentlyReadingScreen
import com.example.shelfie.view.HomeScreen
import com.example.shelfie.view.LoginScreen
import com.example.shelfie.view.MyPhysicalBooksDetailsScreen
import com.example.shelfie.view.MyPhysicalBooksScreen
import com.example.shelfie.view.ProfileScreen
import com.example.shelfie.view.ReadDetailsScreen
import com.example.shelfie.view.ReadScreen
import com.example.shelfie.view.RegisterScreen
import com.google.firebase.auth.FirebaseAuth
import com.example.shelfie.view.SearchScreen
import com.example.shelfie.view.ToBeReadDetailsScreen
import com.example.shelfie.view.ToBeReadScreen
import com.example.shelfie.viewmodel.BooksViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShelfieTheme {
                val currentUser = remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }
                val navController = rememberNavController()
                val booksViewModel: BooksViewModel by viewModels()

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
                        SearchScreen(navController = navController, viewModel = booksViewModel)
                    }
                    composable("profile_screen") {
                        ProfileScreen(navController = navController, viewModel = booksViewModel)
                    }
                    composable("read_screen") {
                        ReadScreen(navController = navController)
                    }
                    composable("toberead_screen") {
                        ToBeReadScreen(navController = navController)
                    }
                    composable("currentlyreading_screen") {
                        CurrentlyReadingScreen(navController = navController)
                    }
                    composable("myphysicalbooks_screen") {
                        MyPhysicalBooksScreen(navController = navController)
                    }
                    composable("bookDetails/{isbn13}") { backStackEntry ->
                        val isbn13 = backStackEntry.arguments?.getString("isbn13")
                        if (isbn13 != null) {
                            BookDetailsScreen(navController = navController, isbn13 = isbn13)
                        } else {
                        }
                    }
                    composable("readDetails/{isbn13}") { backStackEntry ->
                        val isbn13 = backStackEntry.arguments?.getString("isbn13")
                        if (isbn13 != null) {
                            ReadDetailsScreen(navController = navController, isbn13 = isbn13, viewModel = booksViewModel)
                        } else {
                        }
                    }
                    composable("myPhysicalBooksDetails/{isbn13}") { backStackEntry ->
                        val isbn13 = backStackEntry.arguments?.getString("isbn13")
                        if (isbn13 != null) {
                            MyPhysicalBooksDetailsScreen(navController = navController, isbn13 = isbn13)
                        } else {
                        }
                    }
                    composable("toBeReadDetails/{isbn13}") { backStackEntry ->
                        val isbn13 = backStackEntry.arguments?.getString("isbn13")
                        if (isbn13 != null) {
                            ToBeReadDetailsScreen(navController = navController, isbn13 = isbn13)
                        } else {
                        }
                    }
                    composable("currentlyReadingDetails/{isbn13}") { backStackEntry ->
                        val isbn13 = backStackEntry.arguments?.getString("isbn13")
                        if (isbn13 != null) {
                            CurrentlyReadingDetailsScreen(navController = navController, isbn13 = isbn13)
                        } else {
                        }
                    }
                    composable("barcode_screen") {
                        BarcodeScreen(navController = navController, viewModel = booksViewModel)
                    }
                }
            }
        }
    }
}
