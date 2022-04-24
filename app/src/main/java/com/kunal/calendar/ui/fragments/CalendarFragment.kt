package com.kunal.calendar.ui.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.kunal.calendar.databinding.FragmentCalendarBinding
import com.kunal.calendar.ui.adapters.CalendarAdapter
import com.kunal.calendar.ui.base.BaseFragment
import com.kunal.calendar.utils.AppConstants
import com.kunal.calendar.utils.AppConstants.Messages.EVENT_CREATED_SUCCESSFUL
import com.kunal.calendar.utils.OnSwipeTouchListener
import com.kunal.calendar.utils.showSnackBar
import com.kunal.calendar.utils.showToast
import timber.log.Timber
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class CalendarFragment : BaseFragment() {

    private lateinit var selectedDate: LocalDate

    private lateinit var binding: FragmentCalendarBinding

    private var currentSelectedDate: String? = null

    private val _calendarAdapter by lazy {
        currentSelectedDate = "${selectedDate.dayOfMonth} ${getMonthAndYearFromDate(selectedDate)}"
        CalendarAdapter(
            getDaysInMonth(selectedDate),
            ::onDateSelected
        )
    }

    private fun onDateSelected(position: Int, day: String) {
        currentSelectedDate = "$day ${getMonthAndYearFromDate(selectedDate)}"
    }

    private var calendarAdapter: CalendarAdapter? = null
        get() {
            kotlin.runCatching {
                field = _calendarAdapter
            }.onFailure {
                Timber.d("Error: $it")
                field = null
            }
            return field
        }

    companion object {
        const val TAG = "CalendarFragment"

        fun newInstance(): CalendarFragment {
            return CalendarFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        initializeObservers()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initializeViews() {
        viewModel.onNoInternetException = ::showNetworkUnavailableSnackBar
        selectedDate = LocalDate.now()
        binding.calendarView.displayDate.text = getMonthAndYearFromDate(selectedDate)
        binding.calendarView.calendarRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 7)
            adapter = calendarAdapter
        }
        binding.calendarView.nextButton.setOnClickListener {
            changeToNextMonth()
        }
        binding.calendarView.previousButton.setOnClickListener {
            changeToPreviousMonth()
        }
        binding.createEventButton.setOnClickListener {
            val createEventBottomSheetDialogFragment =
                CreateEventBottomSheetDialogFragment.newInstance(currentSelectedDate).also {
                    it.onTaskSave = ::onTaskSave
                }
            createEventBottomSheetDialogFragment.show(
                requireActivity().supportFragmentManager,
                CreateEventBottomSheetDialogFragment.TAG
            )
        }
        /**
         * This will change the month in
         * calendar on left/right swipe.
         **/
        binding.root.setOnTouchListener(
            object :
                OnSwipeTouchListener(context) {
                override fun onSwipeLeft() {
                    super.onSwipeLeft()
                    changeToNextMonth()
                }

                override fun onSwipeRight() {
                    super.onSwipeRight()
                    changeToPreviousMonth()
                }
            })

    }

    private fun showNetworkUnavailableSnackBar() {
        binding.root.showSnackBar(AppConstants.Messages.INTERNET_UNAVAILABLE)
    }

    private fun onTaskSave(body: HashMap<String, Any?>) {
        viewModel.storeEvent(body)
    }

    private fun getDaysInMonth(date: LocalDate): List<String> {
        val daysInMonthArray: MutableList<String> = ArrayList()
        val yearMonth = YearMonth.from(date)
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstOfMonth = selectedDate.withDayOfMonth(1)
        val dayOfWeek = firstOfMonth?.dayOfWeek?.value ?: 0

        for (i in 1 until 42) {
            if (i < dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("")
            } else {
                daysInMonthArray.add((i - dayOfWeek).toString())
            }
        }
        return daysInMonthArray.toList()
    }

    private fun getMonthAndYearFromDate(date: LocalDate): String? {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }

    private fun changeToPreviousMonth() {
        var newMonth = selectedDate.minusMonths(1).monthValue.toString()
        if (newMonth.length == 1) {
            newMonth = "0$newMonth"
        }
        selectedDate = selectedDate.minusMonths(1)
        val previousMonthDays = getDaysInMonth(selectedDate)
        calendarAdapter?.resetMonth(
            previousMonthDays,
            newMonth
        )
        binding.calendarView.displayDate.text = getMonthAndYearFromDate(selectedDate)
    }

    private fun changeToNextMonth() {
        var newMonth = selectedDate.plusMonths(1).monthValue.toString()
        if (newMonth.length == 1) {
            newMonth = "0$newMonth"
        }
        selectedDate = selectedDate.plusMonths(1)
        val nextMonthDays = getDaysInMonth(selectedDate)
        calendarAdapter?.resetMonth(
            nextMonthDays,
            newMonth
        )
        binding.calendarView.displayDate.text = getMonthAndYearFromDate(selectedDate)
    }

    override fun initializeObservers() {
        viewModel.isSaveSuccessFull.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                viewModel.resetIsSaveSuccessFull()
                context?.showToast(EVENT_CREATED_SUCCESSFUL)
            }
        }
    }
}