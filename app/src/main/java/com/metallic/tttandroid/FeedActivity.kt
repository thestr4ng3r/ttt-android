package com.metallic.tttandroid

import android.app.AlertDialog
import android.app.DownloadManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.metallic.tttandroid.adapter.FeedRecyclerViewAdapter
import com.metallic.tttandroid.model.AppDatabase
import com.metallic.tttandroid.model.FeedItem
import com.metallic.tttandroid.model.FeedItemDownload
import com.metallic.tttandroid.model.FeedItemWithDownload
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

	private lateinit var recyclerView: RecyclerView
	private lateinit var recyclerViewAdapter: FeedRecyclerViewAdapter

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

		val feedId = intent.getLongExtra(FEED_ID_EXTRA, 0)

		viewModel = ViewModelProviders.of(this)[FeedViewModel::class.java]
		viewModel.initialize(feedId)

		title = viewModel.feed.name

		progress_bar.show()
		/*viewModel.feedData.observe(this, Observer { response ->
			progress_bar.hide()
			val items = response?.items
			if(items == null)
				error_text_view.visibility = View.VISIBLE
			else
				recyclerViewAdapter.items = items
		})*/

		viewModel.feedItems.observe(this, Observer { items ->
			progress_bar.hide()
			/*if(items == null)
				error_text_view.visibility = View.VISIBLE
			else*/
			recyclerViewAdapter.items = items
		})
	}

	private fun itemClicked(feedItem: FeedItemWithDownload)
	{
		if(feedItem.downloadId == null && feedItem.downloadFile == null)
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