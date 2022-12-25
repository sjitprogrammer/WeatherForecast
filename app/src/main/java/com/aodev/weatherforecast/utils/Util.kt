package com.aodev.weatherforecast.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object Util {

    fun getDayOfMonth(timestamp: Long): String? {
        try {
            return SimpleDateFormat("MMM dd , yyyy", Locale.ENGLISH).format(timestamp * 1000)
        }catch (e:Exception){
            return null
        }
    }

    fun getDayAndHour(timestamp: Long): String {
        return SimpleDateFormat("EE HH:mm", Locale.ENGLISH).format(timestamp * 1000)
    }

    fun getDayOfWeek(timestamp: Long): String {
        return SimpleDateFormat("EEEE", Locale.ENGLISH).format(timestamp * 1000)
    }

    fun getDateTimeAsFormattedString(dateAsLongInMs: Long): String? {
        try {
            return SimpleDateFormat("MMM,dd HH:mm", Locale.ENGLISH).format(dateAsLongInMs * 1000)
        } catch (e: Exception) {
            return null // parsing exception
        }
    }

    fun toFahrenheit(c: Double): Double {
        return (c * 9 / 5) + 32
    }
}