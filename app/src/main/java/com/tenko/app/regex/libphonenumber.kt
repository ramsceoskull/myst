package com.tenko.app.regex

import com.google.i18n.phonenumbers.PhoneNumberUtil

fun formatAsYouType(
    raw: String,
    regionCode: String
): String {
    val formatter = PhoneNumberUtil.getInstance()
        .getAsYouTypeFormatter(regionCode)

    var result = ""
    raw.filter { it.isDigit() }.forEach {
        result = formatter.inputDigit(it)
    }
    return result
}

fun isValidNumber(
    number: String,
    regionCode: String
): Boolean {
    return try {
        val phoneUtil = PhoneNumberUtil.getInstance()
        val parsed = phoneUtil.parse(number, regionCode)
        phoneUtil.isValidNumber(parsed)
    } catch (e: Exception) {
        false
    }
}