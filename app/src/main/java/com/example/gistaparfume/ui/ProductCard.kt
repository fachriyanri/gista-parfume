package com.example.gistaparfume.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gistaparfume.data.Product
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductCard(
    product: Product,
    onAddToCart: (Product, Int) -> Unit,
    widthSizeClass: WindowWidthSizeClass
) {
    // 1. Definisi config data class lokal
    data class CardStyle(
        val padding: Dp,
        val imageHeight: Dp,
        val titleSize: TextUnit,
        val priceSize: TextUnit,
        val badgeFont: TextUnit,
        val qtyWidth: Dp,
        val fieldHeight: Dp,
        val addToCartHeight: Dp,
        val addToCartWidth: Dp,
        val topSpacing:Dp,
        val btnText: String
    )

    // 2. Assign config based on widthSizeClass
    val style = when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> CardStyle(
            padding = 4.dp,
            imageHeight = 120.dp,
            titleSize = 12.sp,
            priceSize = 12.sp,
            badgeFont = 10.sp,
            qtyWidth = 60.dp,
            fieldHeight = 40.dp,
            addToCartHeight = 36.dp,
            addToCartWidth = 100.dp,
            topSpacing = 0.dp,
            btnText = "Tambah\nKe Keranjang"
        )

        WindowWidthSizeClass.Medium -> CardStyle(
            padding = 6.dp,
            imageHeight = 150.dp,
            titleSize = 14.sp,
            priceSize = 13.sp,
            badgeFont = 11.sp,
            qtyWidth = 70.dp,
            fieldHeight = 40.dp,
            addToCartHeight = 36.dp,
            addToCartWidth = 150.dp,
            topSpacing = 0.dp,
            btnText = "Tambah Ke Keranjang"
        )

        else /*Expanded*/ -> CardStyle(
            padding = 8.dp,
            imageHeight = 180.dp,
            titleSize = 16.sp,
            priceSize = 14.sp,
            badgeFont = 12.sp,
            qtyWidth = 80.dp,
            fieldHeight = 40.dp, // 40.dp
            addToCartHeight = 40.dp,
            addToCartWidth = 150.dp,
            topSpacing = 60.dp,
            btnText = "Tambah Ke Keranjang"
        )
    }

    // 3. Pakai style.xxx di layout
    var quantity by remember { mutableIntStateOf(1) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(style.padding),
        shape = RoundedCornerShape(if (widthSizeClass == WindowWidthSizeClass.Compact) 6.dp else 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (widthSizeClass == WindowWidthSizeClass.Compact) 2.dp else 4.dp)
    ) {
        Column(modifier = Modifier.padding(style.padding)) {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(style.imageHeight),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = product.title,
                fontSize = style.titleSize,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = formatPrice(product.price),
                fontSize = style.priceSize,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(2.dp))
            Badge(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Text(product.category, fontSize = style.badgeFont)
            }
            Spacer(Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = quantity.toString(),
                    onValueChange = { quantity = it.toIntOrNull()?.coerceAtLeast(1) ?: 1 },
                    modifier = Modifier
                        .width(style.qtyWidth)
                        .height(style.fieldHeight),
                    singleLine = true,
                    label = { Text("Qty", fontSize = style.badgeFont) },
                    textStyle = LocalTextStyle.current.copy(fontSize = style.badgeFont)
                )
                Spacer(Modifier.width(4.dp))
                Button(
                    onClick = { onAddToCart(product, quantity) },
                    modifier = Modifier
                        .height(style.addToCartHeight)
                        .width(style.addToCartWidth),
                    contentPadding = PaddingValues(horizontal = style.padding)
                ) {
                    Text(
                        text = style.btnText,
                        fontSize = style.badgeFont,
                        maxLines = 2,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

fun formatPrice(price: Int): String {
    val formatter = NumberFormat.getInstance(Locale("id", "ID"))
    return "Rp ${formatter.format(price)},-"
}
