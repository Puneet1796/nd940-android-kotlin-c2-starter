package com.udacity.asteroidradar

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

fun getNextSevenDaysFormattedDates(): ArrayList<String> {
    val formattedDateList = ArrayList<String>()

    val calendar = Calendar.getInstance()
    for (i in 0..Constants.DEFAULT_END_DATE_DAYS) {
        val currentTime = calendar.time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        formattedDateList.add(dateFormat.format(currentTime))
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }

    return formattedDateList
}

fun getToday(): Pair<String, String> {
    val list = getNextSevenDaysFormattedDates()
    return list.first() to list.first()
}

fun getPreviousDay(): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = System.currentTimeMillis()
    calendar.add(Calendar.DAY_OF_MONTH, -1)
    val previousDay = calendar.timeInMillis
    val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.ROOT)
    return dateFormat.format(previousDay)
}