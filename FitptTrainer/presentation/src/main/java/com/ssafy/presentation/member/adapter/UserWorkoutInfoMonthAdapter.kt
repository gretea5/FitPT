package com.ssafy.presentation.member.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.ListItemUserMonthBinding

class UserWorkoutInfoMonthAdapter(
    private val context: Context,
    private val monthList: List<String>,
    private val onItemClicked: (String, Int) -> Boolean
) : RecyclerView.Adapter<UserWorkoutInfoMonthAdapter.MonthViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val binding = ListItemUserMonthBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MonthViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        holder.bind(monthList[position], position)
    }

    override fun getItemCount(): Int = monthList.size

    inner class MonthViewHolder(
        private val binding: ListItemUserMonthBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(month: String, position: Int) {
            binding.tvUserMonth.text = month

            // 선택 상태에 따른 UI 업데이트
            if (position == selectedPosition) {
                binding.cvUserMonth.strokeColor = ContextCompat.getColor(context, R.color.highlight_orange)
                binding.tvUserMonth.setTextColor(ContextCompat.getColor(context, R.color.highlight_orange))
            } else {
                binding.cvUserMonth.strokeColor = ContextCompat.getColor(context, R.color.main_gray)
                binding.tvUserMonth.setTextColor(ContextCompat.getColor(context, R.color.main_gray))
            }

            // 클릭 이벤트
            binding.root.setOnClickListener {
                val shouldUpdateSelection = onItemClicked(month, position)

                if (shouldUpdateSelection) {
                    val previousSelected = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(previousSelected)
                    notifyItemChanged(selectedPosition)
                    onItemClicked(month, position)
                }
            }
        }
    }

    fun setSelectedPosition(position: Int) {
        if (position in monthList.indices && position != selectedPosition) {
            val oldPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(oldPosition)
            notifyItemChanged(selectedPosition)
        }
    }
}