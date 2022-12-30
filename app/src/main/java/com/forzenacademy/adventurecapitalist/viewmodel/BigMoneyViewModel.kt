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
import kotlin.system.measureTimeMillis

class BigMoneyViewModel : ViewModel() {

    private val upgradeRepository: UpgradeRepository = UpgradeRepositoryImpl()

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
                        ((deltaTime + this.lastTimeOffset) / rateMs).toBigDecimal() * magnitude
                    }
                }

                val newMoney = moneyValues.fold(BigDecimal(0)) { acc, i -> acc + i }
                return lastKnownMoney.add(newMoney)
            }

        val progressValues: Map<VentureType, Float>
            get() {
                val deltaTime = currentTime - lastStateChangeTimestamp
                return ventureData.ventureMap.keys.associateWith { key ->
                    ventureData.ventureMap[key]!!.run {
                        ((deltaTime + lastTimeOffset) % rateMs) / (rateMs).toFloat()
                    }
                }
            }

        val offsetValues: Map<VentureType, Long>
            get() {
                val deltaTime = currentTime - lastStateChangeTimestamp
                return ventureData.ventureMap.keys.associateWith { key ->
                    ventureData.ventureMap[key]!!.run {
                        ((deltaTime + lastTimeOffset) % rateMs)
                    }
                }
            }
    }

    private var _ventureData = VentureData(
        mapOf(
            VentureType.LEMON to Lemon(upgradeRepository, 1),
            VentureType.NEWSPAPER to Newspaper(upgradeRepository, 0),
            VentureType.CAR_WASH to CarWash(upgradeRepository, 0),
            VentureType.PIZZA to Pizza(upgradeRepository, 0),
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
            upgradeRepository.addQty(VentureType.LEMON)
            while (true) {
                val timeMs = measureTimeMillis {
                    updateStateTime()
                }
                delay(33 - timeMs)
            }
        }
    }

    fun onUnlock(venture: Venture) {
        rasterizeState()
        val totalMoney = _state.value.totalMoney - venture.unlockCost
        val ventureMap = _state.value.ventureData.ventureMap
        _state.value = State(
            ventureData = VentureData(
                ventureMap.toMutableMap().also {
                    it[venture.type] = venture.copy(lastTimeOffset = 0)
                }
            ),
            currentTime = System.currentTimeMillis(),
            lastKnownMoney = totalMoney,
            lastStateChangeTimestamp = System.currentTimeMillis(),
        )
        upgradeRepository.addQty(venture.type)
    }

    fun onBuyUpgrade(upgrade: PurchasableUpgrade) {
        rasterizeState()
        val totalMoney = _state.value.totalMoney - upgrade.cost
        _state.value = State(
            ventureData = _state.value.ventureData,
            currentTime = System.currentTimeMillis(),
            lastKnownMoney = totalMoney,
            lastStateChangeTimestamp = System.currentTimeMillis(),
        )
        upgradeRepository.add(upgrade.key)
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
