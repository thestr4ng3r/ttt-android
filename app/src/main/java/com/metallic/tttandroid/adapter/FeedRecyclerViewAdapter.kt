package com.metallic.tttandroid.adapter

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.text.format.Formatter
import android.view.View
import android.view.ViewGroup
import com.metallic.tttandroid.FeedActivity
import com.metallic.tttandroid.R
import com.metallic.tttandroid.model.Feed
import com.metallic.tttandroid.model.FeedItem
import com.metallic.tttandroid.model.FeedItemWithDownload
import com.metallic.tttandroid.utils.inflate
import kotlinx.android.synthetic.main.item_feed_item.view.*
import java.text.DateFormat

class FeedRecyclerViewAdapter : RecyclerView.Adapter<FeedRecyclerViewAdapter.ViewHolder>()
{
	var items: List<FeedItemWithDownload>? = null
		set(value)
		{
			field = value
			notifyDataSetChanged()
		}

	var itemOnClickCallback: ((FeedItemWithDownload) -> Unit)? = null

	override fun getItemCount() = items?.size ?: 0
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent.inflate(R.layout.item_feed_item))

	override fun onBindViewHolder(holder: ViewHolder, position: Int)
	{
		val item = items?.get(position) ?: return
		holder.titleTextView.text = item.feedItem.title
		holder.durationTextView.text = item.downloadFile ?:
										item.downloadId?.toString() ?:
										Formatter.formatFileSize(holder.itemView.context, item.feedItem.fileSize)

		holder.itemView.setOnClickListener {
			itemOnClickCallback?.let { callback -> callback(item) }
		}
	}

	inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
	{
		val titleTextView = itemView.title_text_view!!
		val durationTextView = itemView.duration_text_view!!
	}
}