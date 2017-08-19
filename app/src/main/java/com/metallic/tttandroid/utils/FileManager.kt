package com.metallic.tttandroid.utils

import java.io.File

import android.os.Environment

/**
 * This class provides the storage path for the lecture. It also checks if the
 * storage is accessible

 * @author Thomas Krex
 */
class FileManager(private val root: File)
{
	fun getLectureDir(name: String): File
	{
		val lectureDir = File(root, name)
		if (!lectureDir.exists())
			lectureDir.mkdirs()

		return lectureDir
	}

	companion object
	{
		val isExternalStorageWritable get() = (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)
	}
}
