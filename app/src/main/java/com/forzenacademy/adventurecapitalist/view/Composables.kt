package com.forzenacademy.adventurecapitalist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forzenacademy.adventurecapitalist.domain.PurchasableUpgrade
import com.forzenacademy.adventurecapitalist.domain.Venture
import com.forzenacademy.adventurecapitalist.domain.size
import com.forzenacademy.adventurecapitalist.viewmodel.BigMoneyViewModel
import java.math.BigDecimal
import java.text.NumberFormat


@Composable
fun Content(state: BigMoneyViewModel.State, viewModel: BigMoneyViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFeeeeee))
            .verticalScroll(rememberScrollState())
    ) {
        val totalMoney = state.totalMoney
        MoneyText(money = totalMoney)
        val progressMap = state.progressValues
        state.ventureData.ventureMap.keys.sortedBy { it.ordinal }.forEach {
            val venture = state.ventureData.ventureMap[it]!!
            VentureBar(
                venture = venture,
                progress = progressMap[it]!!,
                money = totalMoney,
                timeLeftMs = ((1 - progressMap[it]!!) * venture.rateMs).toLong(),
                onBuyUpgrade = { upgrade -> viewModel.onBuyUpgrade(upgrade) },
                onUnlockUpgrade = { venture -> viewModel.onUnlock(venture) }
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
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                modifier = Modifier.padding(4.dp),
                textAlign = TextAlign.Center,
                text = NumberFormat.getCurrencyInstance().format(money),
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
            )
        }
    }
}

@Composable
fun VentureBar(
    venture: Venture,
    progress: Float,
    timeLeftMs: Long,
    money: BigDecimal,
    onBuyUpgrade: (PurchasableUpgrade) -> Unit,
    onUnlockUpgrade: (Venture) -> Unit,
) {
    if (!venture.unlocked) {
        LockedVentureBar(venture, money >= venture.unlockCost, onUnlockUpgrade)
    } else {
        UnlockedVentureBar(venture, progress, timeLeftMs, money, onBuyUpgrade)
    }
}

@Composable
fun LockedVentureBar(
    venture: Venture,
    canUnlock: Boolean,
    onUnlockUpgrade: (Venture) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(16.dp)
        ) {
            Text(text = venture.type.name)
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onUnlockUpgrade(venture) },
                enabled = canUnlock
            ) {
                Text("UNLOCK \$${venture.unlockCost}")
            }
        }
    }
}

@Composable
fun UnlockedVentureBar(
    venture: Venture,
    progress: Float,
    timeLeftMs: Long,
    money: BigDecimal,
    onBuyUpgrade: (PurchasableUpgrade) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = venture.type.name + " x" + venture.size())
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .height(32.dp),
                    progress = progress
                )
                Text(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center),
                    text = "${venture.magnitude}",
                )
                Text(
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(horizontal = 8.dp),
                    text = "${(timeLeftMs / 1000 % 60) + 1}s",
                )
            }
            venture.purchasableUpgrades.forEach {
                Button(
                    onClick = { onBuyUpgrade(it) },
                    enabled = it.canBuy(money)
                ) {
                    Text(it.text + " - $" + it.cost)
                }
            }
        }
    }
}
