package com.metallic.tttandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import com.metallic.tttandroid.model.AppDatabase
import com.metallic.tttandroid.model.Feed
import kotlinx.android.synthetic.main.activity_edit_feed.*

class EditFeedActivity: AppCompatActivity()
{
	companion object
	{
		val FEED_ID_EXTRA = "feed_id"
	}

	private lateinit var feed: Feed
	private var newFeed = false

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_edit_feed)

		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
		supportActionBar?.setDisplayShowTitleEnabled(false)

		if(intent.hasExtra(FEED_ID_EXTRA))
		{
			feed = AppDatabase.getInstance(this).feedDao()
					.getById(intent.getLongExtra(FEED_ID_EXTRA, 0))
			newFeed = false
		}
		else
		{
			feed = Feed()
			newFeed = true
		}

		feed_name_edit_text.setText(feed.name)
		feed_name_edit_text.addTextChangedListener(object : TextWatcher
		{
			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
			override fun afterTextChanged(s: Editable)
			{
				feed.name = s.toString()
			}
		})

		url_edit_text.setText(feed.url)
		url_edit_text.addTextChangedListener(object : TextWatcher
		{
			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
			override fun afterTextChanged(s: Editable)
			{
				feed.url = s.toString()
			}
		})
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean
	{
		menuInflater.inflate(R.menu.activity_edit_event_toolbar, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean
	{
		when(item.itemId)
		{
			android.R.id.home ->
			{
				onBackPressed()
				return true
			}

			R.id.action_save ->
			{
				save()
				return true
			}
		}
		return super.onOptionsItemSelected(item)
	}

	private fun save()
	{
		val dao = AppDatabase.getInstance(this).feedDao()

		if(newFeed)
			dao.insert(feed)
		else
			dao.update(feed)

		finish()
	}
}