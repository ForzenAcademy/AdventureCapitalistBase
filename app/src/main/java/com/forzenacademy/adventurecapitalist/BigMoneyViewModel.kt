package com.forzenacademy.adventurecapitalist

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forzenacademy.adventurecapitalist.game.Lemon
import com.forzenacademy.adventurecapitalist.game.Newspaper
import com.forzenacademy.adventurecapitalist.game.VentureData
import com.forzenacademy.adventurecapitalist.game.VentureType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal

class BigMoneyViewModel : ViewModel() {

    // TODO store last state of bar when timestamp updates
    data class State(
        val ventureData: VentureData,
        val currentTime: Long = System.currentTimeMillis(),
        val lastKnownMoney: BigDecimal = BigDecimal(0.0),
        val lastStateChangeTimestamp: Long = System.currentTimeMillis(),
    ) {
        val totalMoney: BigDecimal
            get() {
                val deltaTime = currentTime - lastStateChangeTimestamp
                val moneyValues = ventureData.ventureMap.keys.map {
                    ventureData.ventureMap[it]!!.run {
                        ((deltaTime + this.lastTimeOffset) / (calculatedRateMs)) * calculatedMagnitude * quantity
                    }
                }

                val newMoney = moneyValues.sum()
                return lastKnownMoney.add(BigDecimal(newMoney)) // TODO wrap all math
            }

        val progressValues: Map<VentureType, Float>
            get() {
                val deltaTime = currentTime - lastStateChangeTimestamp
                return ventureData.ventureMap.keys.associateWith { key ->
                    ventureData.ventureMap[key]!!.run {
                        ((deltaTime + lastTimeOffset) % calculatedRateMs) / (calculatedRateMs).toFloat()
                    }
                }
            }
    }

    private var _ventureData = VentureData(
        mapOf(
            VentureType.LEMON to Lemon(1, 0),
            VentureType.NEWSPAPER to Newspaper(1, 0)
        )
    )

    private var _state = mutableStateOf(State(_ventureData))
    val state: MutableState<State>
        get() = _state

    private fun updateStateTime() {
        _state.value = _state.value.copy(
            currentTime = System.currentTimeMillis()
        )
    }

    private var gameLoopJob: Job? = null

    fun startGameLoop() {
        if (gameLoopJob != null) return
        gameLoopJob = viewModelScope.launch {
            while (true) {
                delay(33)
                updateStateTime()
            }
        }
    }

    fun onClickBuyAnother(type: VentureType) {
        val ventureMap = _state.value.ventureData.ventureMap
        val current = when (type) {
            VentureType.LEMON -> ventureMap[type] as Lemon
            VentureType.NEWSPAPER -> ventureMap[type] as Newspaper
        }
        val totalMoney = _state.value.totalMoney - current.upgradeCost
        val progressValues = _state.value.progressValues
        _state.value = State(
            ventureData = VentureData(
                ventureMap.toMutableMap().run {
                    this[type] = current.run {
                        this.copy(
                            quantity = this.quantity + 1,
                            lastTimeOffset = (progressValues[this.type]!! * this.calculatedRateMs).toLong()
                        )
                    }
                    this
                }
            ),
            currentTime = System.currentTimeMillis(),
            lastKnownMoney = totalMoney,
            lastStateChangeTimestamp = System.currentTimeMillis(),
        )

        // Need to make a different operation to update the offsets
    }

}
