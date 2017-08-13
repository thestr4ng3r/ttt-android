package com.metallic.tttandroid.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

@Entity
class Feed(@PrimaryKey val id: Int?,
		   val name: String,
		   val url: String) //: Parcelable
{
	/*constructor(name: String, url: String)
	{
		this.name = name
		this.url = url
	}

	constructor(id: Int, name: String, url: String)
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

