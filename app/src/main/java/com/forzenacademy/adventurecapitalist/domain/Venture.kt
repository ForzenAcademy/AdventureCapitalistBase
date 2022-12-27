package com.forzenacademy.adventurecapitalist.domain

import java.math.BigDecimal

abstract class Venture(
    val quantity: Int,
    private val baseMagnitude: Long,
    private val baseRateMs: Long,
    val type: VentureType,
    val lastTimeOffset: Long,
) {
    val calculatedRateMs: Long
        get() = baseRateMs

    val calculatedMagnitude: Long
        get() = baseMagnitude * quantity

    abstract val upgradeCost: BigDecimal

    fun canPurchase(money: BigDecimal): Boolean {
        return money >= upgradeCost
    }

    abstract fun copy(
        quantity: Int = this.quantity,
        lastTimeOffset: Long = this.lastTimeOffset
    ): Venture

}

class Lemon(quantity: Int, lastTimeOffset: Long = 0) :
    Venture(quantity, 1, 2000, VentureType.LEMON, lastTimeOffset) {
    override val upgradeCost: BigDecimal
        get() = BigDecimal(3 * (quantity + 1))

    override fun copy(quantity: Int, lastTimeOffset: Long): Lemon = Lemon(quantity, lastTimeOffset)
}

class Newspaper(quantity: Int, lastTimeOffset: Long = 0) :
    Venture(quantity, 5, 5000, VentureType.NEWSPAPER, lastTimeOffset) {
    override val upgradeCost: BigDecimal
        get() = BigDecimal(5 * (quantity + 1))

    override fun copy(quantity: Int, lastTimeOffset: Long): Newspaper =
        Newspaper(quantity, lastTimeOffset)
}

class CarWash(quantity: Int, lastTimeOffset: Long = 0) :
    Venture(quantity, 16, 11000, VentureType.CAR_WASH, lastTimeOffset) {
    override val upgradeCost: BigDecimal
        get() = BigDecimal(10 * (quantity + 1))

    override fun copy(quantity: Int, lastTimeOffset: Long): CarWash =
        CarWash(quantity, lastTimeOffset)
}

class Pizza(quantity: Int, lastTimeOffset: Long = 0) :
    Venture(quantity, 40, 20000, VentureType.PIZZA, lastTimeOffset) {
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
