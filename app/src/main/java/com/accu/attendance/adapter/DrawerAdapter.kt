package com.accu.attendance.adapter

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.accu.attendance.R
import com.accu.attendance.activity.MainActivity
import com.accu.attendance.data.DrawerData
import java.util.*


class DrawerAdapter(private val activity: MainActivity, private val drawerData: ArrayList<DrawerData>)
    : RecyclerView.Adapter<DrawerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_drawer, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val drawerData = this.drawerData[position]
        holder.titleTV.text = drawerData.title
        holder.parentCL.tag = drawerData
        holder.parentCL.setOnClickListener { v -> activity.onAdapterItemClick(v.tag as DrawerData) }
    }

    override fun getItemCount() = drawerData.size

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var titleTV: TextView = view.findViewById(R.id.titleTV)
        var parentCL: ConstraintLayout = view.findViewById(R.id.parentCL)
    }
}