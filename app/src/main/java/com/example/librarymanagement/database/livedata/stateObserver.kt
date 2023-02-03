package com.example.librarymanagement.database.livedata

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observeAsState(defaultValue:T): State<T>{
    val state = mutableStateOf(defaultValue)
    observeForever(Observer { value ->
        state.value = value
    })
    return state
}