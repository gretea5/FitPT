package com.ssafy.presentation.user.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.domain.model.member.MemberInfo
import com.ssafy.presentation.databinding.ItemWorkoutMemberBinding

class UserWorkoutInfoListAdapter(
    private val onItemSelected: ((MemberInfo) -> Unit)
) : ListAdapter<MemberInfo, UserWorkoutInfoListAdapter.ViewHolder>(MemberInfoDiffCallback()) {
    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWorkoutMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = getItem(position)
        holder.bind(item, position == selectedPosition)

        holder.itemView.setOnClickListener {
            val previousSelected = selectedPosition

            if (previousSelected == position) {
                selectedPosition = -1
            } else {
                selectedPosition = position
            }

            if (previousSelected != -1) {
                notifyItemChanged(previousSelected)
            }
            notifyItemChanged(position)

            if (selectedPosition != -1) {
                onItemSelected.invoke(item)
            }
        }
    }

    class ViewHolder(private val binding: ItemWorkoutMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(memberInfo: MemberInfo, isSelected: Boolean) {
            binding.tvName.text = memberInfo.memberName
            binding.tvBirthDate.text = memberInfo.memberBirth

            if (isSelected) {
                binding.tvName.setTextColor(binding.root.context.getColor(android.R.color.holo_orange_dark))
                binding.tvBirthDate.setTextColor(binding.root.context.getColor(android.R.color.holo_orange_dark))
            } else {
                binding.tvName.setTextColor(binding.root.context.getColor(android.R.color.black))
                binding.tvBirthDate.setTextColor(binding.root.context.getColor(android.R.color.black))
            }
        }
    }

    class MemberInfoDiffCallback : DiffUtil.ItemCallback<MemberInfo>() {
        override fun areItemsTheSame(oldItem: MemberInfo, newItem: MemberInfo): Boolean {
            return oldItem.memberId == newItem.memberId
        }

        override fun areContentsTheSame(oldItem: MemberInfo, newItem: MemberInfo): Boolean {
            return oldItem == newItem
        }
    }

    fun getSelectedItem(): MemberInfo? {
        return if (selectedPosition != -1) getItem(selectedPosition) else null
    }

    fun clearSelection() {
        val previousSelected = selectedPosition
        selectedPosition = -1
        if (previousSelected != -1) {
            notifyItemChanged(previousSelected)
        }
    }
}
