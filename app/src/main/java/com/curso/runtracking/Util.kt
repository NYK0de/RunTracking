package com.curso.runtracking

import android.annotation.SuppressLint
import android.content.res.Resources
import java.text.SimpleDateFormat


fun convertNumericQualityToString(quality: Int, resources: Resources): String {
    var qualityString = resources.getString(R.string.three_ok)
    when (quality) {
        -1 -> qualityString = "--"
        0 -> qualityString = resources.getString(R.string.zero_very_bad)
        1 -> qualityString = resources.getString(R.string.one_poor)
        2 -> qualityString = resources.getString(R.string.two_soso)
        4 -> qualityString = resources.getString(R.string.four_pretty_good)
        5 -> qualityString = resources.getString(R.string.five_excellent)
    }
    return qualityString
}

fun convertNumericEvaluationToDrawable(evaluation: Int): Int {
    var qualityString = R.drawable.ic_run_0
    when (evaluation) {
        0 -> qualityString = R.drawable.ic_run_1
        1 -> qualityString = R.drawable.ic_run_2
        2 -> qualityString = R.drawable.ic_run_3
        4 -> qualityString = R.drawable.ic_run_4
        5 -> qualityString = R.drawable.ic_run_5
    }
    return qualityString
}

@SuppressLint("SimpleDateFormat")
fun convertLongToDateString(systemTime: Long): String {
    return SimpleDateFormat("EEEE dd, HH:mm:ss")
        .format(systemTime).toString()
}