package com.metallic.tttandroid.adapter

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.metallic.tttandroid.FeedActivity
import com.metallic.tttandroid.R
import com.metallic.tttandroid.model.Feed
import com.metallic.tttandroid.utils.inflate
import kotlinx.android.synthetic.main.item_feed.view.*
import java.text.DateFormat

class FeedListRecyclerViewAdapter : RecyclerView.Adapter<FeedListRecyclerViewAdapter.ViewHolder>()
{
	private val DATE_FORMAT_DATE = DateFormat.getDateInstance()!!

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
		holder.startDateTextView.text = DATE_FORMAT_DATE.format(item.startDate.calendar.time)

		holder.itemView.setOnClickListener {
			val intent = Intent(holder.itemView.context, FeedActivity::class.java)
			intent.putExtra(FeedActivity.EXTRA_FEED_ID, item.id)
			holder.itemView.context.startActivity(intent)
		}
	}

	inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
	{
		val nameTextView = itemView.name_text_view!!
		val startDateTextView = itemView.start_date_text_view!!
	}
}