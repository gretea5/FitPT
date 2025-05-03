package com.ssafy.presentation.measurement_record.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.domain.model.measure.BodyInfoItem
import com.ssafy.presentation.databinding.ItemBodyCompositionBinding
import com.ssafy.presentation.databinding.ItemMeasureRecordBinding

class BodyInfoAdapter(
    private val items: List<BodyInfoItem>,
    private val onItemClick: (BodyInfoItem) -> Unit
) : RecyclerView.Adapter<BodyInfoAdapter.BodyInfoViewHolder>() {

    inner class BodyInfoViewHolder(private val binding: ItemBodyCompositionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BodyInfoItem) {
            binding.tvItemName.text = item.name
            binding.tvItemValue.text = item.value
            binding.tvStatus.text = item.status

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BodyInfoViewHolder {
        val binding = ItemBodyCompositionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BodyInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BodyInfoViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}