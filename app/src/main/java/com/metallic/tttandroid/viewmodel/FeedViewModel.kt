package com.metallic.tttandroid.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.metallic.tttandroid.model.AppDatabase
import com.metallic.tttandroid.model.Feed
import com.metallic.tttandroid.request.FeedResponse
import com.metallic.tttandroid.request.enqueueFeed
import okhttp3.*

class FeedViewModel(application: Application): AndroidViewModel(application)
{
	private var _feed: Feed? = null
	val feed: Feed get() = _feed!!

	private val _feedData = MutableLiveData<FeedResponse>()
	val feedData: LiveData<FeedResponse> get() = _feedData

	private var call: Call? = null

	fun initialize(feedId: Long) // TODO: is this really the correct way to init a ViewModel with a parameter?
	{
		if(_feed != null)
			return

		_feed = AppDatabase.getInstance(getApplication()).feedDao().getById(feedId)

		val url = _feed?.fullUri?.toString()

		if(url == null)
		{
			_feedData.postValue(FeedResponse(null))
			return
		}

		val request = Request.Builder()
				.url(url)
				.build()

		call = OkHttpClient().newCall(request)
		call?.enqueueFeed { response ->
			call = null
			_feedData.postValue(response)
		}
	}

	override fun onCleared()
	{
		super.onCleared()
		call?.cancel()
	}
}