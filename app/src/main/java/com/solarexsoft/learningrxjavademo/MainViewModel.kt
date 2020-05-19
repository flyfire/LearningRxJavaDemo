package com.solarexsoft.learningrxjavademo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import kotlin.concurrent.thread

/**
 * Created by houruhou on 2020/5/19/5:03 PM
 * Desc:
 */
class MainViewModel : ViewModel() {
    var questionList:List<String> = emptyList()
    val index:MutableLiveData<Int> = MutableLiveData()
    val currentQuestion: LiveData<String> = Transformations.map(index, questionList::get)

    fun mockQuestion() {
        Thread {
            Thread.sleep(1000)
            questionList = arrayListOf("solarex", "flyfire")
            index.postValue(0)
            questionList = arrayListOf("hello", "world", "china")
            index.postValue(2)
        }.start()
    }
}