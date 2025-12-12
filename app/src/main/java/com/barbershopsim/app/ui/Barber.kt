package com.barbershopsim.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Barber(barber: String?) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(Color.Blue)
            .widthIn(min = 36.dp)
            .heightIn(min = 36.dp)
    ) {
        Text(
            text = barber.orEmpty(),
            fontSize = 16.sp,
            color = Color.Yellow,
        )
    }
}

@Preview
@Composable
private fun Preview() = Barber("A")