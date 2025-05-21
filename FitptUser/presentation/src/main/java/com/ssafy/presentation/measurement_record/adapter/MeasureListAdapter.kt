package com.ssafy.presentation.measurement_record.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.domain.model.measure_record.MeasureRecordItem
import com.ssafy.presentation.databinding.ItemMeasureRecordBinding
import com.ssafy.presentation.util.CommonUtils

class MeasureListAdapter(
    private var items: List<MeasureRecordItem>,
    private val onItemClick: (MeasureRecordItem,Int) -> Unit // 클릭 리스너 콜백 추가
) : RecyclerView.Adapter<MeasureListAdapter.MeasureViewHolder>() {

    inner class MeasureViewHolder(private val binding: ItemMeasureRecordBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MeasureRecordItem, position: Int) {
            binding.tvWeightMain.text = String.format("%.2f", item.weight)
            binding.tvMuscleMassValue.text = String.format("%.2f kg", item.smm)
            binding.tvBodyFatValue.text = String.format("%.0f %%", item.bfp)
            binding.tvDateTime.text = CommonUtils.formatCreatedAt(item.createAt)

            // 클릭 리스너 설정
            binding.root.setOnClickListener {
                onItemClick(item,position) // 클릭 시 onItemClick 콜백 호출
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasureViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMeasureRecordBinding.inflate(inflater, parent, false)
        return MeasureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MeasureViewHolder, position: Int) {
        holder.bind(items[position],position)
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newList: List<MeasureRecordItem>) {
        this.items = newList
        notifyDataSetChanged()
    }
}