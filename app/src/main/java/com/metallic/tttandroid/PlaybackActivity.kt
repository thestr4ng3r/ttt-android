package com.metallic.tttandroid

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.metallic.tttandroid.adapter.IndexRecyclerViewAdapter
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

	private lateinit var playbackControlsView: View
	private lateinit var seekBar: SeekBar
	private lateinit var playButton: ImageButton
	private lateinit var durationTextView: TextView
	private lateinit var positionTextView: TextView

	private lateinit var indexRecyclerView: RecyclerView
	private lateinit var indexRecyclerViewAdapter: IndexRecyclerViewAdapter

	private lateinit var updateHandler: Handler

	private var currentPositionSeconds = 0

	private var playbackControlsDisplayed = true
	private lateinit var hidePlaybackControlsHandler: Handler

	private var indexDisplayed = false

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_playback)

		window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
				View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
				View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

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

		playbackControlsView = playback_controls_view

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

		hidePlaybackControlsHandler = Handler()

		imageView.setOnClickListener { animatePlaybackControls() }
		playbackControlsView.setOnClickListener { animatePlaybackControls(true) }

		animatePlaybackControls(true)


		indexRecyclerView = index_recycler_view
		val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
		indexRecyclerView.layoutManager = layoutManager
		val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)
		indexRecyclerView.addItemDecoration(dividerItemDecoration)

		indexRecyclerViewAdapter = IndexRecyclerViewAdapter()
		indexRecyclerViewAdapter.recording = viewModel.recording
		indexRecyclerView.adapter = indexRecyclerViewAdapter

		toggle_index_button.setOnClickListener { animateIndex() }
	}

	private fun animatePlaybackControls(show: Boolean = !playbackControlsDisplayed)
	{
		val fullDuration = resources.getInteger(R.integer.playback_controls_anim_duration).toLong()
		val targetAlpha = if(show) 1.0f else 0.0f
		val currentAlpha = playbackControlsView.alpha
		val duration = (fullDuration * Math.abs(targetAlpha - currentAlpha)).toLong()

		if(duration > 0L)
		{
			playbackControlsView.animate()
					.alpha(targetAlpha)
					.setDuration(duration)
					.start()

			playbackControlsDisplayed = show
		}

		if(playbackControlsDisplayed)
			schedulePlaybackControlsHiding()
	}

	private fun animateIndex(show: Boolean = !indexDisplayed)
	{
		val fullDuration = resources.getInteger(R.integer.index_sidebar_anim_duration).toLong()
		val targetScale = if(show) 1.0f else 0.0f
		val currentScale = indexRecyclerView.scaleX
		val duration = (fullDuration * Math.abs(targetScale - currentScale)).toLong()

		if(duration > 0L)
		{
			if(show)
				indexRecyclerView.visibility = View.VISIBLE

			val animation = indexRecyclerView.animate()
					.scaleX(targetScale)
					.setDuration(duration)

			if(!show)
				animation.withEndAction {
					indexRecyclerView.visibility = View.GONE
				}

			animation.start()

			indexDisplayed = show
		}
	}

	private val hidePlaybackControlsCallback = Runnable {
		if(viewModel.recording?.isPlaying ?: true)
			animatePlaybackControls(false)
	}

	private fun schedulePlaybackControlsHiding(clearOnly: Boolean = false)
	{
		hidePlaybackControlsHandler.removeCallbacks(hidePlaybackControlsCallback)

		if(clearOnly)
			return

		val timeout = resources.getInteger(R.integer.playback_controls_hide_timeout)
		hidePlaybackControlsHandler.postDelayed(hidePlaybackControlsCallback, timeout.toLong())
	}

	override fun onDestroy()
	{
		super.onDestroy()
		viewModel.recording?.removeListener(this)
		schedulePlaybackControlsHiding(true)
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

		schedulePlaybackControlsHiding(!playing)
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