package com.metallic.tttandroid.model

import android.arch.persistence.room.*
import android.content.Context

@Database(entities = arrayOf(Feed::class), version = 2)
@TypeConverters(AppDatabase.Converters::class)
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


	class Converters
	{
		@TypeConverter
		fun startDateFromTimestamp(value: Int): Feed.StartDate
		{
			return Feed.StartDate(value shr 9,
					(value shr 5) and 0b1111,
					value and 0b11111)
		}

		@TypeConverter
		fun timestampFromStartDate(value: Feed.StartDate): Int
		{
			return value.day +
					(value.month shl 5) +
					(value.year shl 9)
		}
	}
}