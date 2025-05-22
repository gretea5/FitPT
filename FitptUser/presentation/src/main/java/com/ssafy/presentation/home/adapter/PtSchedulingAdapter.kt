package com.ssafy.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.domain.model.home.PtScheduleItem
import com.ssafy.presentation.databinding.ItemPtCalendarBinding

class PtScheduleAdapter(
    private val items: List<PtScheduleItem>,
    private val onItemClick: (PtScheduleItem) -> Unit
) : RecyclerView.Adapter<PtScheduleAdapter.PtScheduleViewHolder>() {

    inner class PtScheduleViewHolder(private val binding: ItemPtCalendarBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PtScheduleItem) {
            binding.tvTrainerName.text = item.trainerName
            binding.tvTime.text = item.time

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PtScheduleViewHolder {
        val binding = ItemPtCalendarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PtScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PtScheduleViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}