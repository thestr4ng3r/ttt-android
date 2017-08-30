package com.metallic.tttandroid.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import com.metallic.tttandroid.model.AppDatabase
import com.metallic.tttandroid.model.FeedItem
import com.metallic.tttandroid.model.FeedItemIdentifier
import com.metallic.tttandroid.model.FeedItemWithDownload
import com.metallic.tttandroid.ttt.RecordingGraphicsLiveData
import com.metallic.tttandroid.ttt.core.Index
import com.metallic.tttandroid.ttt.core.Recording
import kotlinx.android.synthetic.main.activity_playback.*
import java.io.File

class PlaybackViewModel(application: Application): AndroidViewModel(application), Index.Listener
{
	var feedItem: FeedItemWithDownload? = null
		private set

	var audioPlayer: MediaPlayer? = null
		private set

	lateinit var tttFile: File private set

	var recording: Recording? = null
		private set

	private val _currentIndex = MutableLiveData<Int>()
	val currentIndex: LiveData<Int> get() = _currentIndex

	lateinit var graphicsLiveData: RecordingGraphicsLiveData

	private var initializeAsyncTask: InitializeAsyncTask? = null
	private var initializedCallbacks = mutableSetOf<((success: Boolean) -> Unit)>()
	fun removeInitializedCallback(callback: ((success: Boolean) -> Unit)) = initializedCallbacks.remove(callback)

	/**
	 * Load recording asynchronously and start playback. Must be called on the main thread.
	 */
	fun initialize(feedItemIdentifier: FeedItemIdentifier, callback: ((success: Boolean) -> Unit))
	{
		initializedCallbacks.add(callback)

		// already initialized?
		if(recording != null)
		{
			initializationFinished(true)
			return
		}

		// currently initializing?
		if(initializeAsyncTask != null)
			return

		val initAsyncTask = InitializeAsyncTask()
		this.initializeAsyncTask = initAsyncTask
		initAsyncTask.execute(feedItemIdentifier)
	}

	private fun initializationFinished(success: Boolean)
	{
		for(callback in initializedCallbacks)
			callback(success)
		initializedCallbacks.clear()
	}

	private inner class InitializeAsyncTask: AsyncTask<FeedItemIdentifier, Unit, Recording>()
	{
		override fun doInBackground(vararg params: FeedItemIdentifier): Recording?
		{
			val context = getApplication<Application>()

			val db = AppDatabase.getInstance(getApplication())

			val feedItemIdentifier = params[0]
			val feedItem = db.feedItemDao().getSingleWithDownloads(feedItemIdentifier.feedId, feedItemIdentifier.feedItemTitle) ?: return null
			val lectureDir = feedItem.lectureDir ?: return null

			val lectureDirUri = Uri.fromFile(File(lectureDir))
			val audioUri = lectureDirUri.buildUpon().appendPath(feedItem.feedItem.title + ".mp3").build()
			val tttUri = lectureDirUri.buildUpon().appendPath(feedItem.feedItem.title + ".ttt").build()

			audioPlayer = MediaPlayer.create(context, audioUri) ?: return null
			tttFile = File(tttUri.path)

			val recording = Recording(tttFile, audioPlayer, feedItem.lastPlaybackPosition)

			return recording
		}

		override fun onPostExecute(recording: Recording?)
		{
			initializeAsyncTask = null

			if(isCancelled)
				return

			if(recording == null)
			{
				initializationFinished(false)
				return
			}

			_currentIndex.value = recording.index.currentIndex
			recording.index.setListener(this@PlaybackViewModel)
			graphicsLiveData = RecordingGraphicsLiveData(recording.graphicsContext)
			recording.play()

			this@PlaybackViewModel.recording = recording

			initializationFinished(true)
		}
	}

	override fun onCleared()
	{
		super.onCleared()
		initializeAsyncTask?.cancel(true)
		recording?.close()
		audioPlayer?.release()
	}

	override fun currentIndexChanged(index: Int)
	{
		_currentIndex.postValue(index)
	}

	fun saveCurrentPlaybackPosition()
	{
		val time = recording?.time ?: return
		val link = feedItem?.feedItem?.link ?: return
		AppDatabase.getInstance(getApplication()).feedItemDownloadDao().setLastPlaybackPosition(link, time)
	}
}