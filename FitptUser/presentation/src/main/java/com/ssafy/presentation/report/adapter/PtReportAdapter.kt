package com.ssafy.presentation.report.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.domain.model.report.PtReportItem
import com.ssafy.presentation.databinding.ItemPtReportBinding
import com.ssafy.presentation.util.CommonUtils

class PtReportAdapter(
    private val items: List<PtReportItem>,
    private val onItemClick: (PtReportItem) -> Unit
) : RecyclerView.Adapter<PtReportAdapter.PtViewHolder>() {

    inner class PtViewHolder(private val binding: ItemPtReportBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PtReportItem) {
            binding.txtDateTime.text = CommonUtils.formatCreatedAt(item.dateTime)  // 예: "2025년 02월 28일 오후 06:20"
            binding.txtTrainerName.text = item.trainerName // 예: "박장훈"

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PtViewHolder {
        val binding = ItemPtReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PtViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PtViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}