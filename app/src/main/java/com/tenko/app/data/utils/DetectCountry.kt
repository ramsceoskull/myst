package com.tenko.app.data.utils

import android.content.Context
import android.telephony.TelephonyManager
import android.util.Log
import com.tenko.app.data.model.Country
import java.util.Locale

fun detectCountry(context: Context, countries: List<Country>): Country {
    val telephonyManager =
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    val simCountry = try {
        telephonyManager.simCountryIso?.uppercase()
    } catch (e: Exception) {
        Log.e("DetectCountry", "Error getting SIM country: ${e.message}")
        null
    }
    val networkCountry = telephonyManager.networkCountryIso?.uppercase()
    val localeCountry = Locale.getDefault().country

    val iso = simCountry ?: networkCountry ?: localeCountry

    return countries.find { it.iso == iso } ?: countries.first()
}