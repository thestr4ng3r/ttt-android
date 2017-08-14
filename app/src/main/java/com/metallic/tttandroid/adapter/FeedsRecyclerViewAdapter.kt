package com.metallic.tttandroid.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.metallic.tttandroid.R
import com.metallic.tttandroid.model.Feed
import com.metallic.tttandroid.utils.inflate
import kotlinx.android.synthetic.main.item_feed.view.*

class FeedsRecyclerViewAdapter: RecyclerView.Adapter<FeedsRecyclerViewAdapter.ViewHolder>()
{
	var items: List<Feed>? = null
		set(value)
		{
			field = value
			notifyDataSetChanged()
		}

	override fun getItemCount() = items?.size ?: 0
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent.inflate(R.layout.item_feed))

	override fun onBindViewHolder(holder: ViewHolder, position: Int)
	{
		val item = items?.get(position) ?: return
		holder.nameTextView.text = item.name
	}

	inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
	{
		val nameTextView = itemView.name_text_view!!
	}
}