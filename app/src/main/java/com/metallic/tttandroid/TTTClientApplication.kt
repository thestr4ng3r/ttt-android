package com.metallic.tttandroid

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.support.annotation.RequiresApi

class TTTClientApplication: Application()
{
	companion object
	{
		val NOTIFICATION_CHANNEL_ID_EXTRACT = "notification_channel_extract"
	}

	@RequiresApi(Build.VERSION_CODES.O)
	override fun onCreate()
	{
		super.onCreate()

		val notificationManager = getSystemService(NotificationManager::class.java)

		val extractNotificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID_EXTRACT,
				getString(R.string.notification_channel_extract_name),
				NotificationManager.IMPORTANCE_LOW)
		extractNotificationChannel.description = getString(R.string.notification_channel_extract_description)

		notificationManager.createNotificationChannel(extractNotificationChannel)
	}
}