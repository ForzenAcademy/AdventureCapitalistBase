package com.forzenacademy.adventurecapitalist.domain

import java.math.BigDecimal

interface UpgradeRepository {
    fun add(key: String)
    fun remove(key: String)
    fun all(): List<String>
    fun contains(key: String): Boolean
}

class UpgradeRepositoryImpl : UpgradeRepository {

    // Using a Set for no duplicates
    private val keys = mutableSetOf<String>()

    override fun add(key: String) {
        keys.add(key)
    }

    override fun remove(key: String) {
        keys.remove(key)
    }

    override fun all(): List<String> = keys.toList()

    override fun contains(key: String) = keys.contains(key)

}

data class PurchasableUpgrade(val text: String, val key: String, val cost: BigDecimal) {

    fun canBuy(totalMoney: BigDecimal) = cost <= totalMoney

}
