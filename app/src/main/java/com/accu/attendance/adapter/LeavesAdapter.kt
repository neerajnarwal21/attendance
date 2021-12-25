package com.accu.attendance.adapter

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.accu.attendance.R
import com.accu.attendance.data.UserDayData
import com.accu.attendance.utils.Const
import com.accu.attendance.utils.Utils
import java.util.*


class LeavesAdapter(private val context: Context, private val userDayData: ArrayList<UserDayData>)
    : RecyclerView.Adapter<LeavesAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_leaves, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val userDayData = this.userDayData[position]
        if (userDayData.status == Const.DayStats.SHORT_LEAVE) {
            holder.parentCL.visibility = View.VISIBLE
            holder.dateTV.background = ContextCompat.getDrawable(context, R.drawable.round_skyblue)
            holder.vieww.setBackgroundColor(ContextCompat.getColor(context, R.color.startcolor_grad))
            holder.statusTV.text = context.getString(R.string.short_leave)
            holder.dateTV.text = Utils.changeDateFormat(userDayData.date, "yyyy-MM-dd", "dd")
        } else if (userDayData.status == Const.DayStats.HALF_DAY) {
            holder.parentCL.visibility = View.VISIBLE
            holder.dateTV.background = ContextCompat.getDrawable(context, R.drawable.round_red)
            holder.vieww.setBackgroundColor(ContextCompat.getColor(context, R.color.signin_red))
            holder.statusTV.text = context.getString(R.string.half_day)
            holder.dateTV.text = Utils.changeDateFormat(userDayData.date, "yyyy-MM-dd", "dd")
        } else {
            holder.parentCL.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return userDayData.size
    }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var dateTV: TextView
        var statusTV: TextView
        var vieww: View
        var parentCL: ConstraintLayout

        init {
            parentCL = view.findViewById(R.id.parentCL)
            vieww = view.findViewById(R.id.vieww)
            dateTV = view.findViewById(R.id.dateTV)
            statusTV = view.findViewById(R.id.statusTV)
        }
    }
}