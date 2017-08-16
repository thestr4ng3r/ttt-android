package com.metallic.tttandroid.model

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

private const val tableName = "feed_item"

class FeedItemWithDownload
{
	@Embedded(prefix = "feed_item_")
	lateinit var feedItem: FeedItem

	@ColumnInfo(name = "download_id")
	var downloadId: Long? = null

	@ColumnInfo(name = "download_file")
	var downloadFile: String? = null
}

@Dao
interface FeedItemDao
{
	@Query("SELECT * FROM $tableName ORDER BY date")
	fun getAll(): LiveData<List<FeedItem>>

	@Query("SELECT * FROM $tableName WHERE feed_id = :arg0 AND title = :arg1")
	fun get(feedId: Long, title: String): FeedItem

	@Query("SELECT * FROM $tableName WHERE feed_id = :arg0 ORDER BY date")
	fun getByFeedId(id: Long): LiveData<List<FeedItem>>

	@Query("SELECT " +
			"feed_item.feed_id AS feed_item_feed_id, " +
			"feed_item.title AS feed_item_title, " +
			"feed_item.link AS feed_item_link, " +
			"feed_item.description AS feed_item_description, " +
			"feed_item.date AS feed_item_date, " +
			"feed_item.file_size AS feed_item_file_size, " +
			"feed_item_download.download_id AS download_id, " +
			"feed_item_download.download_file AS download_file " +
			"FROM $tableName LEFT OUTER JOIN feed_item_download ON $tableName.link = feed_item_download.link " +
			"WHERE feed_id = :arg0 ORDER BY date")
	fun getByFeedIdWithDownloads(id: Long): LiveData<List<FeedItemWithDownload>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(items: List<FeedItem>)
}