package com.forzenacademy.adventurecapitalist

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forzenacademy.adventurecapitalist.game.Venture
import com.forzenacademy.adventurecapitalist.game.VentureType
import java.math.BigDecimal
import java.text.NumberFormat


@Composable
fun Content(state: BigMoneyViewModel.State, viewModel: BigMoneyViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        val totalMoney = state.totalMoney
        MoneyText(money = totalMoney)
        val progressMap = state.progressValues
        state.ventureData.ventureMap.keys.sortedBy { it.ordinal }.forEach {
            val venture = state.ventureData.ventureMap[it]!!
            VentureBar(
                venture,
                progress = progressMap[it]!!,
                venture.canPurchase(totalMoney),
                onClickBuyAnother = { viewModel.onClickBuyAnother(it) }
            )
        }
    }
}

@Composable
fun MoneyText(money: BigDecimal) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = NumberFormat.getCurrencyInstance().format(money),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )
    }
}

@Composable
fun VentureBar(
    venture: Venture,
    progress: Float,
    canBuyAnother: Boolean,
    onClickBuyAnother: (VentureType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = venture.type.name + " x" + venture.quantity)
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            progress = progress
        )
        Button(
            onClick = { onClickBuyAnother(venture.type) },
            enabled = canBuyAnother
        ) {
            Text("BUY ANOTHER \$${venture.upgradeCost}")
        }
    }
}
