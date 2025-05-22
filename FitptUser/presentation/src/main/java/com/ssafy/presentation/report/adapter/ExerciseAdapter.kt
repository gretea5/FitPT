package com.ssafy.presentation.report.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.domain.model.report.ReportExercise
import com.ssafy.presentation.databinding.ItemExerciseBinding


class ExerciseReportAdapter(
    private val items: List<ReportExercise>
) : RecyclerView.Adapter<ExerciseReportAdapter.ExerciseViewHolder>() {

    inner class ExerciseViewHolder(private val binding: ItemExerciseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReportExercise) {
            binding.tvExerciseName.text = item.exerciseName
            binding.tvExerciseDescription.text = item.exerciseComment
            binding.tvExerciseAchievement.text = item.exerciseAchievement
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ItemExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}