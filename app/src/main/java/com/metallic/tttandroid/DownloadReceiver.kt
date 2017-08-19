package com.metallic.tttandroid

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.metallic.tttandroid.model.AppDatabase
import com.metallic.tttandroid.utils.log

class DownloadReceiver: BroadcastReceiver()
{
	override fun onReceive(context: Context, intent: Intent)
	{
		when(intent.action)
		{
			DownloadManager.ACTION_DOWNLOAD_COMPLETE ->
			{
				val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)

				val db = AppDatabase.getInstance(context)

				val feedItem = db.feedItemDownloadDao().feedItemForDownloadId(downloadId)

				val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
				val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadId))

				if(!cursor.moveToNext())
					return

				val localUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))

				db.feedItemDownloadDao().finishDownload(downloadId, localUri)

				if(feedItem == null)
				{
					log("Failed to get feed item for completed download")
					return
				}

				log("Download completed for feed item ${feedItem.title}")

				val extractIntent = Intent(context, ExtractService::class.java)
				extractIntent.putExtra(ExtractService.EXTRA_DOWNLOAD_FILE, localUri)
				extractIntent.putExtra(ExtractService.EXTRA_LECTURE_NAME, feedItem.title)
				context.startService(extractIntent)
			}
		}
	}

}