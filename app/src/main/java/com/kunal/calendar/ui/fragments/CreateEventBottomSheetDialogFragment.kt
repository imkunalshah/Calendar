package com.kunal.calendar.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.kunal.calendar.data.network.datastore.DatastoreManager
import com.kunal.calendar.data.network.models.TaskDetail
import com.kunal.calendar.databinding.FragmentCreateEventBottomsheetDialogBinding
import com.kunal.calendar.utils.AppConstants.Messages.EVENT_TIME_IS_REQUIRED
import com.kunal.calendar.utils.AppConstants.Messages.EVENT_TITLE_IS_REQUIRED
import com.kunal.calendar.utils.AppConstants.Messages.SELECT_TIME_OF_EVENT
import com.kunal.calendar.utils.AppConstants.Remote.TASK
import com.kunal.calendar.utils.AppConstants.Remote.USER_ID
import com.kunal.calendar.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CreateEventBottomSheetDialogFragment : RoundedBottomSheetDialogFragment() {

    lateinit var binding: FragmentCreateEventBottomsheetDialogBinding

    var onTaskSave: ((body: HashMap<String, Any?>) -> Unit?)? = null

    @Inject
    lateinit var datastoreManager: DatastoreManager

    companion object {
        const val TAG = "CreateEventBottomSheetDialogFragment"
        const val SELECTED_DATE = "selectedDate"
        fun newInstance(selectedDate: String?): CreateEventBottomSheetDialogFragment {
            val bundle = Bundle().apply {
                putString(SELECTED_DATE, selectedDate)
            }
            val createEventBottomSheetDialogFragment =
                CreateEventBottomSheetDialogFragment().apply {
                    arguments = bundle
                }
            return createEventBottomSheetDialogFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateEventBottomsheetDialogBinding.inflate(inflater, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
    }

    private var selectedTime: String? = null
    private var selectedHour: Int? = null
    private var selectedMinutes: Int? = null

    private fun initializeViews() {
        binding.timeLayout.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timePicker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(selectedHour ?: 12)
                    .setMinute(selectedMinutes ?: 0)
                    .setTitleText(SELECT_TIME_OF_EVENT)
                    .build()
            timePicker.addOnPositiveButtonClickListener {
                val minute = timePicker.minute
                val hour = timePicker.hour
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                selectedHour = hour
                selectedMinutes = minute
                selectedTime = SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(calendar.time)
                binding.selectedTime.text = selectedTime
            }
            timePicker.show(requireActivity().supportFragmentManager, "")
        }

        binding.saveEventButton.setOnClickListener {
            val taskTitle = binding.titleInputLayout.editText?.text.toString()
            val taskDetails = binding.detailsInputLayout.editText?.text.toString()
            val taskLocation = binding.locationInputLayout.editText?.text.toString()

            if (taskTitle.isEmpty() || taskTitle.isBlank()) {
                context?.showToast(EVENT_TITLE_IS_REQUIRED)
                return@setOnClickListener
            }
            if (selectedTime.isNullOrEmpty() || selectedTime.isNullOrBlank()) {
                context?.showToast(EVENT_TIME_IS_REQUIRED)
                return@setOnClickListener
            }
            lifecycleScope.launch(Dispatchers.IO) {
                val task = TaskDetail(
                    title = taskTitle,
                    description = taskDetails,
                    date = arguments?.getString(SELECTED_DATE),
                    location = taskLocation,
                    time = selectedTime
                )
                val userId = datastoreManager.userId.first()
                val body = hashMapOf(
                    USER_ID to userId,
                    TASK to task
                )
                onTaskSave?.invoke(body)
                dismiss()
            }
        }

    }
}