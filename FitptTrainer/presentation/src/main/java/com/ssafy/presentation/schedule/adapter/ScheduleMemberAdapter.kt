package com.ssafy.presentation.schedule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ssafy.domain.model.member.MemberInfo
import com.ssafy.presentation.R

class ScheduleMemberAdapter (
    private val context: Context,
    private val memberList: List<MemberInfo>
) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = memberList.size

    override fun getItem(position: Int): Any = memberList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.item_member, parent, false)

        val nameTextView = view.findViewById<TextView>(R.id.tv_name)
        val birthDateTextView = view.findViewById<TextView>(R.id.tv_birthdate)

        val member = memberList[position]
        nameTextView.text = member.memberName
        birthDateTextView.text = member.memberBirth

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getView(position, convertView, parent)
    }
}