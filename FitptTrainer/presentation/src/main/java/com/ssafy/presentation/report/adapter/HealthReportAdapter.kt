package com.ssafy.presentation.report.adapter

import android.os.Build.VERSION_CODES.P
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.domain.model.report.HealthReportWorkout
import com.ssafy.domain.model.report.WorkoutNameScoreItem
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.ListItemReportWorkoutBinding

private const val TAG = "HealthReportAdapter_FitPT"

class HealthReportAdapter(
    var items: MutableList<WorkoutNameScoreItem>,
    private val onItemChanged: () -> Unit,
    private val onItemClicked: (Long, Boolean) -> Unit
) : RecyclerView.Adapter<HealthReportAdapter.HealthReportViewHolder>() {

    private var nameWatcher: TextWatcher? = null
    private var scoreWatcher: TextWatcher? = null

    inner class HealthReportViewHolder(private val binding: ListItemReportWorkoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WorkoutNameScoreItem) {
            if (item.isEditing) {
                binding.apply {
                    clReportWorkoutItemEditMode.visibility = View.VISIBLE
                    clReportWorkoutItemViewMode.visibility = View.GONE

                    // 기존 리스너 제거
                    nameWatcher?.let { tvReportWorkoutEditName.removeTextChangedListener(it) }
                    scoreWatcher?.let { tvReportWorkoutEditScore.removeTextChangedListener(it) }

                    tvReportWorkoutEditName.setText(item.name)
                    tvReportWorkoutEditScore.setText(item.score)

                    nameWatcher = object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            item.name = s.toString()
                            onItemChanged()
                        }

                        override fun afterTextChanged(s: Editable?) {}
                    }

                    scoreWatcher = object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            item.score = s.toString()
                            onItemChanged()
                        }

                        override fun afterTextChanged(s: Editable?) {}
                    }

                    tvReportWorkoutEditName.addTextChangedListener(nameWatcher)
                    tvReportWorkoutEditScore.addTextChangedListener(scoreWatcher)
                }
            } else {
                binding.apply {
                    clReportWorkoutItemEditMode.visibility = View.GONE
                    clReportWorkoutItemViewMode.visibility = View.VISIBLE

                    tvReportWorkoutViewName.text = item.name
                    tvReportWorkoutViewScore.text = item.score

                    // 카드 스타일 적용
                    applyStyle(item)

                    root.setOnClickListener {
                        // 이미 다른 아이템이 선택되어 있다면 클릭 무시
                        val otherSelected = items.any { it.isSelected && it != item }
                        if (otherSelected) return@setOnClickListener

                        item.isSelected = !item.isSelected
                        notifyItemChanged(adapterPosition)
                        onItemClicked(item.id, item.isSelected)
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

    fun removeSelectedItem() {
        val removedIndexes = items.mapIndexedNotNull { index, item ->
            if (item.isSelected) index else null
        }

        items.removeIf { it.isSelected }

        removedIndexes.sortedDescending().forEach {
            notifyItemRemoved(it)
        }

        Log.d(TAG, "deleteWorkout: ${items}")
    }

    fun finalizeLastItem() {
        items.lastOrNull()?.let {
            Log.d(TAG, "AddWorkout : Adapter: ${it.id}")

            it.isEditing = false
            notifyItemChanged(items.lastIndex)
        }
    }

    private fun ListItemReportWorkoutBinding.applyStyle(item: WorkoutNameScoreItem) {
        val context = root.context
        val backgroundColor = if (item.isSelected) R.color.main_blue else R.color.white
        val textColor = if (item.isSelected) R.color.main_white else R.color.main_black
        val strokeColor = if (item.isEditing) R.color.main_blue else R.color.main_black

        cvReportWorkoutViewContentName.setCardBackgroundColor(ContextCompat.getColor(context, backgroundColor))
        cvReportWorkoutViewContentScore.setCardBackgroundColor(ContextCompat.getColor(context, backgroundColor))

        tvReportWorkoutViewName.setTextColor(ContextCompat.getColor(context, textColor))
        tvReportWorkoutViewScore.setTextColor(ContextCompat.getColor(context, textColor))

        cvReportWorkoutViewContentName.strokeColor = ContextCompat.getColor(context, strokeColor)
        cvReportWorkoutViewContentScore.strokeColor = ContextCompat.getColor(context, strokeColor)
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
