package com.metallic.tttandroid.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.metallic.tttandroid.R
import com.metallic.tttandroid.ttt.core.IndexEntry
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

	var currentIndex: Int? = null
		set(value)
		{
			if(field == value)
				return

			val old = field
			field = value

			if(old != null)
				notifyItemChanged(old)

			if(value != null)
				notifyItemChanged(value)
		}

	var itemOnClickCallback: ((IndexEntry) -> Unit)? = null

	override fun getItemCount() = recording?.index?.index?.size ?: 0
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent.inflate(R.layout.item_index_entry))

	override fun onBindViewHolder(holder: ViewHolder, position: Int)
	{
		val entry = recording?.index?.index?.get(position) ?: return
		val isCurrent = currentIndex == position

		holder.numberTextView.text = (position + 1).toString()
		holder.thumbnailImageView.setImageBitmap(entry.bitmap)
		holder.itemView.setBackgroundResource(if(isCurrent) R.color.index_entry_background_current else R.color.index_entry_background_default)

		holder.clickView.setOnClickListener {
			itemOnClickCallback?.let { it(entry) }
		}
	}

	inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
	{
		val numberTextView = itemView.number_text_view!!
		val thumbnailImageView = itemView.thumbnail_image_view!!
		val clickView = itemView.click_view!!
	}
}