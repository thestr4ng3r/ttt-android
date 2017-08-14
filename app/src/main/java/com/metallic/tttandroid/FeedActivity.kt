package com.metallic.tttandroid

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.metallic.tttandroid.utils.LifecycleAppCompatActivity
import com.metallic.tttandroid.viewmodel.FeedViewModel
import kotlinx.android.synthetic.main.activity_feed.*

class FeedActivity: LifecycleAppCompatActivity()
{
	companion object
	{
		const val FEED_ID_EXTRA = "feed_id"
	}

	private lateinit var viewModel: FeedViewModel

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_feed)

		setSupportActionBar(toolbar)

		val feedId = intent.getLongExtra(FEED_ID_EXTRA, 0)

		viewModel = ViewModelProviders.of(this)[FeedViewModel::class.java]
		viewModel.initialize(feedId)

		title = viewModel.feed.name

		viewModel.feedData.observe(this, Observer { data ->
			test_text_view.text = data
		})
	}
}