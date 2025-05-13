package com.ssafy.presentation.report.adapter

import android.os.Build.VERSION_CODES.P
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.domain.model.report.WorkoutItem
import com.ssafy.presentation.databinding.ListItemReportWorkoutBinding

class HealthReportAdapter(
    var items: MutableList<WorkoutItem>,
    private val onItemChanged: () -> Unit
) : RecyclerView.Adapter<HealthReportAdapter.HealthReportViewHolder>() {

    inner class HealthReportViewHolder(private val binding: ListItemReportWorkoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WorkoutItem) {
            if (item.isEditing) {
                binding.apply {
                    clReportWorkoutItemEditMode.visibility = View.VISIBLE
                    clReportWorkoutItemViewMode.visibility = View.GONE

                    tvReportWorkoutEditName.setText(item.name)
                    tvReportWorkoutEditScore.setText(item.score)

                    binding.tvReportWorkoutEditName.addTextChangedListener {
                        item.name = it.toString()
                        onItemChanged()
                    }

                    binding.tvReportWorkoutEditScore.addTextChangedListener {
                        item.score = it.toString()
                        onItemChanged()
                    }
                }
            } else {
                binding.apply {
                    clReportWorkoutItemEditMode.visibility = View.GONE
                    clReportWorkoutItemViewMode.visibility = View.VISIBLE

                    tvReportWorkoutViewName.setText(item.name)
                    tvReportWorkoutViewScore.setText(item.score)
                }
            }
        }
    }

    fun addEditItem() {
        if (items.lastOrNull()?.isEditing == true) return
        items.add(WorkoutItem("", "", true))
        notifyItemInserted(items.lastIndex)
    }

    fun removeEditModeItem() {
        val index = items.indexOfFirst { it.isEditing }
        if (index != -1) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun finalizeLastItem() {
        items.lastOrNull()?.let {
            it.isEditing = false
            notifyItemChanged(items.lastIndex)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HealthReportViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemReportWorkoutBinding.inflate(inflater, parent, false)
        return HealthReportViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: HealthReportViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
