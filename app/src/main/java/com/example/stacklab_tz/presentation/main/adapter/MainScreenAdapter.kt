package com.example.stacklab_tz.presentation.main.adapter

import android.graphics.Point
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.stacklab_tz.databinding.ItemTaskBinding
import com.example.stacklab_tz.db.entities.RepeatMode.DAILY
import com.example.stacklab_tz.db.entities.RepeatMode.MONTOFRI
import com.example.stacklab_tz.db.entities.RepeatMode.ONCE
import com.example.stacklab_tz.db.entities.Task

class MainScreenAdapter(
) : RecyclerView.Adapter<MainScreenAdapter.MainScreenViewHolder>() {

    var menuClickListener: ((Task, Point) -> Unit)? = null
    private var items: List<Task> = emptyList()

    fun setItems(items: List<Task>) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class MainScreenViewHolder(private val binding: ItemTaskBinding) :
        ViewHolder(binding.root) {
        fun bind(item: Task) = with(binding) {
            titleTask.text = item.title
            val formattedMinute = if (item.minute / 10 == 0) {
                "0${item.minute}"
            } else {
                "" + item.minute
            }
            timeTask.text = "${item.hour}:$formattedMinute"
            val repeatMode = when (item.repeatMode) {
                ONCE -> "Once"
                DAILY -> "Daily"
                MONTOFRI -> "Monday to Friday"
            }
            dateTask.text = if (item.repeatMode == ONCE)
                "${item.day}/${item.month + 1}/${item.year}"
            else
                repeatMode
            openMenuTask.setOnClickListener {
                val location = IntArray(2)
                openMenuTask.getLocationOnScreen(location)
                val point = Point()
                point.x = location[0]
                point.y = location[1]
                menuClickListener?.invoke(item, point)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainScreenViewHolder {
        val binding =
            ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainScreenViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MainScreenViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

}