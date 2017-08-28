package com.metallic.tttandroid.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "feed_item_download")
class FeedItemDownload
{
	@PrimaryKey
	lateinit var link: String

	@ColumnInfo(name = "download_id")
	var downloadId: Long? = null

	@ColumnInfo(name = "download_file")
	var downloadFile: String? = null

	@ColumnInfo(name = "lecture_dir")
	var lectureDir: String? = null

	@ColumnInfo(name = "last_playback_position")
	var lastPlaybackPosition: Int = 0
}
