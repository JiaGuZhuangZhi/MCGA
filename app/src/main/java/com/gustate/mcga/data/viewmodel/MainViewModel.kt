package com.gustate.mcga.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.gustate.mcga.data.XposedRepo
import com.gustate.mcga.utils.RootUtils

class MainViewModel(context: Application) : AndroidViewModel(context) {

    private val repoXposed = XposedRepo(context)

    private val _isModuleActive = MutableLiveData<Boolean>()
    val isModuleActive get() = _isModuleActive

    private val _isRootAvailable = MutableLiveData<Boolean>()
    val isRootAvailable get() = _isRootAvailable

    init {
        _isModuleActive.value = checkModuleActive()
        _isRootAvailable.value = checkRootAvailable()
    }

    fun checkModuleActive() = repoXposed.isModuleActive()
    fun checkRootAvailable() = RootUtils.isRootAvailable()

}