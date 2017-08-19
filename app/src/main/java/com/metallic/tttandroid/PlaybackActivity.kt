package com.metallic.tttandroid

import android.arch.lifecycle.ViewModelProviders
import android.os.AsyncTask
import android.os.Bundle
import com.metallic.tttandroid.ttt.core.Recording
import com.metallic.tttandroid.utils.LifecycleAppCompatActivity
import com.metallic.tttandroid.utils.logError
import com.metallic.tttandroid.viewmodel.PlaybackViewModel
import kotlinx.android.synthetic.main.activity_playback.*

class PlaybackActivity: LifecycleAppCompatActivity()
{
	companion object
	{
		const val EXTRA_FEED_ID = "feed_id"
		const val EXTRA_FEED_ITEM_TITLE = "feed_item_title"
	}

	private lateinit var viewModel: PlaybackViewModel

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_playback)

		val feedId = intent.getLongExtra(EXTRA_FEED_ID, -1)
		val feedItemTitle = intent.getStringExtra(EXTRA_FEED_ITEM_TITLE)

		viewModel = ViewModelProviders.of(this)[PlaybackViewModel::class.java]
		if(!viewModel.initialize(feedId, feedItemTitle))
		{
			logError("Failed to initialize Playback")
			finish()
			return
		}

		test_text_view.text = viewModel.feedItem.feedItem.title

		AsyncTask.execute {
			val recording = Recording(this, viewModel.tttFile, viewModel.audioPlayer, playback_image_view, null)
			recording.play()
		}
	}
}