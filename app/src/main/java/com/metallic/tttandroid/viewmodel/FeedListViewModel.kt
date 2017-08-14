package com.metallic.tttandroid.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.metallic.tttandroid.model.AppDatabase

class FeedListViewModel(application: Application): AndroidViewModel(application)
{
	val feeds = AppDatabase.getInstance(application).feedDao().getAll()
}