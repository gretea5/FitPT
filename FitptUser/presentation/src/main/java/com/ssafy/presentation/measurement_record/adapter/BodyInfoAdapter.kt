package com.ssafy.presentation.measurement_record.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.domain.model.measure.BodyInfoItem
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.ItemBodyCompositionBinding
import com.ssafy.presentation.databinding.ItemMeasureRecordBinding

class BodyInfoAdapter(
    private var items: List<BodyInfoItem>,
    private val onItemClick: (BodyInfoItem) -> Unit
) : RecyclerView.Adapter<BodyInfoAdapter.BodyInfoViewHolder>() {

    inner class BodyInfoViewHolder(private val binding: ItemBodyCompositionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BodyInfoItem) {
            binding.tvItemName.text = item.name
            binding.tvItemValue.text = item.value
            binding.tvStatus.text = item.status
            if(item.name=="골격근량"){
                binding.tvChogwa.text = "높음"
                binding.tvManyChogwa.text = "매우 높음"
            }
            when (item.status) {
                "미만" -> {
                    binding.statusDot.setBackgroundResource(R.drawable.circle_yellow)
                    binding.tvScoreMidal.text = item.value
                    binding.tvScoreMidal.visibility = View.VISIBLE
                }

                "적정" -> {
                    binding.statusDot.setBackgroundResource(R.drawable.circle_blue)
                    binding.tvScoreBotong.text = item.value
                    binding.tvScoreBotong.visibility = View.VISIBLE
                }

                "초과" -> {
                    binding.statusDot.setBackgroundResource(R.drawable.circle_red)
                    binding.tvScoreChogwa.text = item.value
                    binding.tvScoreChogwa.visibility = View.VISIBLE
                }

                "매우 초과" -> {
                    binding.statusDot.setBackgroundResource(R.drawable.circle_red) // or circle_red
                    binding.tvScoreManyChogwa.text = item.value
                    binding.tvScoreManyChogwa.visibility = View.VISIBLE
                }
            }
            if(item.name=="기초대사량"){
                binding.statusDot.setBackgroundResource(R.drawable.circle_blue)
                binding.tvScoreBotong.text = item.value
                binding.tvScoreBotong.visibility = View.VISIBLE
            }
            binding.root.setOnClickListener {
                onItemClick(item)
                binding.clMeasureDetail.isVisible = !binding.clMeasureDetail.isVisible
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

    fun updateItems(newItems: List<BodyInfoItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}