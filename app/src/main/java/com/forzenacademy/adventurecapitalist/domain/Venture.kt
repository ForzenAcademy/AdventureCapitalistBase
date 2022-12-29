package com.forzenacademy.adventurecapitalist.domain

import java.math.BigDecimal

abstract class Venture(
    val type: VentureType,
    val upgrades: UpgradeRepository,
    val lastTimeOffset: Long,
) {

    abstract val rateMs: Long

    abstract val magnitude: BigDecimal

    abstract val upgradeCost: BigDecimal

    abstract val unlocked: Boolean

    fun canPurchase(money: BigDecimal): Boolean {
        return money >= upgradeCost
    }

    abstract fun copy(
        lastTimeOffset: Long = this.lastTimeOffset
    ): Venture

}

class Lemon(upgrades: UpgradeRepository, lastTimeOffset: Long = 0) :
    Venture(VentureType.LEMON, upgrades, lastTimeOffset) {

    override val rateMs: Long
        get() = 2000

    override val magnitude: BigDecimal
        get() = BigDecimal(1 * upgrades.quantity(type))

    override val upgradeCost: BigDecimal
        get() = BigDecimal(3 * (upgrades.quantity(type) + 1))

    override val unlocked: Boolean
        get() = upgrades.quantity(type) > 0

    override fun copy(lastTimeOffset: Long): Lemon = Lemon(upgrades, lastTimeOffset)
}

class Newspaper(upgrades: UpgradeRepository, lastTimeOffset: Long = 0) :
    Venture(VentureType.NEWSPAPER, upgrades, lastTimeOffset) {

    override val rateMs: Long
        get() = 5000

    override val magnitude: BigDecimal
        get() = BigDecimal(5 * upgrades.quantity(type))

    override val upgradeCost: BigDecimal
        get() = BigDecimal(5 * (upgrades.quantity(type) + 1))

    override val unlocked: Boolean
        get() = upgrades.quantity(type) > 0

    override fun copy(lastTimeOffset: Long): Newspaper = Newspaper(upgrades, lastTimeOffset)
}

class CarWash(upgrades: UpgradeRepository, lastTimeOffset: Long = 0) :
    Venture(VentureType.CAR_WASH, upgrades, lastTimeOffset) {

    override val rateMs: Long
        get() = 11000

    override val magnitude: BigDecimal
        get() = BigDecimal(16 * upgrades.quantity(type))

    override val upgradeCost: BigDecimal
        get() = BigDecimal(10 * (upgrades.quantity(type) + 1))

    override val unlocked: Boolean
        get() = upgrades.quantity(type) > 0

    override fun copy(lastTimeOffset: Long): CarWash = CarWash(upgrades, lastTimeOffset)
}

class Pizza(upgrades: UpgradeRepository, lastTimeOffset: Long = 0) :
    Venture(VentureType.PIZZA, upgrades, lastTimeOffset) {

    override val rateMs: Long
        get() = 20000

    override val magnitude: BigDecimal
        get() = BigDecimal(40 * upgrades.quantity(type))

    override val upgradeCost: BigDecimal
        get() = BigDecimal(50 * (upgrades.quantity(type) + 1))

    override val unlocked: Boolean
        get() = upgrades.quantity(type) > 0

    override fun copy(lastTimeOffset: Long): Pizza = Pizza(upgrades, lastTimeOffset)
}

enum class VentureType {
    LEMON, NEWSPAPER, CAR_WASH, PIZZA
}

data class VentureData(
    val ventureMap: Map<VentureType, Venture>
)

private fun UpgradeRepository.quantity(type: VentureType): Int =
    all().filter { it.startsWith(type.name + "_LEVEL_") }.size

fun UpgradeRepository.addQty(type: VentureType) {
    add(type.name + "_LEVEL_" + quantity(type))
}

fun Venture.size() = upgrades.quantity(type)