package com.kunal.calendar.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kunal.calendar.databinding.LayoutDayCellBinding
import com.kunal.calendar.utils.gone
import com.kunal.calendar.utils.visible
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(
    days: List<String>,
    private val callback: (position: Int, day: String) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private var dayOfMonth: String? = null
    private var daysOfMonth: MutableList<String>? = null
    private var selectedDate: String? = null
    private var currentMonth: String? = null
    private var activeMonth: String? = null

    init {
        daysOfMonth = days.toMutableList()
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
        val dateFormatMonth = SimpleDateFormat("MM", Locale.getDefault())
        dayOfMonth = dateFormat.format(currentDate)
        currentMonth = dateFormatMonth.format(currentDate)
        activeMonth = currentMonth
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CalendarViewHolder {
        val binding =
            LayoutDayCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = daysOfMonth?.size ?: 0

    fun resetMonth(newDaysOfMonth: List<String>, newMonth: String) {
        daysOfMonth?.clear()
        daysOfMonth?.addAll(newDaysOfMonth)
        selectedDate = null
        activeMonth = newMonth
        notifyDataSetChanged()
    }

    inner class CalendarViewHolder(private val binding: LayoutDayCellBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val day = daysOfMonth?.get(position) ?: ""
            if (day.isNullOrBlank() || day.isNullOrEmpty() || day == "0") {
                binding.root.gone()
            } else if (day.equals(selectedDate, true)) {
                binding.currentDateLayout.visible()
                binding.normalDateLayout.gone()
                binding.currentDateLayoutInactive.gone()
                binding.currentDate.text = selectedDate
            } else {
                binding.normalDateLayout.visible()
                binding.currentDateLayout.gone()
                binding.currentDateLayoutInactive.gone()
                binding.normalDate.text = day
            }

            if (day == dayOfMonth && dayOfMonth != selectedDate && currentMonth == activeMonth) {
                binding.currentDateLayout.gone()
                binding.normalDateLayout.gone()
                binding.currentDateLayoutInactive.visible()
                binding.currentDateInactive.text = day
            }

            binding.normalDateLayout.setOnClickListener {
                selectedDate = day
                callback.invoke(position, day)
                notifyDataSetChanged()
            }
            binding.currentDateInactive.setOnClickListener {
                selectedDate = day
                callback.invoke(position, day)
                notifyDataSetChanged()
            }
            binding.currentDateLayout.setOnClickListener {
                selectedDate = day
                callback.invoke(position, day)
                notifyDataSetChanged()
            }
        }
    }
}