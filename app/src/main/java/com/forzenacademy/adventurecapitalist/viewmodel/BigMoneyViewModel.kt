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
        val totalMoney: BigDecimal
            get() {
                val deltaTime = currentTime - lastStateChangeTimestamp
                val moneyValues = ventureData.ventureMap.keys.map {
                    ventureData.ventureMap[it]!!.run {
                        ((deltaTime + this.lastTimeOffset) / calculatedRateMs) * calculatedMagnitude
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
