package com.example.gistaparfume.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gistaparfume.R

@Composable
fun MainHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF990000))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Selamat Datang di Gista Parfum Jatiwaringin",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Parfum terbaik, akan membawa kamu ke tempat yang luar biasa",
                color = Color.White,
                fontSize = 12.sp
            )
        }

        Image(
            painter = painterResource(id = R.drawable.bleau_de_channel),
            contentDescription = "Header Perfume",
            modifier = Modifier
                .size(64.dp)
                .padding(start = 8.dp),
            contentScale = ContentScale.Fit
        )
    }
}
