package com.metallic.tttandroid.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.metallic.tttandroid.model.AppDatabase
import com.metallic.tttandroid.model.Feed
import com.metallic.tttandroid.model.FeedItem
import com.metallic.tttandroid.model.FeedItemWithDownload
import com.metallic.tttandroid.request.FeedResponse
import com.metallic.tttandroid.request.enqueueFeed
import okhttp3.*

class FeedViewModel(application: Application): AndroidViewModel(application)
{
	private var _feed: Feed? = null
	val feed: Feed get() = _feed!!

	private val _feedData = MutableLiveData<FeedResponse>()
	val feedData: LiveData<FeedResponse> get() = _feedData

	lateinit var feedItems: LiveData<List<FeedItemWithDownload>>

	private var call: Call? = null

	fun initialize(feedId: Long)
	{
		if(_feed != null)
			return

		val db = AppDatabase.getInstance(getApplication())

		_feed = db.feedDao().getById(feedId)
		feedItems = db.feedItemDao().getByFeedIdWithDownloads(feed.id)

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

			val items = response.items
			if(items != null)
			{
				for(item in items)
				{
					item.feedId = _feed!!.id
				}
				db.feedItemDao().insert(items)
			}
		}
	}

	override fun onCleared()
	{
		super.onCleared()
		call?.cancel()
	}
}