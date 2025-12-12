package com.barbershopsim.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.barbershopsim.app.R

@Composable
fun ShopOpenCloseSign(isShopOpen: Boolean) {
    Image(
        painter = painterResource(if (isShopOpen) R.drawable.open_sign else R.drawable.closed_sign),
        contentDescription = null,
        modifier = Modifier.size(100.dp),
        contentScale = ContentScale.Fit,
        alignment = Alignment.Center
    )
}

@Preview
@Composable
private fun Preview() {
    Column {
        Box(modifier = Modifier.border(width = 2.dp, color = Color.Black)) {
            ShopOpenCloseSign(true)
        }
        Box(modifier = Modifier.border(width = 2.dp, color = Color.Black)) {
            ShopOpenCloseSign(false)
        }
    }
}