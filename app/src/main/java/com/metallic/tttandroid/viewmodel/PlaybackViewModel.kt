package com.metallic.tttandroid.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.metallic.tttandroid.model.AppDatabase
import com.metallic.tttandroid.model.FeedItemWithDownload

class PlaybackViewModel(application: Application): AndroidViewModel(application)
{
	private var _feedItem: FeedItemWithDownload? = null
	val feedItem: FeedItemWithDownload get() = _feedItem!!

	fun initialize(feedId: Long, feedItemTitle: String): Boolean
	{
		if(_feedItem != null)
			return true

		val db = AppDatabase.getInstance(getApplication())

		_feedItem = db.feedItemDao().getSingleWithDownloads(feedId, feedItemTitle)
		if(_feedItem == null)
			return false

		return true
	}
}