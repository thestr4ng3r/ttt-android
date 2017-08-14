package com.metallic.tttandroid

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.metallic.tttandroid.adapter.FeedsRecyclerViewAdapter
import com.metallic.tttandroid.model.Feed
import com.metallic.tttandroid.utils.LifecycleAppCompatActivity
import com.metallic.tttandroid.viewmodel.FeedsViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : LifecycleAppCompatActivity()
{
	private lateinit var recyclerView: RecyclerView
	private lateinit var recyclerViewAdapter: FeedsRecyclerViewAdapter

	private lateinit var viewModel: FeedsViewModel

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

		recyclerViewAdapter = FeedsRecyclerViewAdapter()
		recyclerView.adapter = recyclerViewAdapter

		fab.setOnClickListener {
			val intent = Intent(this, EditFeedActivity::class.java)
			startActivity(intent)
		}

		viewModel = ViewModelProviders.of(this)[FeedsViewModel::class.java]
		viewModel.feeds.observe(this, Observer { items -> recyclerViewAdapter.items = items })
	}
}
