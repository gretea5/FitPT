package com.ssafy.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.domain.model.schedule.ScheduleWithMemberInfo
import com.ssafy.presentation.databinding.ListItemScheduleBinding
import com.ssafy.presentation.util.TimeUtils

class HomeAdapter : ListAdapter<ScheduleWithMemberInfo, HomeAdapter.ScheduleViewHolder>(ScheduleDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ListItemScheduleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ScheduleViewHolder(private val binding: ListItemScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ScheduleWithMemberInfo) {
            val startTime = TimeUtils.parseDateTime(item.startTime).second;
            val endTime = TimeUtils.parseDateTime(item.endTime).second;
            binding.tvScheduleName.text = item.memberName
            binding.tvScheduleTime.text = "${startTime}~${endTime}"

            binding.root.setOnClickListener {
            }
        }
    }

    class ScheduleDiffCallback : DiffUtil.ItemCallback<ScheduleWithMemberInfo>() {
        override fun areItemsTheSame(oldItem: ScheduleWithMemberInfo, newItem: ScheduleWithMemberInfo): Boolean {
            return oldItem.scheduleId == newItem.scheduleId
        }

        override fun areContentsTheSame(oldItem: ScheduleWithMemberInfo, newItem: ScheduleWithMemberInfo): Boolean {
            return oldItem == newItem
        }
    }
}
