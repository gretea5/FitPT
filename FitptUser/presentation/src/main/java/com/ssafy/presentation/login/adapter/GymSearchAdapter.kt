package com.ssafy.presentation.login.adapter

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.domain.model.sign.GymInfoItem
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.ItemGymBinding

class GymSearchAdapter(
    private val items: List<GymInfoItem>,
    private val onItemClick: (GymInfoItem) -> Unit
) : RecyclerView.Adapter<GymSearchAdapter.GymViewHolder>(){
    private var selectedPosition = RecyclerView.NO_POSITION
    inner class GymViewHolder(private val binding: ItemGymBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GymInfoItem, position: Int) {
            binding.tvLocationTitle.text = item.gymName
            binding.tvLocationAddress.text = item.location

            binding.root.setBackgroundResource(
                if (selectedPosition == position) R.drawable.bg_gym_selected
                else R.drawable.bg_gym_unselected
            )

            binding.root.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = position
                notifyItemChanged(previousPosition)  // 이전 아이템 배경 초기화
                notifyItemChanged(position)          // 현재 선택한 아이템 배경 적용
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GymViewHolder {
        val binding = ItemGymBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GymViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GymViewHolder, position: Int) {
        holder.bind(items[position],position)
    }

    override fun getItemCount(): Int = items.size
}