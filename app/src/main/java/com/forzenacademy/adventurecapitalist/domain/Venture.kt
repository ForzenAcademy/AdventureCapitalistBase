package com.forzenacademy.adventurecapitalist.domain

import java.math.BigDecimal
import kotlin.math.pow

abstract class Venture(
    val type: VentureType,
    val upgrades: UpgradeRepository,
    val lastTimeOffset: Long,
) {

    abstract val rateMs: Long

    abstract val magnitude: BigDecimal

    abstract val unlocked: Boolean

    abstract val unlockCost: BigDecimal

    abstract val purchasableUpgrades: List<PurchasableUpgrade>

    abstract fun copy(
        lastTimeOffset: Long = this.lastTimeOffset
    ): Venture

}

class Lemon(upgrades: UpgradeRepository, lastTimeOffset: Long = 0) :
    Venture(VentureType.LEMON, upgrades, lastTimeOffset) {

    override val rateMs: Long
        get() = if (upgrades.contains("BIG_DONG_LEMON")) 1000 else 2000

    override val magnitude: BigDecimal
        get() = BigDecimal(1 * upgrades.quantity(type)) * upgrades.multiplier(this.type) //*global

    override val unlocked: Boolean
        get() = upgrades.quantity(type) > 0

    override val unlockCost: BigDecimal = BigDecimal(3)

    override val purchasableUpgrades: List<PurchasableUpgrade>
        get() = listOf(
            PurchasableUpgrade(
                "+QTY",
                type.name + "_LEVEL_" + size(),
                BigDecimal((3 - (3 * 0)) * (1.1 - 0).pow(upgrades.quantity(type)) - 0)
                //Price = (B- B*BCE) * ((1.1-CCU)^(BC-BCU))
            ),
        ) + if (!upgrades.contains("BIG_DONG_LEMON") && size() > 3) listOf(
            PurchasableUpgrade(
                "BIG LEMON ENERGY BRO",
                "BIG_DONG_LEMON",
                BigDecimal(100)
            )
        ) else listOf()

    override fun copy(lastTimeOffset: Long): Lemon = Lemon(upgrades, lastTimeOffset)
}

class Newspaper(upgrades: UpgradeRepository, lastTimeOffset: Long = 0) :
    Venture(VentureType.NEWSPAPER, upgrades, lastTimeOffset) {

    override val rateMs: Long
        get() = 5000

    override val magnitude: BigDecimal
        get() = BigDecimal(5 * upgrades.quantity(type)) * upgrades.multiplier(this.type)

    override val unlocked: Boolean
        get() = upgrades.quantity(type) > 0

    override val unlockCost: BigDecimal = BigDecimal(5)

    override val purchasableUpgrades: List<PurchasableUpgrade>
        get() = listOf(
            PurchasableUpgrade(
                "+QTY",
                type.name + "_LEVEL_" + size(),
                BigDecimal((5 - (5 * 0)) * (1.1 - 0).pow(upgrades.quantity(type)) - 0)
            )
        )

    override fun copy(lastTimeOffset: Long): Newspaper = Newspaper(upgrades, lastTimeOffset)
}

class CarWash(upgrades: UpgradeRepository, lastTimeOffset: Long = 0) :
    Venture(VentureType.CAR_WASH, upgrades, lastTimeOffset) {

    override val rateMs: Long
        get() = 11000

    override val magnitude: BigDecimal
        get() = BigDecimal(16 * upgrades.quantity(type)) * upgrades.multiplier(this.type)

    override val unlocked: Boolean
        get() = upgrades.quantity(type) > 0

    override val unlockCost: BigDecimal = BigDecimal(10)

    override val purchasableUpgrades: List<PurchasableUpgrade>
        get() = listOf(
            PurchasableUpgrade(
                "+QTY",
                type.name + "_LEVEL_" + size(),
                BigDecimal((20 - (20 * 0)) * (1.1 - 0).pow(upgrades.quantity(type)) - 0)
            )
        )

    override fun copy(lastTimeOffset: Long): CarWash = CarWash(upgrades, lastTimeOffset)
}

class Pizza(upgrades: UpgradeRepository, lastTimeOffset: Long = 0) :
    Venture(VentureType.PIZZA, upgrades, lastTimeOffset) {

    override val rateMs: Long
        get() = 20000

    override val magnitude: BigDecimal
        get() = BigDecimal(40 * upgrades.quantity(type)) * upgrades.multiplier(this.type)

    override val unlocked: Boolean
        get() = upgrades.quantity(type) > 0

    override val unlockCost: BigDecimal = BigDecimal(30)

    override val purchasableUpgrades: List<PurchasableUpgrade>
        get() = listOf(
            PurchasableUpgrade(
                "+QTY",
                type.name + "_LEVEL_" + size(),
                BigDecimal((50 - (50 * 0)) * (1.1 - 0).pow(upgrades.quantity(type)) - 0)
            )
        )

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

/**
 * at 10 and 1000 specificaly doing a special one time upgrade of 2x and 500x respectively
 * at every increment of 25 50 and 100 we multiply the multiplier by 2 3 and 5 respectively
 * meaning that every 100 of something we have a multiplier of 60x
 * so we can use 60 ^ n where n being how many hundreds is in our quantity
 * to avoid putting extra math the 1000 break point is just 100 but it will be mulitplied by the 100 value so it will
 * equate out to 500
 */
private fun UpgradeRepository.multiplier(type: VentureType): BigDecimal {
    var multiplier = BigDecimal(1)
    val quantity = quantity(type)
    if (quantity >= 10) multiplier *= BigDecimal(2)
    if (quantity >= 1000) multiplier *= BigDecimal(100) //so we don't have to do complex shit to check if were at 1000
    val remainder = quantity % 100
    multiplier *= BigDecimal(60.0.pow(quantity / 100))
    when (true) {
        (remainder >= 75) -> multiplier *= BigDecimal(12)
        (remainder >= 50) -> multiplier *= BigDecimal(6)
        (remainder >= 25) -> multiplier *= BigDecimal(2)
        else -> {}
    }
    return multiplier
}

fun Venture.size() = upgrades.quantity(type)