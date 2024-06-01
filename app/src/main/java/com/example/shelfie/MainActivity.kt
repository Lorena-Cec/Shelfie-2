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
import androidx.navigation.compose.rememberNavController
import com.example.shelfie.view.HomeScreen
import com.example.shelfie.view.LoginScreen
import com.example.shelfie.view.RegisterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShelfieTheme {
                val mainViewModel: MainViewModel by viewModels()
                val navController = rememberNavController()
                HomeScreen(navController)
            }
        }
    }
}
