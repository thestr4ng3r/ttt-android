package com.metallic.tttandroid.model

import android.app.DownloadManager
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import java.io.File

@RequiresApi(Build.VERSION_CODES.M)
fun FeedItemWithDownload.deleteDownloadFiles(downloadManager: DownloadManager)
{
	if(isExtracting)
		return

	downloadId?.let { id ->
		downloadManager.remove(id)
	}

	lectureDir?.let { dir ->
		val file = File(dir)
		if(file.exists())
			file.deleteRecursively()
	}
}