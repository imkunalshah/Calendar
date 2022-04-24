package com.kunal.calendar.ui.fragments

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kunal.calendar.data.network.models.Task
import com.kunal.calendar.databinding.FragmentEventsBinding
import com.kunal.calendar.ui.adapters.EventsListAdapter
import com.kunal.calendar.ui.base.BaseFragment
import com.kunal.calendar.utils.*
import com.kunal.calendar.utils.AppConstants.Messages.EVENT_DELETE_SUCCESSFUL
import com.kunal.calendar.utils.AppConstants.Messages.INTERNET_UNAVAILABLE
import timber.log.Timber

class EventsFragment : BaseFragment() {

    private lateinit var view: FragmentEventsBinding

    companion object {
        const val TAG = "EventsFragment"
        fun newInstance(): EventsFragment {
            return EventsFragment()
        }
    }


    private val _eventListAdapter by lazy {
        EventsListAdapter(
            emptyList(),
            ::onTaskClick
        )
    }

    /**
     * This will be triggered on long press of an
     * event card and will give a haptic feedback
     **/
    private fun onTaskClick(task: Task?, position: Int) {
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(150L)
        val taskDetailsDialogFragment = TaskDetailsDialogFragment.newInstance(task).also {
            it.onDeleteClick = { task ->
                eventListAdapter?.removeItem(position)
                viewModel.deleteEvent(task?.taskId.toString())
                if (eventListAdapter?.getData()?.isEmpty() == true) {
                    view.emptyListOverlay.visible()
                    view.eventListRecyclerView.gone()
                }
            }
        }
        taskDetailsDialogFragment.show(
            requireActivity().supportFragmentManager,
            TaskDetailsDialogFragment.TAG
        )
    }

    private var eventListAdapter: EventsListAdapter? = null
        get() {
            kotlin.runCatching {
                field = _eventListAdapter
            }.onFailure {
                Timber.d("Error: $it")
                field = null
            }
            return field
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = FragmentEventsBinding.inflate(inflater, container, false)
        return view.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        initializeViews()
        initializeObservers()
    }

    override fun initializeViews() {
        view.eventListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = eventListAdapter
        }
        enableSwipeToDelete()
        viewModel.onNoInternetException = ::showNetworkUnavailableSnackBar
    }

    private fun showNetworkUnavailableSnackBar() {
        view.root.showSnackBar(INTERNET_UNAVAILABLE)
    }

    /**
     * This Is For Swipe To Delete.
     * Swipe and it will delete a task
     **/
    private fun enableSwipeToDelete() {
        val swipeToDeleteCallback: SwipeToDeleteCallback =
            object : SwipeToDeleteCallback(requireContext()) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                    val position = viewHolder.adapterPosition
                    val task = eventListAdapter?.getData()?.get(position)
                    eventListAdapter?.removeItem(position)
                    if (eventListAdapter?.getData()?.isEmpty() == true) {
                        view.emptyListOverlay.visible()
                        view.eventListRecyclerView.gone()
                    }
                    viewModel.deleteEvent(task?.taskId.toString())
                }
            }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(view.eventListRecyclerView)
    }

    private fun getData() {
        viewModel.fetchEvents()
    }

    override fun initializeObservers() {
        viewModel.eventList.observe(viewLifecycleOwner) { taskList ->
            if (!taskList.isNullOrEmpty()) {
                eventListAdapter?.updateEventList(taskList)
                view.emptyListOverlay.gone()
                view.eventListRecyclerView.visible()
            } else {
                view.emptyListOverlay.visible()
                view.eventListRecyclerView.gone()
            }
        }
        viewModel.isDeleteSuccessFull.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                viewModel.resetIsDeleteSuccessFull()
                context?.showToast(EVENT_DELETE_SUCCESSFUL)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearEventList()
    }
}