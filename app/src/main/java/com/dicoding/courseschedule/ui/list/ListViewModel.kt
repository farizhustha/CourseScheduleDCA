package com.dicoding.courseschedule.ui.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.dicoding.courseschedule.data.Course
import com.dicoding.courseschedule.data.DataRepository
import com.dicoding.courseschedule.util.SortType

class ListViewModel(private val repository: DataRepository) : ViewModel() {

    private val _sortParams = MutableLiveData<SortType>()

    init {
        _sortParams.value = SortType.TIME
    }

    val courses = _sortParams.switchMap {
        repository.getAllCourse(it).cachedIn(viewModelScope)
    }

    fun sort(newValue: SortType) {
        _sortParams.value = newValue
    }

    fun delete(course: Course) {
        repository.delete(course)
    }
}