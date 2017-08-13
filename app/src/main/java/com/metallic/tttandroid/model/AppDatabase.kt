package com.metallic.tttandroid.model

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = arrayOf(Feed::class), version = 1)
abstract class AppDatabase: RoomDatabase()
{
	abstract fun feedDao(): FeedDao

	companion object
	{
		private var instance: AppDatabase? = null
		fun getInstance(context: Context): AppDatabase
		{
			val instance = instance
			if(instance == null)
			{
				val newInstance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app-database")
						.allowMainThreadQueries()
						.build()
				this.instance = newInstance
				return newInstance
			}

			return instance
		}
	}
}