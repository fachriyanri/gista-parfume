package com.example.gistaparfume.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
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
fun Footer(widthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact) {
    val isCompact = widthSizeClass == WindowWidthSizeClass.Compact

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF990000))
            .padding(16.dp)
    ) {
        if (isCompact) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // METODE PEMBAYARAN
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Metode Pembayaran", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_bca),
                            contentDescription = "BCA",
                            modifier = Modifier.size(32.dp)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.logo_bri),
                            contentDescription = "BRI",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // BAGIAN ROW BAWAH: AKSES CEPAT - LOGO PARFUM - HUBUNGI KAMI
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    // KIRI - AKSES CEPAT
                    Column(
                        modifier = Modifier.weight(1.2f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Akses Cepat", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                        Text("Home", fontSize = 11.sp, color = Color.Black)
                        Text("Keranjang", fontSize = 11.sp, color = Color.Black)
                        Text("Status Order", fontSize = 11.sp, color = Color.Black)
                    }

                    // TENGAH - LOGO PARFUM
                    Box(
                        modifier = Modifier
                            .weight(0.6f),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.gista_logo),
                            contentDescription = "Logo Bawah",
                            modifier = Modifier.size(48.dp)
                        )
                    }


                    // KANAN - HUBUNGI KAMI
                    Column(
                        modifier = Modifier
                            .weight(1.2f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text("Hubungi Kami", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                        Text("gistaparfume@gmail.com", fontSize = 11.sp, color = Color.Black)
                        Text("WA: 081382861563", fontSize = 11.sp, color = Color.Black)
                        Text("WA: 082122542628", fontSize = 11.sp, color = Color.Black)
                        Text("FB @GistaParfume", fontSize = 11.sp, color = Color.Black)
                    }
                }
            }
        } else {
            // 💻 TABLET & MEDIUM - Tetap Horizontal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bleau_de_channel),
                    contentDescription = "Logo",
                    modifier = Modifier.size(60.dp),
                    contentScale = ContentScale.Fit
                )
                Column {
                    Text("Metode Pembayaran", fontWeight = FontWeight.Bold, color = Color.Black)
                    Row {
                        Image(
                            painter = painterResource(id = R.drawable.logo_bca),
                            contentDescription = "BCA",
                            modifier = Modifier.size(50.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Image(
                            painter = painterResource(id = R.drawable.logo_bri),
                            contentDescription = "BRI",
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }
                Column {
                    Text("Akses Cepat", fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("Home", color = Color.Black)
                    Text("Keranjang", color = Color.Black)
                    Text("Status Order", color = Color.Black)
                }
                Column {
                    Text("Hubungi Kami", fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("gistaparfume@gmail.com", color = Color.Black)
                    Text("WA: 081382861563", color = Color.Black)
                    Text("WA: 082122542628", color = Color.Black)
                    Text("FB @GistaParfume", color = Color.Black)
                }
            }
        }
    }
}


