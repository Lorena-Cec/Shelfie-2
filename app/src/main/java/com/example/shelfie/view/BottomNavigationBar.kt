package com.example.shelfie.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.shelfie.ui.theme.LightPurple


@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth(),
        containerColor = LightPurple,
        contentColor = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.navigate("home_screen") },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(35.dp),
                    tint = if (currentRoute == "home_screen") Color.White else Color.Black
                )
            }
            IconButton(
                onClick = { navController.navigate("search_screen") },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Search",
                    modifier = Modifier.size(35.dp),
                    tint = if (currentRoute == "search_screen") Color.White else Color.Black
                )
            }
            IconButton(
                onClick = { navController.navigate("read_screen") },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Filled.List,
                    contentDescription = "Library",
                    modifier = Modifier.size(35.dp),
                    tint = if (currentRoute == "read_screen") Color.White else Color.Black
                )
            }
            IconButton(
                onClick = { navController.navigate("profile_screen") },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(35.dp),
                    tint = if (currentRoute == "profile_screen") Color.White else Color.Black
                )
            }
        }
    }
}