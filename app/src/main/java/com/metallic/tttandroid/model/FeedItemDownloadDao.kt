package com.metallic.tttandroid.model

import android.arch.persistence.room.*

private const val tableName = "feed_item_download"

@Dao
interface FeedItemDownloadDao
{
	@Query("SELECT * FROM $tableName")
	fun getAll(): List<FeedItemDownload>

	@Query("UPDATE $tableName SET download_id = null, download_file = :arg1 WHERE download_id = :arg0")
	fun finishDownload(downloadId: Long, downloadFile: String?)

	@Query("UPDATE $tableName SET download_file = null, lecture_dir = :arg1 WHERE download_file = :arg0")
	fun finishExtract(downloadFile: String, lectureDir: String?)

	@Query("SELECT * FROM feed_item JOIN feed_item_download ON feed_item.link = feed_item_download.link WHERE download_id = :arg0")
	fun feedItemForDownloadId(downloadId: Long): FeedItem?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(feed: FeedItemDownload)

	@Update
	fun update(feed: FeedItemDownload)

	@Delete
	fun delete(feed: FeedItemDownload)
}