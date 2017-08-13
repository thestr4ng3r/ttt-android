package com.metallic.tttandroid.model

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface FeedDao
{
	@Query("SELECT * FROM feed")
	fun getAll(): List<Feed>

	@Insert
	fun insert(feed: Feed)

	@Delete
	fun delete(feed: Feed)
}