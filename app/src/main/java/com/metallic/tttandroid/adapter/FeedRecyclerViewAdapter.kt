package com.metallic.tttandroid.adapter

import android.app.DownloadManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.metallic.tttandroid.R
import com.metallic.tttandroid.model.FeedItemWithDownload
import com.metallic.tttandroid.utils.inflate
import kotlinx.android.synthetic.main.item_feed_item.view.*
import java.text.DateFormat

class FeedRecyclerViewAdapter : RecyclerView.Adapter<FeedRecyclerViewAdapter.ViewHolder>()
{
	private val DATE_FORMAT_DATE = DateFormat.getDateInstance()!!

	var items: List<FeedItemWithDownload>? = null
		set(value)
		{
			field = value
			notifyDataSetChanged()
		}

	var itemOnClickCallback: ((FeedItemWithDownload) -> Unit)? = null

	override fun getItemCount() = items?.size ?: 0
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent.inflate(R.layout.item_feed_item))

	@RequiresApi(Build.VERSION_CODES.M)
	override fun onBindViewHolder(holder: ViewHolder, position: Int)
	{
		val item = items?.get(position) ?: return

		val context = holder.itemView.context

		holder.titleTextView.text = item.feedItem.title

		when
		{
			item.isExtracting ->
			{
				holder.subtitleTextView.text = context.getString(R.string.feed_item_subtitle_extracting)
			}

			item.isDownloading ->
			{
				var downloadStatus = DownloadManager.STATUS_RUNNING

				val downloadId = item.downloadId

				if(downloadId != null)
				{
					val downloadManager = context.getSystemService(DownloadManager::class.java)
					val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadId))

					if(cursor.moveToNext())
					{
						downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
					}
				}

				holder.subtitleTextView.text = context.getString(when(downloadStatus)
				{
					DownloadManager.STATUS_FAILED -> R.string.feed_item_subtitle_download_failed
					DownloadManager.STATUS_PAUSED -> R.string.feed_item_subtitle_download_paused
					else -> R.string.feed_item_subtitle_downloading
				})
			}

			else ->
			{
				holder.subtitleTextView.text = DATE_FORMAT_DATE.format(item.feedItem.date)
			}
		}


		holder.iconImageView.setImageResource(when {
			item.isDownloaded -> 	0
			item.isDownloading ->	R.drawable.ic_pause_white_24dp
			else ->					R.drawable.ic_file_download_white_24dp
		})

		holder.itemView.setOnClickListener {
			itemOnClickCallback?.let { callback -> callback(item) }
		}
	}

	inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
	{
		val titleTextView = itemView.title_text_view!!
		val subtitleTextView = itemView.subtitle_text_view!!
		val iconImageView = itemView.icon_image_view!!
	}
}