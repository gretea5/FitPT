package com.ssafy.presentation.report.adapter

import android.os.Build.VERSION_CODES.P
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.domain.model.report.HealthReportWorkout
import com.ssafy.domain.model.report.WorkoutNameScoreItem
import com.ssafy.presentation.databinding.ListItemReportWorkoutBinding

private const val TAG = "HealthReportAdapter_FitPT"

class HealthReportAdapter(
    var items: MutableList<WorkoutNameScoreItem>,
    private val onItemChanged: () -> Unit,
    private val onItemClicked: (Long) -> Unit
) : RecyclerView.Adapter<HealthReportAdapter.HealthReportViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    inner class HealthReportViewHolder(private val binding: ListItemReportWorkoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WorkoutNameScoreItem) {
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

                    root.setOnClickListener {

                        Log.d(TAG, "SelectedWorkout: ${item.id}")
                        onItemClicked(item.id)
                    }
                }
            }
        }
    }

    fun addEditItem(newId: Long) {
        if (items.lastOrNull()?.isEditing == true) return

        items.add(
            WorkoutNameScoreItem(
                id = newId,
                name = "",
                score = "",
                isEditing = true
            )
        )
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
