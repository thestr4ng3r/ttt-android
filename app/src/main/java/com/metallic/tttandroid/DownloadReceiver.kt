package com.metallic.tttandroid

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.metallic.tttandroid.model.AppDatabase

class DownloadReceiver: BroadcastReceiver()
{
	override fun onReceive(context: Context, intent: Intent)
	{
		when(intent.action)
		{
			DownloadManager.ACTION_DOWNLOAD_COMPLETE ->
			{
				val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
				println("download completed $downloadId")

				val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
				val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadId))

				// TODO cursor.moveToNext()

				val localUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))

				AppDatabase.getInstance(context).feedItemDownloadDao().finishDownload(downloadId, localUri)
			}
		}
	}

}