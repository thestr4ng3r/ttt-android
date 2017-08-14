package com.metallic.tttandroid.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import java.util.*

@Entity
class Feed //: Parcelable
{
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	var id: Int = 0

	@ColumnInfo(name = "name")
	var name: String = ""
		set(value)
		{
			field = value.trim()
		}

	@ColumnInfo(name = "url")
	var feedUrl: String = ""
		set(value)
		{
			field = value.trim()
		}

	@ColumnInfo(name = "start_date")
	var startDate: StartDate = StartDate(0, 0, 0)


	val fullUri: Uri?
		get()
		{
			val feedUrl = feedUrl

			if(feedUrl.isEmpty())
				return null

			val startDateString = "%4d_%2d_%2d".format(startDate.year, startDate.month, startDate.day)

			val feedUri = Uri.parse(feedUrl)

			if(!feedUri.isAbsolute)
				return null

			return feedUri.buildUpon()
				.appendQueryParameter("begindate", startDateString)
				.build()
		}


	data class StartDate(val year: Int, val month: Int, val day: Int)
	{
		val calendar: GregorianCalendar get() = GregorianCalendar(year, month, day)

		companion object
		{
			val currentDefault: StartDate get()
			{
				// from old TTT app:
				// determine the current semester return its beginning
				val c = Calendar.getInstance()
				val currMonth = c.get(Calendar.MONTH)
				val year: Int
				val month: Int

				// winter semester
				if (currMonth >= Calendar.OCTOBER || currMonth < Calendar.APRIL)
				{
					month = Calendar.OCTOBER
					if (currMonth >= Calendar.OCTOBER)
						year = c.get(Calendar.YEAR)
					else
						year = c.get(Calendar.YEAR) - 1

				}
				else // summer semester
				{
					year = c.get(Calendar.YEAR)
					month = Calendar.APRIL
				}

				return StartDate(year, month, 1)
			}
		}
	}



	/*constructor(id: Int, name: String, url: String)
	{
		this.id = id
		this.name = name
		this.url = url
	}

	constructor(`in`: Parcel)
	{
		this.id = `in`.readInt()
		this.name = `in`.readString()
		this.url = `in`.readString()
	}*/

	/*override fun describeContents(): Int
	{
		return 0
	}

	override fun writeToParcel(arg0: Parcel, arg1: Int)
	{
		arg0.writeInt(id)
		arg0.writeString(name)
		arg0.writeString(url)

	}

	companion object
	{

		val CREATOR: Parcelable.Creator<*> = object : Parcelable.Creator
		{
			override fun createFromParcel(`in`: Parcel): Feed
			{
				return Feed(`in`)
			}

			override fun newArray(arg0: Int): Array<Feed>
			{
				return arrayOfNulls(arg0)
			}

		}
	}*/


}

