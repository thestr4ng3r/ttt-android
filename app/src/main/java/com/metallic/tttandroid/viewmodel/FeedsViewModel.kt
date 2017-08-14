package com.metallic.tttandroid.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.metallic.tttandroid.model.AppDatabase

class FeedsViewModel(application: Application): AndroidViewModel(application)
{
	val feeds = AppDatabase.getInstance(application).feedDao().getAll()
}