package com.metallic.tttandroid

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.metallic.tttandroid.adapter.FeedListRecyclerViewAdapter
import com.metallic.tttandroid.utils.LifecycleAppCompatActivity
import com.metallic.tttandroid.viewmodel.FeedListViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : LifecycleAppCompatActivity()
{
	private lateinit var recyclerView: RecyclerView
	private lateinit var recyclerViewAdapter: FeedListRecyclerViewAdapter

	private lateinit var viewModel: FeedListViewModel

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

		recyclerViewAdapter = FeedListRecyclerViewAdapter()
		recyclerView.adapter = recyclerViewAdapter

		fab.setOnClickListener {
			val intent = Intent(this, EditFeedActivity::class.java)
			startActivity(intent)
		}

		viewModel = ViewModelProviders.of(this)[FeedListViewModel::class.java]
		viewModel.feeds.observe(this, Observer { items ->
			recyclerViewAdapter.items = items
			empty_text_view.visibility = if(items?.isEmpty() ?: true) View.VISIBLE else View.GONE
		})
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean
	{
		menuInflater.inflate(R.menu.activity_main_toolbar, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean
	{
		when(item.itemId)
		{
			R.id.action_about ->
			{
				val intent = Intent(this, AboutActivity::class.java)
				startActivity(intent)
			}
		}

		return super.onOptionsItemSelected(item)
	}
}
