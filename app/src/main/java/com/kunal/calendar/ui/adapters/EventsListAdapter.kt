package com.kunal.calendar.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kunal.calendar.data.network.models.Task
import com.kunal.calendar.databinding.LayoutEventItemCellBinding
import timber.log.Timber


class EventsListAdapter(
    eventList: List<Task>,
    private val onTaskClick: ((task: Task?, position: Int) -> Unit)
) : RecyclerView.Adapter<EventsListAdapter.EventsListViewHolder>() {

    private var _eventList: MutableList<Task>? = null

    init {
        _eventList = eventList.toMutableList()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventsListViewHolder {
        val binding =
            LayoutEventItemCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventsListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventsListViewHolder, position: Int) {
        val task = _eventList?.get(position)
        holder.bind(task)
    }

    override fun getItemCount() = _eventList?.size ?: 0

    fun updateEventList(eventList: List<Task>) {
        _eventList?.clear()
        _eventList?.addAll(eventList.toMutableList())
        notifyDataSetChanged()
    }

    fun getData(): ArrayList<Task> {
        return ArrayList(_eventList ?: emptyList())
    }

    fun removeItem(position: Int) {
        _eventList?.removeAt(position)
        Timber.d("Position:$position")
        notifyDataSetChanged()
    }

    inner class EventsListViewHolder(private val binding: LayoutEventItemCellBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task?) {
            binding.eventTitle.text = task?.taskDetails?.title
            binding.eventDate.text = task?.taskDetails?.date
            binding.eventTime.text = task?.taskDetails?.time
            binding.root.setOnLongClickListener {
                onTaskClick.invoke(task, adapterPosition)
                true
            }
        }

    }
}