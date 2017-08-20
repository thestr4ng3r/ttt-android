package com.metallic.tttandroid

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.metallic.tttandroid.ttt.core.Recording
import com.metallic.tttandroid.utils.LifecycleAppCompatActivity
import com.metallic.tttandroid.utils.logError
import com.metallic.tttandroid.viewmodel.PlaybackViewModel
import kotlinx.android.synthetic.main.activity_playback.*

@Suppress("NAME_SHADOWING")
class PlaybackActivity: LifecycleAppCompatActivity(), Recording.Listener
{
	companion object
	{
		const val EXTRA_FEED_ID = "feed_id"
		const val EXTRA_FEED_ITEM_TITLE = "feed_item_title"
	}

	private lateinit var viewModel: PlaybackViewModel

	private lateinit var imageView: ImageView
	private lateinit var seekBar: SeekBar
	private lateinit var playButton: ImageButton
	private lateinit var durationTextView: TextView
	private lateinit var positionTextView: TextView

	private lateinit var updateHandler: Handler

	private var currentPositionSeconds = 0

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

		imageView = playback_image_view

		viewModel.graphicsLiveData.observe(this, Observer { bitmap ->
			imageView.setImageBitmap(bitmap)
			imageView.invalidate()
		})

		seekBar = progress_seek_bar
		seekBar.max = viewModel.recording!!.duration
		seekBar.setOnSeekBarChangeListener(seekBarListener)

		playButton = play_button
		playButton.setOnClickListener { playPause() }

		positionTextView = position_text_view
		durationTextView = duration_text_view

		updateHandler = Handler()
		viewModel.recording!!.addListener(this)

		updatePlayButton(viewModel.recording?.isPlaying)
		updatePositionTextView(viewModel.recording?.time)
		updateDurationTextView(viewModel.recording?.duration)
	}

	override fun onDestroy()
	{
		super.onDestroy()
		viewModel.recording?.removeListener(this)
	}

	private fun updatePlayButton(playing: Boolean?)
	{
		val playing = playing ?: false

		playButton.setImageResource(
				if(playing) R.drawable.ic_pause_white_24dp
				else R.drawable.ic_play_arrow_white_24dp)
	}

	private fun formatTime(value: Int) = "%d:%02d".format(value / 60000, (value / 1000) % 60)

	private fun updateDurationTextView(duration: Int?)
	{
		val duration = duration ?: 0
		durationTextView.text = formatTime(duration)
	}

	private fun updatePositionTextView(position: Int?)
	{
		val position = position ?: 0
		currentPositionSeconds = position / 1000
		positionTextView.text = formatTime(position)
	}

	private fun updateSeekBarPosition(position: Int?)
	{
		val position = position ?: 0
		seekBar.progress = position
	}

	private var seekBarIsTrackingTouch = false

	private val seekBarListener = object: SeekBar.OnSeekBarChangeListener
	{
		override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean)
		{
			if(!fromUser)
				return
			viewModel.recording?.setTime(progress, true)
		}

		override fun onStartTrackingTouch(seekBar: SeekBar)
		{
			seekBarIsTrackingTouch = true
		}

		override fun onStopTrackingTouch(seekBar: SeekBar)
		{
			seekBarIsTrackingTouch = false
		}
	}

	private fun playPause()
	{
		viewModel.recording?.let { recording ->
			if(recording.isPlaying)
				recording.pause()
			else
				recording.play()
		}
	}

	override fun playbackStateChanged(recording: Recording, playing: Boolean)
	{
		updateHandler.post {
			updatePlayButton(playing)
		}
	}

	override fun timeChanged(recording: Recording, time: Int)
	{
		if(currentPositionSeconds == time / 1000)
			return

		currentPositionSeconds = time / 1000
		updateHandler.post {
			updatePositionTextView(time)
			if(!seekBarIsTrackingTouch)
				updateSeekBarPosition(time)
		}
	}

}