package com.metallic.tttandroid.model

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

private const val tableName = "feed"

@Dao
interface FeedDao
{
	@Query("SELECT * FROM $tableName ORDER BY name")
	fun getAll(): LiveData<List<Feed>>

	@Query("SELECT * FROM $tableName WHERE id = :arg0")
	fun getById(id: Long): Feed

	@Insert
	fun insert(feed: Feed)

	@Update
	fun update(feed: Feed)

	@Delete
	fun delete(feed: Feed)
}