package com.ssafy.presentation.report.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.domain.model.measure.CompositionCondition
import com.ssafy.presentation.R
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
            //tvBotongScore.text = item.bodyCondition
            if(item.bodyCondition=="미만"){
                tvMidalScore.visibility = View.VISIBLE
                tvMidalScore.text = item.bodyScore
                statusDot.setBackgroundResource(R.drawable.circle_yellow)
            }
            else if(item.bodyCondition=="적정"){
                tvBotongScore.visibility = View.VISIBLE
                tvBotongScore.text = item.bodyScore
                statusDot.setBackgroundResource(R.drawable.circle_green)
            }
            else if(item.bodyCondition=="초과"){
                tvChogwaScore.visibility = View.VISIBLE
                tvChogwaScore.text = item.bodyScore
                statusDot.setBackgroundResource(R.drawable.circle_red)
            }
            else{
                tvManyChogwaScore.visibility = View.VISIBLE
                tvManyChogwaScore.text = item.bodyScore
                statusDot.setBackgroundResource(R.drawable.circle_red)
            }
            if(item.bodyTitle=="골격근량"){
                tvChogwa.text="높음"
                tvManyChogwa.text="매우 높음"
            }

            tvStatus.text = "${item.bodyCondition}"
        }
    }

    override fun getItemCount(): Int = items.size
}