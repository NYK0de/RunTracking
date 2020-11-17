package com.curso.runtracking.runtracking

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.curso.runtracking.convertLongToDateString
import com.curso.runtracking.convertNumericEvaluationToDrawable
import com.curso.runtracking.database.RunTracker
import com.curso.runtracking.databinding.RecyclerCardBinding

class RunTrackerAdapter :
    ListAdapter<RunTracker, RunTrackerAdapter.MyCustomViewHolder>( RunTrackingDiffCallback() ) {


    // This method do the drawing of each element of the collection
    override fun onBindViewHolder(myCustomHolder: MyCustomViewHolder, position: Int) {
        val item = getItem(position)
        myCustomHolder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCustomViewHolder {
        return MyCustomViewHolder.from(parent)
    }

    // Here we have a custom ViewHolder that is an object that holds a view
    class MyCustomViewHolder private constructor(val binding: RecyclerCardBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: RunTracker ) {
            binding.runImageView.setImageResource(convertNumericEvaluationToDrawable(item.runEvaluation))
            binding.runDistanceTextView.text = item.runDistance.toString()
            binding.horaInicioTextView.text = convertLongToDateString(item.startRunTimeMilli)
            binding.horaFinTextView.text = convertLongToDateString(item.endRunTimeMilli)
        }

        companion object {
            fun from(parent: ViewGroup): MyCustomViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecyclerCardBinding.inflate(layoutInflater, parent, false)
                return MyCustomViewHolder(binding)
            }
        }
    }

    class RunTrackingDiffCallback: DiffUtil.ItemCallback<RunTracker>(){
        override fun areItemsTheSame(oldItem: RunTracker, newItem: RunTracker): Boolean {
             return oldItem.runId == newItem.runId
        }

        override fun areContentsTheSame(oldItem: RunTracker, newItem: RunTracker): Boolean {
            return oldItem == newItem
        }

    }
}