package com.metallic.tttandroid.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.metallic.tttandroid.R
import com.metallic.tttandroid.ttt.core.Recording
import com.metallic.tttandroid.utils.inflate
import kotlinx.android.synthetic.main.item_index_entry.view.*


class IndexRecyclerViewAdapter : RecyclerView.Adapter<IndexRecyclerViewAdapter.ViewHolder>()
{
	var recording: Recording? = null
		set(value)
		{
			field = value
			notifyDataSetChanged()
		}

	override fun getItemCount() = recording?.index?.index?.size ?: 0
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent.inflate(R.layout.item_index_entry))

	override fun onBindViewHolder(holder: ViewHolder, position: Int)
	{
		val entry = recording?.index?.index?.get(position) ?: return

		holder.numberTextView.text = position.toString()
		holder.thumbnailImageView.setImageBitmap(entry.bitmap)
	}

	inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
	{
		val numberTextView = itemView.number_text_view!!
		val thumbnailImageView = itemView.thumbnail_image_view!!
	}
}