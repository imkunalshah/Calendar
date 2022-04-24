package com.kunal.calendar.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kunal.calendar.data.network.models.Task
import com.kunal.calendar.data.repositories.TasksRepository
import com.kunal.calendar.utils.AppConstants.Remote.SUCCESS
import com.kunal.calendar.utils.NoInternetException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private var _isSaveSuccessFull = MutableLiveData<Boolean>()
    val isSaveSuccessFull: LiveData<Boolean> = _isSaveSuccessFull

    private var _isDeleteSuccessFull = MutableLiveData<Boolean>()
    val isDeleteSuccessFull: LiveData<Boolean> = _isDeleteSuccessFull

    private var _eventList = MutableLiveData<List<Task>>()
    val eventList: LiveData<List<Task>> = _eventList

    var onNoInternetException: (() -> Unit)? = null

    init {
        _isSaveSuccessFull.value = false
        _isDeleteSuccessFull.value = false
    }

    fun storeEvent(body: HashMap<String, Any?>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = tasksRepository.saveEvent(body)
                if (response.equals(SUCCESS, true)) {
                    _isSaveSuccessFull.postValue(true)
                }
            } catch (e: NoInternetException) {
                onNoInternetException?.invoke()
            }
        }
    }

    fun fetchEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = tasksRepository.getEvents()
                launch(Dispatchers.Main) {
                    _eventList.value = response.value
                }
            } catch (e: NoInternetException) {
                onNoInternetException?.invoke()
            }
        }
    }

    fun deleteEvent(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = tasksRepository.removeEvent(taskId)
                if (response.equals(SUCCESS, true)) {
                    _isDeleteSuccessFull.postValue(true)
                }
            } catch (e: NoInternetException) {
                onNoInternetException?.invoke()
            }
        }
    }

    fun resetIsSaveSuccessFull() {
        _isSaveSuccessFull.value = false
    }

    fun resetIsDeleteSuccessFull() {
        _isDeleteSuccessFull.value = false
    }

    fun clearEventList() {
        _eventList.value = emptyList()
    }
}