package com.kunal.calendar.ui.base

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kunal.calendar.data.network.datastore.DatastoreManager
import com.kunal.calendar.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var datastoreManager: DatastoreManager

    val viewModel: MainViewModel by viewModels()

}