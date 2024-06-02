package com.example.shelfie.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shelfie.ui.theme.DarkPurple
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Color

@Composable
fun TopBarWithMenu(
    navController: NavController,
    title: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = DarkPurple)
            .height(70.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(start = 16.dp),
                textAlign = TextAlign.Start,
                fontSize = 24.sp,
                color = Color.White
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 16.dp)
            ) {
                IconButton(onClick = { onExpandedChange(!expanded) }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { onExpandedChange(false) }
                ) {
                    DropdownMenuItem(onClick = {
                        onExpandedChange(false)
                        navController.navigate("myBooks")
                    }) {
                        Text("My Books")
                    }
                    DropdownMenuItem(onClick = {
                        onExpandedChange(false)
                        navController.navigate("myPhysicalBooks")
                    }) {
                        Text("My Physical Books")
                    }
                    DropdownMenuItem(onClick = {
                        onExpandedChange(false)
                        navController.navigate("readBooks")
                    }) {
                        Text("Read Books")
                    }
                    DropdownMenuItem(onClick = {
                        onExpandedChange(false)
                        navController.navigate("toBeReadBooks")
                    }) {
                        Text("To Be Read Books")
                    }
                    DropdownMenuItem(onClick = {
                        onExpandedChange(false)
                        navController.navigate("currentlyReading")
                    }) {
                        Text("Currently Reading")
                    }
                }
            }
        }
    }
}
