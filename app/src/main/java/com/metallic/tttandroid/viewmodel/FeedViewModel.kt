package com.metallic.tttandroid.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.metallic.tttandroid.model.AppDatabase
import com.metallic.tttandroid.model.Feed
import okhttp3.*
import java.io.IOException

class FeedViewModel(application: Application): AndroidViewModel(application)
{
	private var _feed: Feed? = null
	val feed: Feed get() = _feed!!

	private val _feedData = MutableLiveData<String>()
	val feedData: LiveData<String> get() = _feedData

	private var call: Call? = null

	fun initialize(feedId: Long) // TODO: is this really the correct way to init a ViewModel with a parameter?
	{
		if(_feed != null)
			return

		_feed = AppDatabase.getInstance(getApplication()).feedDao().getById(feedId)

		val url = _feed?.fullUri?.toString() ?: return

		val request = Request.Builder()
				.url(url)
				.build()

		call = OkHttpClient().newCall(request)
		call?.enqueue(object: Callback
		{
			override fun onResponse(call: Call, response: Response)
			{
				_feedData.postValue(response.body()?.string() ?: "wtf")
			}

			override fun onFailure(call: Call, e: IOException)
			{
				_feedData.postValue("fail")
			}
		})
	}

	override fun onCleared()
	{
		super.onCleared()
		call?.cancel()
	}
}