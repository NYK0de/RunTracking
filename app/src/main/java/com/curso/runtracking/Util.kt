package com.curso.runtracking

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat
import com.curso.runtracking.database.RunTracker
import java.text.SimpleDateFormat

fun formatRuns(days: List<RunTracker>, resources: Resources): Spanned {
    val sb = StringBuilder()

    sb.apply {

        append(resources.getString(R.string.title))

        days.forEach {
            append("<br>")
            append(resources.getString(R.string.start_time))
            append("\t${convertLongToDateString(it.startRunTimeMilli)}<br>")
            if (it.endRunTimeMilli != it.startRunTimeMilli) {
                append(resources.getString(R.string.end_time))
                append("\t${convertLongToDateString(it.endRunTimeMilli)}<br>")
                append(resources.getString(R.string.quality))
                append("\t${convertNumericQualityToString(it.runEvaluation, resources)}<br>")
                append(resources.getString(R.string.distance))
                append("\t${it.runDistance} kms <br>")
                append(resources.getString(R.string.hours_runned))
                // Hours
                append("\t ${it.endRunTimeMilli.minus(it.startRunTimeMilli) / 1000 / 60 / 60}:")
                // Minutes
                append("${it.endRunTimeMilli.minus(it.startRunTimeMilli) / 1000 / 60}:")
                // Seconds
                append("${it.endRunTimeMilli.minus(it.startRunTimeMilli) / 1000}<br><br>")
            }
        }
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
    } else {
        return HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}

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