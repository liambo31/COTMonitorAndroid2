package com.kianosh.cotmonitor

import android.content.SharedPreferences
import java.util.Locale
import kotlin.math.round

enum class Side { BUY, SELL }
data class Tick(val price: Double, val size: Int, val side: Side, val time: Long)
data class Snapshot(
    val symbol: String, val high: Double, val low: Double, val highPrice: Double, val lowPrice: Double,
    val price: Double, val volume: Double, val tickCount: Int, val history: List<Pair<Double, Double>>, val trades: List<Tick>
)

/** The calculation mirrors Quantower's decompiled IndicatorCOT exactly. */
class CotEngine(private val prefs: SharedPreferences) {
    var symbol: String = prefs.getString("symbol", "NQ") ?: "NQ"
    private var highPrice = prefs.getFloat("highPrice", 0f).toDouble()
    private var lowPrice = prefs.getFloat("lowPrice", 0f).toDouble()
    private var high = prefs.getFloat("high", 0f).toDouble()
    private var low = prefs.getFloat("low", 0f).toDouble()
    private var price = prefs.getFloat("price", 0f).toDouble()
    private var volume = prefs.getFloat("volume", 0f).toDouble()
    private var tickCount = prefs.getInt("tickCount", 0)
    private val history = parseHistory(prefs.getString("history", "") ?: "").toMutableList()
    private val trades = parseTrades(prefs.getString("trades", "") ?: "").toMutableList()

    fun ingest(newPrice: Double, size: Int, side: Side): Snapshot {
        require(newPrice.isFinite() && size > 0)
        if (highPrice <= newPrice || highPrice == 0.0) { highPrice = newPrice; high = 0.0 }
        else { high += if (side == Side.BUY) size else -size }
        if (lowPrice >= newPrice || lowPrice == 0.0) { lowPrice = newPrice; low = 0.0 }
        else { low += if (side == Side.BUY) size else -size }
        price = newPrice; volume += if (side == Side.BUY) size else -size; tickCount++
        history.add(high to low); while (history.size > 60) history.removeAt(0)
        trades.add(0, Tick(newPrice, size, side, System.currentTimeMillis())); while (trades.size > 8) trades.removeAt(trades.lastIndex)
        persist(); return snapshot()
    }

    fun snapshot(): Snapshot = Snapshot(symbol, high, low, highPrice, lowPrice, price, volume, tickCount, history.toList(), trades.toList())

    fun reset() { prefs.edit().clear().apply(); symbol = "NQ"; highPrice=0.0; lowPrice=0.0; high=0.0; low=0.0; price=0.0; volume=0.0; tickCount=0; history.clear(); trades.clear() }

    private fun persist() {
        prefs.edit().putString("symbol", symbol).putFloat("highPrice", highPrice.toFloat()).putFloat("lowPrice", lowPrice.toFloat())
            .putFloat("high", high.toFloat()).putFloat("low", low.toFloat()).putFloat("price", price.toFloat()).putFloat("volume", volume.toFloat())
            .putInt("tickCount", tickCount).putString("history", history.joinToString("|") { "${it.first},${it.second}" })
            .putString("trades", trades.joinToString(";") { "${it.price},${it.size},${it.side.name},${it.time}" }).apply()
    }
    private fun parseHistory(raw: String) = raw.split("|").mapNotNull { p -> p.split(",").takeIf { it.size==2 }?.let { it[0].toDoubleOrNull()?.let { a -> it[1].toDoubleOrNull()?.let { b -> a to b } } } }
    private fun parseTrades(raw: String) = raw.split(";").mapNotNull { p -> p.split(",").takeIf { it.size==4 }?.let { a -> val side=runCatching{Side.valueOf(a[2])}.getOrNull(); val pr=a[0].toDoubleOrNull(); val sz=a[1].toIntOrNull(); val tm=a[3].toLongOrNull(); if(side!=null&&pr!=null&&sz!=null&&tm!=null) Tick(pr,sz,side,tm) else null } }.toMutableList()
}

fun nf(value: Double): String = String.format(Locale.US, "%,.2f", value)
