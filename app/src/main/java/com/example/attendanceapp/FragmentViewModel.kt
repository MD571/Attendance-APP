package com.example.attendanceapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FragmentViewModel : ViewModel() {
    val ID =MutableLiveData<String>()
    fun setData(newData : String){
        ID.value=newData
    }
}