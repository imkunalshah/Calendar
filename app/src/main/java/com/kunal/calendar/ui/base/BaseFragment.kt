package com.kunal.calendar.ui.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kunal.calendar.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseFragment : Fragment() {

    val viewModel: MainViewModel by activityViewModels()

    abstract fun initializeViews()

    abstract fun initializeObservers()

}