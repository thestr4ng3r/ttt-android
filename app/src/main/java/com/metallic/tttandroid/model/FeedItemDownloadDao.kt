package com.metallic.tttandroid.model

import android.arch.persistence.room.*

private const val tableName = "feed_item_download"

@Dao
interface FeedItemDownloadDao
{
	@Query("SELECT * FROM $tableName")
	fun getAll(): List<FeedItemDownload>

	@Query("DELETE FROM $tableName WHERE link IN(:arg0)")
	fun deleteDownloads(link: List<String>)

	@Query("UPDATE $tableName SET download_file = null WHERE download_id = :arg0")
	fun finishDownloadFailed(downloadId: Long)

	@Query("UPDATE $tableName SET download_id = null WHERE download_id = :arg0")
	fun finishDownloadCanceled(downloadId: Long)

	@Query("UPDATE $tableName SET download_id = null, download_file = :arg1 WHERE download_id = :arg0")
	fun finishDownload(downloadId: Long, downloadFile: String?)

	@Query("UPDATE $tableName SET download_file = null, lecture_dir = :arg1 WHERE download_file = :arg0")
	fun finishExtract(downloadFile: String, lectureDir: String?)

	@Query("UPDATE $tableName SET last_playback_position = :arg1 WHERE link = :arg0")
	fun setLastPlaybackPosition(link: String, position: Int)

	@Query("SELECT * FROM feed_item JOIN feed_item_download ON feed_item.link = feed_item_download.link WHERE download_id = :arg0")
	fun feedItemForDownloadId(downloadId: Long): FeedItem?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(feed: FeedItemDownload)

	@Update
	fun update(feed: FeedItemDownload)

	@Delete
	fun delete(feed: FeedItemDownload)

	@Query("DELETE FROM $tableName WHERE link IN(:arg0)")
	fun deleteByLinks(links: List<String>)
}