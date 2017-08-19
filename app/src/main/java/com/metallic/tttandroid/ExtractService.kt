package com.metallic.tttandroid

import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import com.metallic.tttandroid.model.AppDatabase
import com.metallic.tttandroid.utils.FileManager
import com.metallic.tttandroid.utils.UnzipManager
import java.io.File

class ExtractService: IntentService("ExtractService")
{
	companion object
	{
		val EXTRA_DOWNLOAD_FILE = "download_file"
		val EXTRA_LECTURE_NAME = "lecture_name"
	}

	@RequiresApi(Build.VERSION_CODES.O)
	override fun onHandleIntent(intent: Intent)
	{
		val downloadFile = intent.getStringExtra(EXTRA_DOWNLOAD_FILE)
		val zipFile = File(downloadFile)
		val lectureName = intent.getStringExtra(EXTRA_LECTURE_NAME)


		val notificationManager = getSystemService(NotificationManager::class.java)

		val extractNotificationTag = "extract_$downloadFile"
		val notificationBuilder = Notification.Builder(this, TTTClientApplication.NOTIFICATION_CHANNEL_ID_EXTRACT)
		notificationBuilder.setContentTitle(getString(R.string.notification_extracting_title))
				.setContentText(getString(R.string.notification_extracting_text, lectureName))
				.setSmallIcon(android.R.drawable.stat_sys_download)
				.setProgress(0, 0, true)
		notificationManager.notify(extractNotificationTag, 0, notificationBuilder.build())

		// TODO: check whether external storage is writable

		val lectureDir = FileManager(getExternalFilesDir(null)).getLectureDir(lectureName)

		val unzip = UnzipManager(zipFile.absolutePath, lectureDir.absolutePath)
		unzip.unzip()
		zipFile.delete()

		AppDatabase.getInstance(this).feedItemDownloadDao().finishExtract(downloadFile, lectureDir.absolutePath)

		notificationManager.cancel(extractNotificationTag, 0)

		notificationBuilder.setContentTitle(getString(R.string.notification_download_finished_title, lectureName))
				.setContentText(getString(R.string.notification_download_finished_text))
				.setSmallIcon(android.R.drawable.stat_sys_download_done)
				.setProgress(0, 0, false)
		notificationManager.notify(getString(R.string.notification_download_finished_tag, lectureName), 0, notificationBuilder.build())
	}
}