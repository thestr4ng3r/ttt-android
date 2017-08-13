package com.metallic.tttandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*

class EditFeedActivity: AppCompatActivity()
{
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_edit_feed)

		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
		supportActionBar?.setDisplayShowTitleEnabled(false)
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
	}
}