package com.forzenacademy.adventurecapitalist.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forzenacademy.adventurecapitalist.domain.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal

class BigMoneyViewModel : ViewModel() {

    data class State(
        val ventureData: VentureData,
        val currentTime: Long = System.currentTimeMillis(),
        val lastKnownMoney: BigDecimal = BigDecimal(0.0),
        val lastStateChangeTimestamp: Long = System.currentTimeMillis(),
    ) {
        /**
         * returns a bigdecimal sum of lastknown money and what the current calculated money should be
         * calculates this by getting a list of venture types and then useing those venture types in the
         * venture data's map to get the values of each venture, it then calculates the value by delta time and rate by magnitude
         */
        val totalMoney: BigDecimal
            get() {
                return ventureData.ventureMap.keys.map {
                    ventureData.ventureMap[it]!!.run {
                        calculatedMagnitude.multiply(BigDecimal((currentTime - lastStateChangeTimestamp + this.lastTimeOffset) / calculatedRateMs))
                    }
                }.sum().add(lastKnownMoney)
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

        val offsetValues: Map<VentureType, Long>
            get() {
                val deltaTime = currentTime - lastStateChangeTimestamp
                return ventureData.ventureMap.keys.associateWith { key ->
                    ventureData.ventureMap[key]!!.run {
                        ((deltaTime + lastTimeOffset) % calculatedRateMs)
                    }
                }
            }
    }

    private var _ventureData = VentureData(
        mapOf(
            VentureType.LEMON to Lemon(1),
            VentureType.NEWSPAPER to Newspaper(0),
            VentureType.CAR_WASH to CarWash(0),
            VentureType.PIZZA to Pizza(0),
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
        rasterizeState()
        val ventureMap = _state.value.ventureData.ventureMap
        val current = when (type) {
            VentureType.LEMON -> ventureMap[type] as Lemon
            VentureType.NEWSPAPER -> ventureMap[type] as Newspaper
            VentureType.CAR_WASH -> ventureMap[type] as CarWash
            VentureType.PIZZA -> ventureMap[type] as Pizza
        }
        val totalMoney = _state.value.totalMoney - current.upgradeCost
        _state.value = State(
            ventureData = VentureData(
                ventureMap.toMutableMap().run {
                    this[type] = current.run {
                        this.copy(
                            quantity = quantity + 1,
                            lastTimeOffset = if (quantity == 0) 0 else lastTimeOffset
                        )
                    }
                    this
                }
            ),
            currentTime = System.currentTimeMillis(),
            lastKnownMoney = totalMoney,
            lastStateChangeTimestamp = System.currentTimeMillis(),
        )
    }

    private fun rasterizeState() {
        val totalMoney = _state.value.totalMoney
        val ventureMap = _state.value.ventureData.ventureMap
        val offsetValues = _state.value.offsetValues
        _state.value = State(
            ventureData = VentureData(
                ventureMap.toMutableMap().run {
                    keys.forEach { key ->
                        val v = this[key]!!
                        this[key] = v.copy(
                            lastTimeOffset = offsetValues[v.type]!!
                        )
                    }
                    this
                }
            ),
            currentTime = System.currentTimeMillis(),
            lastKnownMoney = totalMoney,
            lastStateChangeTimestamp = System.currentTimeMillis(),
        )
    }

}

private fun List<BigDecimal>.sum(): BigDecimal {
    var sum: BigDecimal = BigDecimal.ZERO
    for (element in this) {
        sum += element
    }
    return sum
}