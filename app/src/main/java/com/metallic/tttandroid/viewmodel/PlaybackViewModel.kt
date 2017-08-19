package com.metallic.tttandroid.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.metallic.tttandroid.model.AppDatabase
import com.metallic.tttandroid.model.FeedItemWithDownload
import java.io.File

class PlaybackViewModel(application: Application): AndroidViewModel(application)
{
	private var _feedItem: FeedItemWithDownload? = null
	val feedItem: FeedItemWithDownload get() = _feedItem!!

	private var _audioPlayer: MediaPlayer? = null
	val audioPlayer: MediaPlayer get() = _audioPlayer!!

	lateinit var tttFile: File private set

	fun initialize(feedId: Long, feedItemTitle: String): Boolean
	{
		if(_feedItem != null)
			return true

		val context = getApplication<Application>()

		val db = AppDatabase.getInstance(getApplication())

		_feedItem = db.feedItemDao().getSingleWithDownloads(feedId, feedItemTitle) ?: return false
		val lectureDir = feedItem.lectureDir ?: return false

		val lectureDirUri = Uri.parse(lectureDir)
		val audioUri = lectureDirUri.buildUpon().appendPath(feedItem.feedItem.title + ".mp3").build()
		val tttUri = lectureDirUri.buildUpon().appendPath(feedItem.feedItem.title + ".ttt").build()

		_audioPlayer = MediaPlayer.create(context, audioUri) ?: return false
		tttFile = File(tttUri.toString())

		return true
	}

	override fun onCleared()
	{
		super.onCleared()
		_audioPlayer?.release()
	}
}