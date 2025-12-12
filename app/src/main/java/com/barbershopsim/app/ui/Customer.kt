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
fun Customer(customer: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(Color.DarkGray)
            .widthIn(min = 36.dp)
            .heightIn(min = 36.dp)
    ) {
        Text(
            text = if (customer == -1) "" else customer.toString().padStart(2, '0'),
            fontSize = 16.sp,
            color = Color.Cyan,
        )
    }
}

@Preview
@Composable
private fun Preview() = Customer(1)