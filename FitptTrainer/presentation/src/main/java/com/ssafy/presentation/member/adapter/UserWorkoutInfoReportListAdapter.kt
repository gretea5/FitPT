package com.ssafy.presentation.member.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.domain.model.report.ReportList
import com.ssafy.presentation.databinding.ListItemUserReportBinding
import com.ssafy.presentation.util.TimeUtils

class UserWorkoutInfoReportListAdapter(
    private val onItemClicked: (ReportList) -> Unit
) : ListAdapter<ReportList, UserWorkoutInfoReportListAdapter.ReportViewHolder>(ReportDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ListItemUserReportBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReportViewHolder(
        private val binding: ListItemUserReportBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(report: ReportList) {
            binding.tvUserReportListTime.text = TimeUtils.formatDateTime(report.createdAt)

            binding.root.setOnClickListener {
                onItemClicked(report)
            }
        }
    }

    class ReportDiffCallback : DiffUtil.ItemCallback<ReportList>() {
        override fun areItemsTheSame(oldItem: ReportList, newItem: ReportList): Boolean {
            return oldItem.reportId == newItem.reportId
        }

        override fun areContentsTheSame(oldItem: ReportList, newItem: ReportList): Boolean {
            return oldItem == newItem
        }
    }
}
