package com.forzenacademy.adventurecapitalist.game

import java.math.BigDecimal

abstract class Venture(
    val quantity: Int,
    val baseMagnitude: Long,
    val baseRateMs: Long,
    val type: VentureType,
    val lastTimeOffset: Long,
) {
    val calculatedRateMs: Long
        get() = baseRateMs // TODO flesh out

    val calculatedMagnitude: Long
        get() = baseMagnitude // TODO flesh out

    abstract val upgradeCost: BigDecimal

    fun canPurchase(money: BigDecimal): Boolean {
        return money >= upgradeCost
    }

    abstract fun copy(
        quantity: Int = this.quantity,
        lastTimeOffset: Long = this.lastTimeOffset
    ): Venture

}

class Lemon(quantity: Int, lastTimeOffset: Long) :
    Venture(quantity, 1, 5000, VentureType.LEMON, lastTimeOffset) {
    override val upgradeCost: BigDecimal
        get() = BigDecimal(3 * quantity)

    override fun copy(quantity: Int, lastTimeOffset: Long): Lemon = Lemon(quantity, lastTimeOffset)
}

class Newspaper(quantity: Int, lastTimeOffset: Long) :
    Venture(quantity, 3, 8000, VentureType.NEWSPAPER, lastTimeOffset) {
    override val upgradeCost: BigDecimal
        get() = BigDecimal(5 * quantity)

    override fun copy(quantity: Int, lastTimeOffset: Long): Newspaper =
        Newspaper(quantity, lastTimeOffset)
}

enum class VentureType {
    LEMON, NEWSPAPER
}

data class VentureData(
    val ventureMap: Map<VentureType, Venture>
)
