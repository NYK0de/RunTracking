package com.curso.runtracking.runtracking

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.curso.runtracking.convertLongToDateString
import com.curso.runtracking.convertNumericEvaluationToDrawable
import com.curso.runtracking.database.RunTracker
import kotlinx.android.synthetic.main.fragment_run_evaluation.view.*


@BindingAdapter("runDistance")
fun TextView.setRunDistance(item: RunTracker?){
    item?.let {
        text = "${item.runDistance} Kms"
    }
}
@BindingAdapter("iniHour")
fun TextView.setIniHour(item: RunTracker?){
    item?.let {
        text = convertLongToDateString(item.startRunTimeMilli)
    }
}
@BindingAdapter("endHour")
fun TextView.setEndHour(item: RunTracker?){
    item?.let {
        text = convertLongToDateString(item.endRunTimeMilli)
    }
}

@BindingAdapter("runImage")
fun ImageView.setRunImage(item: RunTracker){
    setImageResource(convertNumericEvaluationToDrawable(item.runEvaluation))
}
