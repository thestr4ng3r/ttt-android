package com.metallic.tttandroid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.metallic.tttandroid.adapter.FeedsRecyclerViewAdapter
import com.metallic.tttandroid.model.Feed
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity()
{
	private val testFeeds = arrayOf(
			Feed(42, "Feed 1", "url"),
			Feed(42, "Feed 2", "url"),
			Feed(42, "Feed 3", "url"),
			Feed(42, "Feed 4", "url"),
			Feed(42, "Feed 5", "url"),
			Feed(42, "Feed 6", "url"),
			Feed(42, "Feed 7", "url"),
			Feed(42, "Feed 8", "url"),
			Feed(42, "Feed 9", "url"),
			Feed(42, "Feed 10", "url"),
			Feed(42, "Feed 11", "url"),
			Feed(42, "Feed 12", "url"))

	private lateinit var recyclerView: RecyclerView
	private lateinit var recyclerViewAdapter: FeedsRecyclerViewAdapter

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		setSupportActionBar(toolbar)
		title = getString(R.string.activity_main_title)

		recyclerView = recycler_view
		val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
		recyclerView.layoutManager = layoutManager
		val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)
		recyclerView.addItemDecoration(dividerItemDecoration)

		recyclerViewAdapter = FeedsRecyclerViewAdapter(testFeeds)
		recyclerView.adapter = recyclerViewAdapter

		fab.setOnClickListener {
			val intent = Intent(this, EditFeedActivity::class.java)
			startActivity(intent)
		}
	}
}
