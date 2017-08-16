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
				AppDatabase.getInstance(context).feedItemDownloadDao().finishDownload(downloadId, "test")
				println("download completed $downloadId")
			}
		}
	}

}