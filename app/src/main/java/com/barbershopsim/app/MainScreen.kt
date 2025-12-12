package com.barbershopsim.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.barbershopsim.app.ui.Customer
import com.barbershopsim.app.ui.HaircutArea
import com.barbershopsim.app.ui.ShopOpenCloseSign
import com.barbershopsim.app.ui.WaitingArea

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val isShopOpen by viewModel.isShopOpen.collectAsState()
    val incomingCustomer by viewModel.customerEnter.collectAsState()
    val outgoingCustomer by viewModel.customerExit.collectAsState()
    val waitingArea by viewModel.waitingArea.collectAsState()
    val haircutArea by viewModel.haircutArea.collectAsState()
    val clock by viewModel.clock.collectAsState()

    Scaffold(content = { padding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    viewModel.shopName,
                    fontSize = 30.sp,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = clock.ifEmpty { "[08:59] " },
                    fontSize = 30.sp,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                ShopOpenCloseSign(isShopOpen)
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Timescale:\n${viewModel.timescaleFactor}",
                    style = TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 13.sp
                    ),
                    modifier = Modifier.widthIn(max = 100.dp).align(Alignment.CenterVertically),
                )
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Customer(outgoingCustomer ?: -1)
                    Text("↖️", fontSize = 26.sp)
                    Text("🚪", fontSize = 40.sp)
                    Text("↗️", fontSize = 26.sp)
                    Customer(incomingCustomer ?: -1)
                }

                Spacer(modifier = Modifier.size(8.dp))

                Column {
                    WaitingArea(waitingArea)
                    Spacer(modifier = Modifier.height(8.dp))
                    HaircutArea(haircutArea)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = { viewModel.start() }) {
                            // TODO: implement pause
                            Text("⏯", fontSize = 32.sp)
                        }
                        Button(onClick = { viewModel.start() }) {
                            Text("↺", fontSize = 32.sp)
                        }
                    }
                }
            }
        }
    })
}

@Preview
@Composable
private fun Preview() {
    MainScreen()
}