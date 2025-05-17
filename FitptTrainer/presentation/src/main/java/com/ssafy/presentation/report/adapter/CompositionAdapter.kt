package com.ssafy.presentation.report.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.domain.model.measure.CompositionCondition
import com.ssafy.presentation.databinding.ItemBodyCompositionBinding

class CompositionAdapter(private val items: List<CompositionCondition>) : RecyclerView.Adapter<CompositionAdapter.CompositionViewHolder>() {

    inner class CompositionViewHolder(val binding: ItemBodyCompositionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompositionViewHolder {
        val binding = ItemBodyCompositionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CompositionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompositionViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvBodyMeasureTitle.text = item.bodyTitle
            scoreBotong.text = item.bodyCondition
            tvStatus.text = "${item.count}점"
        }
    }

    override fun getItemCount(): Int = items.size
}