package com.metallic.tttandroid.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "feed_item",
		primaryKeys = arrayOf("feed_id", "title"),
		foreignKeys = arrayOf(ForeignKey(entity = Feed::class,
										 parentColumns = arrayOf("id"),
										 childColumns = arrayOf("feed_id"),
										 onDelete = ForeignKey.CASCADE)))
class FeedItem
{
	@ColumnInfo(name = "feed_id")
	var feedId: Long = 0

	var title: String = ""

	var link: String = ""

	var description: String = ""

	var date: Date = Date()

	@ColumnInfo(name = "file_size")
	var fileSize: Long = 0
}