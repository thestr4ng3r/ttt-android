package com.metallic.tttandroid.model

import android.app.DownloadManager
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.M)
fun Feed.cleanupAndDelete(context: Context)
{
	val db = AppDatabase.getInstance(context)
	val downloadManager = context.getSystemService(DownloadManager::class.java)

	val items = db.feedItemDao().getByFeedIdWithDownloads(id)
	for(item in items)
		item.deleteDownloadFiles(downloadManager)

	db.feedItemDownloadDao().deleteByLinks(items.map { it.feedItem.link })
	db.feedDao().delete(this)
}