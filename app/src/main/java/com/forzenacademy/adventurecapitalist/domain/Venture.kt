package com.forzenacademy.adventurecapitalist.domain

import com.forzenacademy.adventurecapitalist.domain.DefaultValues.CAR_WASH_START_MAGNITUDE
import com.forzenacademy.adventurecapitalist.domain.DefaultValues.CAR_WASH_START_RATEMS
import com.forzenacademy.adventurecapitalist.domain.DefaultValues.DEFAULT_OFFSET
import com.forzenacademy.adventurecapitalist.domain.DefaultValues.LEMON_START_MAGNITUDE
import com.forzenacademy.adventurecapitalist.domain.DefaultValues.LEMON_START_RATEMS
import com.forzenacademy.adventurecapitalist.domain.DefaultValues.NEWSPAPER_START_MAGNITUDE
import com.forzenacademy.adventurecapitalist.domain.DefaultValues.NEWSPAPER_START_RATEMS
import com.forzenacademy.adventurecapitalist.domain.DefaultValues.PIZZA_START_MAGNITUDE
import com.forzenacademy.adventurecapitalist.domain.DefaultValues.PIZZA_START_RATEMS
import java.math.BigDecimal

abstract class Venture(
    val quantity: Int,
    private val baseMagnitude: BigDecimal,
    private val baseRateMs: Long,
    val type: VentureType,
    val lastTimeOffset: Long,
) {
    val calculatedRateMs: Long
        get() = baseRateMs

    val calculatedMagnitude: BigDecimal
        get() = baseMagnitude.multiply(BigDecimal(quantity))

    abstract val upgradeCost: BigDecimal

    fun canPurchase(money: BigDecimal): Boolean {
        return money >= upgradeCost
    }

    abstract fun copy(
        quantity: Int = this.quantity,
        lastTimeOffset: Long = this.lastTimeOffset
    ): Venture

}

class Lemon(quantity: Int, lastTimeOffset: Long = DEFAULT_OFFSET) :
    Venture(
        quantity,
        BigDecimal(LEMON_START_MAGNITUDE),
        LEMON_START_RATEMS,
        VentureType.LEMON,
        lastTimeOffset
    ) {
    override val upgradeCost: BigDecimal
        get() = BigDecimal(3 * (quantity + 1))

    override fun copy(quantity: Int, lastTimeOffset: Long): Lemon = Lemon(quantity, lastTimeOffset)
}

class Newspaper(quantity: Int, lastTimeOffset: Long = DEFAULT_OFFSET) :
    Venture(
        quantity,
        BigDecimal(NEWSPAPER_START_MAGNITUDE),
        NEWSPAPER_START_RATEMS,
        VentureType.NEWSPAPER,
        lastTimeOffset
    ) {
    override val upgradeCost: BigDecimal
        get() = BigDecimal(5 * (quantity + 1))

    override fun copy(quantity: Int, lastTimeOffset: Long): Newspaper =
        Newspaper(quantity, lastTimeOffset)
}

class CarWash(quantity: Int, lastTimeOffset: Long = DEFAULT_OFFSET) :
    Venture(
        quantity,
        BigDecimal(CAR_WASH_START_MAGNITUDE),
        CAR_WASH_START_RATEMS,
        VentureType.CAR_WASH,
        lastTimeOffset
    ) {
    override val upgradeCost: BigDecimal
        get() = BigDecimal(10 * (quantity + 1))

    override fun copy(quantity: Int, lastTimeOffset: Long): CarWash =
        CarWash(quantity, lastTimeOffset)
}

class Pizza(quantity: Int, lastTimeOffset: Long = DEFAULT_OFFSET) :
    Venture(
        quantity,
        BigDecimal(PIZZA_START_MAGNITUDE),
        PIZZA_START_RATEMS,
        VentureType.PIZZA,
        lastTimeOffset
    ) {
    override val upgradeCost: BigDecimal
        get() = BigDecimal(50 * (quantity + 1))

    override fun copy(quantity: Int, lastTimeOffset: Long): Pizza =
        Pizza(quantity, lastTimeOffset)
}

enum class VentureType {
    LEMON, NEWSPAPER, CAR_WASH, PIZZA
}

data class VentureData(
    val ventureMap: Map<VentureType, Venture>
)

object DefaultValues {
    const val DEFAULT_OFFSET: Long = 0
    const val LEMON_START_MAGNITUDE = 1
    const val NEWSPAPER_START_MAGNITUDE = 5
    const val CAR_WASH_START_MAGNITUDE = 16
    const val PIZZA_START_MAGNITUDE = 40
    const val DONUT_START_MAGNITUDE = 100
    const val SOCCER_TEAM_START_MAGNITUDE = 225
    const val MINES_START_MAGNITUDE = 500
    const val CORPORATE_OFFICE_START_MAGNITUDE = 1500
    const val LEMON_START_RATEMS: Long = 2000
    const val NEWSPAPER_START_RATEMS: Long = 5000
    const val CAR_WASH_START_RATEMS: Long = 11000
    const val PIZZA_START_RATEMS: Long = 20000
    const val DONUT_START_RATEMS: Long = 2000
    const val SOCCER_TEAM_START_RATEMS: Long = 2000
    const val MINES_START_RATEMS: Long = 2000
    const val CORPORATE_OFFICE_START_RATEMS: Long = 2000
}