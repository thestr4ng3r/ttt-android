package com.metallic.tttandroid

import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import com.metallic.tttandroid.model.AppDatabase
import com.metallic.tttandroid.utils.FileManager
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

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
		val downloadUri = Uri.parse(downloadFile)

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

		val inputStream = contentResolver.openInputStream(downloadUri)
		unzip(inputStream, lectureDir.absolutePath)
		inputStream.close()

		// TODO: delete downloadFile

		AppDatabase.getInstance(this).feedItemDownloadDao().finishExtract(downloadFile, lectureDir.absolutePath)

		notificationManager.cancel(extractNotificationTag, 0)

		notificationBuilder.setContentTitle(getString(R.string.notification_download_finished_title, lectureName))
				.setContentText(getString(R.string.notification_download_finished_text))
				.setSmallIcon(android.R.drawable.stat_sys_download_done)
				.setProgress(0, 0, false)
		notificationManager.notify(getString(R.string.notification_download_finished_tag, lectureName), 0, notificationBuilder.build())
	}


	fun unzip(inputStream: InputStream, destination: String)
	{
		try
		{
			val zipStream = ZipInputStream(inputStream)
			var zEntry: ZipEntry? = zipStream.nextEntry
			while (zEntry != null)
			{
				if (zEntry.isDirectory)
				{
					handleDirectory(zEntry.name, destination)
				}
				else
				{
					val fout = FileOutputStream(destination + "/" + zEntry.name)
					val bufout = BufferedOutputStream(fout)
					val buffer = ByteArray(1024)
					var read = zipStream.read(buffer)
					while (read != -1)
					{
						bufout.write(buffer, 0, read)
						read = zipStream.read(buffer)
					}

					zipStream.closeEntry()
					bufout.close()
					fout.close()
				}

				zEntry = zipStream.nextEntry
			}
			zipStream.close()
		}
		catch (e: Exception)
		{
			Log.e("Unzip", "Unzipping failed")
			e.printStackTrace()
		}
	}

	fun handleDirectory(dir: String, destination: String)
	{
		val f = File(destination + dir)
		if (!f.isDirectory)
		{
			f.mkdirs()
		}
	}
}