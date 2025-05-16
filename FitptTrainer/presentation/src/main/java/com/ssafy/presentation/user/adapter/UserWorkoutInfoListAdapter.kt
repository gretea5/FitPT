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
) : ListAdapter<MemberInfo, UserWorkoutInfoListAdapter.ViewHolder>(PersonDiffCallback()) {

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
            // 이전에 선택된 아이템 위치 저장
            val previousSelected = selectedPosition

            // 같은 아이템을 다시 클릭한 경우에도 선택 유지 (보고서 작성으로 이동하기 위함)
            selectedPosition = position

            // 이전 선택 아이템과 새 선택 아이템 갱신
            if (previousSelected != -1) {
                notifyItemChanged(previousSelected)
            }
            notifyItemChanged(selectedPosition)

            // 선택된 회원 정보를 콜백으로 전달
            onItemSelected.invoke(item)
        }
    }

    class ViewHolder(private val binding: ItemWorkoutMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(memberInfo: MemberInfo, isSelected: Boolean) {
            binding.tvName.text = memberInfo.memberName
            binding.tvBirthDate.text = memberInfo.memberBirth

            // 선택 상태에 따른 시각적 표시
            if (isSelected) {
                itemView.setBackgroundResource(android.R.color.holo_blue_light)
            } else {
                itemView.setBackgroundResource(android.R.color.transparent)
            }
        }
    }

    class PersonDiffCallback : DiffUtil.ItemCallback<MemberInfo>() {
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
