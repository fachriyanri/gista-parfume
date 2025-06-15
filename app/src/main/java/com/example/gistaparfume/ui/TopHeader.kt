package com.example.gistaparfume.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopHeader(widthSizeClass: WindowWidthSizeClass) {
    val isCompact = widthSizeClass == WindowWidthSizeClass.Compact

    if (isCompact) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF990000))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            TitleText(modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NavButtons()
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF990000))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TitleText()
            Row(verticalAlignment = Alignment.CenterVertically) {
                NavButtons()
            }
        }
    }
}

@Composable
fun TitleText(modifier: Modifier = Modifier) {
    Text(
        "Gista Parfum",
        color = Color.White,
        fontSize = 14.sp,
        modifier = modifier
    )
}

@Composable
fun NavButtons() {
    TextButton(onClick = {}) { Text("Home", color = Color.White, fontSize = 12.sp) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {}) {
            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color.White)
        }
        Text("Cart(0)", color = Color.White, fontSize = 12.sp)
    }

    TextButton(onClick = {}) { Text("Login", color = Color.White, fontSize = 12.sp) }
    TextButton(onClick = {}) { Text("Register", color = Color.White, fontSize = 12.sp) }
}


