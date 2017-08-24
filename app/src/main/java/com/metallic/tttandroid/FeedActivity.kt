package com.metallic.tttandroid

import android.app.AlertDialog
import android.app.DownloadManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.metallic.tttandroid.adapter.FeedRecyclerViewAdapter
import com.metallic.tttandroid.model.AppDatabase
import com.metallic.tttandroid.model.FeedItemDownload
import com.metallic.tttandroid.model.FeedItemWithDownload
import com.metallic.tttandroid.utils.LifecycleAppCompatActivity
import com.metallic.tttandroid.viewmodel.FeedViewModel
import kotlinx.android.synthetic.main.activity_feed.*

class FeedActivity: LifecycleAppCompatActivity()
{
	companion object
	{
		const val EXTRA_FEED_ID = "feed_id"
	}

	private lateinit var viewModel: FeedViewModel

	private lateinit var recyclerView: RecyclerView
	private lateinit var recyclerViewAdapter: FeedRecyclerViewAdapter

	private lateinit var swipeRefreshLayout: SwipeRefreshLayout

	private var snackbar: Snackbar? = null

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_feed)

		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		recyclerView = recycler_view
		val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
		recyclerView.layoutManager = layoutManager
		val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)
		recyclerView.addItemDecoration(dividerItemDecoration)

		recyclerViewAdapter = FeedRecyclerViewAdapter()
		recyclerView.adapter = recyclerViewAdapter
		recyclerViewAdapter.itemOnClickCallback = this::itemClicked

		val feedId = intent.getLongExtra(EXTRA_FEED_ID, -1)

		viewModel = ViewModelProviders.of(this)[FeedViewModel::class.java]
		viewModel.initialize(feedId)

		title = viewModel.feed.name

		swipeRefreshLayout = swipe_refresh_layout
		swipeRefreshLayout.isRefreshing = viewModel.isRefreshing

		viewModel.feedData.observe(this, Observer { response ->
			swipeRefreshLayout.isRefreshing = false
			val items = response?.items
			if(items == null)
				showLoadingError()
			else
			{
				snackbar?.dismiss()
				snackbar = null
			}
		})

		swipeRefreshLayout.setOnRefreshListener { viewModel.refreshFeed() }

		viewModel.feedItems.observe(this, Observer { items ->
			recyclerViewAdapter.items = items
		})
	}

	private fun itemClicked(feedItem: FeedItemWithDownload)
	{
		if(feedItem.isDownloaded)
		{
			val intent = Intent(this, PlaybackActivity::class.java)
			intent.putExtra(PlaybackActivity.EXTRA_FEED_ID, feedItem.feedItem.feedId)
			intent.putExtra(PlaybackActivity.EXTRA_FEED_ITEM_TITLE, feedItem.feedItem.title)
			startActivity(intent)
		}
		else if(!feedItem.isDownloading)
		{
			val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
			val uri = Uri.parse(feedItem.feedItem.link)
			val request = DownloadManager.Request(uri)
					.setVisibleInDownloadsUi(false)
			val download = FeedItemDownload()
			download.link = feedItem.feedItem.link
			download.downloadId = downloadManager.enqueue(request)
			AppDatabase.getInstance(this).feedItemDownloadDao().insert(download)
		}
	}

	private fun refreshFeed()
	{
		swipeRefreshLayout.isRefreshing = true
		viewModel.refreshFeed()
	}

	private fun showLoadingError()
	{
		val snackbar = Snackbar.make(coordinator_layout, R.string.feed_loading_error, Snackbar.LENGTH_INDEFINITE)
		snackbar.setAction(R.string.action_refresh_feed, { refreshFeed() })
		snackbar.show()
		this.snackbar = snackbar
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean
	{
		menuInflater.inflate(R.menu.activity_feed_toolbar, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean
	{
		when(item.itemId)
		{
			android.R.id.home ->
			{
				finish()
				return true
			}

			R.id.action_refresh -> refreshFeed()

			R.id.action_edit ->
			{
				val intent = Intent(this, EditFeedActivity::class.java)
				intent.putExtra(EditFeedActivity.FEED_ID_EXTRA, viewModel.feed.id)
				startActivity(intent)
				return true
			}

			R.id.action_delete ->
			{
				AlertDialog.Builder(this)
						.setMessage(getString(R.string.dialog_delete_feed_message, viewModel.feed.name))
						.setPositiveButton(R.string.dialog_delete_positive, { _, _ ->
							AppDatabase.getInstance(this).feedDao().delete(viewModel.feed)
							finish()
						})
						.setNegativeButton(R.string.dialog_delete_negative, null)
						.create()
						.show()
				return true
			}
		}
		return super.onOptionsItemSelected(item)
	}
}