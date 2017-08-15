package com.metallic.tttandroid.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

//@Entity
class FeedItem
{
	//@PrimaryKey(autoGenerate = true)
	//@ColumnInfo(name = "id")
	var id: Int = 0

	var title: String = ""

	var link: String = ""

	var description: String = ""

	var date: String = ""

	var fileSize: Long = 0
}