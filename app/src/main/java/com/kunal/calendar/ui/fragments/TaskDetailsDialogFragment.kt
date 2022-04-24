package com.kunal.calendar.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.kunal.calendar.R
import com.kunal.calendar.data.network.models.Task
import com.kunal.calendar.databinding.FragmentTaskDetailsDialogBinding
import com.kunal.calendar.utils.AppConstants.Remote.TASK
import com.kunal.calendar.utils.getDialogWidth
import com.kunal.calendar.utils.gone

class TaskDetailsDialogFragment : DialogFragment() {


    lateinit var binding: FragmentTaskDetailsDialogBinding

    companion object {

        const val TAG = "TaskDetailsDialogFragment"

        fun newInstance(task: Task?): TaskDetailsDialogFragment {
            val bundle = Bundle().apply {
                putSerializable(TASK, task)
            }
            val taskDetailsDialogFragment = TaskDetailsDialogFragment().apply {
                arguments = bundle
            }
            return taskDetailsDialogFragment
        }
    }

    var onDeleteClick: ((task: Task?) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_rounded)
        dialog.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.dismiss()
                true
            } else false
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskDetailsDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDimens(view)
        initializeViews()
    }

    private fun setDimens(view: View) {
        view.layoutParams.width = context?.getDialogWidth()?: 200
    }

    private fun initializeViews() {
        val task = arguments?.getSerializable(TASK) as? Task
        binding.deleteEventButton.setOnClickListener {
            onDeleteClick?.invoke(task)
            dismiss()
        }
        val taskLocation = task?.taskDetails?.location
        val taskDetails = task?.taskDetails?.description
        val date = task?.taskDetails?.date
        val time = task?.taskDetails?.time
        val title = task?.taskDetails?.title

        binding.taskTime.text = time
        binding.eventTitle.text = title
        binding.taskDate.text = date


        if (taskLocation.isNullOrBlank() || taskLocation.isNullOrEmpty()) {
            binding.locationLayout.gone()
        } else {
            binding.taskLocation.text = taskLocation
        }

        if (taskDetails.isNullOrBlank() || taskDetails.isNullOrEmpty()) {
            binding.detailLayout.gone()
        } else {
            binding.taskDetails.text = taskDetails
        }

    }


}