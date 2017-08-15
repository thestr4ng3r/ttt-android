package com.metallic.tttandroid.request

import android.sax.RootElement
import android.util.Xml
import com.metallic.tttandroid.model.FeedItem
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.regex.Pattern

data class FeedResponse(val items: List<FeedItem>?)

private val RSS = "rss"
private val CHANNEL = "channel"
private val ITEM = "item"
private val LANGUAGE = "language"
private val DESCRIPTION = "description"
private val LINK = "link"
private val TITLE = "title"
private val ENCLOSURE = "enclosure"
private val LENGTH = "length"

fun Call.enqueueFeed(callback: (FeedResponse) -> Unit)
{
	enqueue(object: Callback {
		override fun onResponse(call: Call, response: Response)
		{
			val responseBody = response.body()
			if(responseBody == null)
			{
				callback(FeedResponse(null))
				return
			}

			// from old ttt app
			var feedItem: FeedItem? = null
			val itemList = mutableListOf<FeedItem>()

			val root = RootElement(RSS)
			val itemlist = root.getChild(CHANNEL)

			val item = itemlist.getChild(ITEM)
			item.setStartElementListener { feedItem = FeedItem() }
			item.setEndElementListener {
				feedItem?.let { feedItem -> itemList.add(feedItem) }
				feedItem = null
			}

			item.getChild(TITLE).setEndTextElementListener { body ->
				feedItem?.title = body
				val pattern = Pattern.compile(".*(\\d{4}_\\d{2}_\\d{2}).*")
				val m = pattern.matcher(body)
				if (m.matches())
					feedItem?.date = m.group(1)
			}


			item.getChild(LINK).setEndTextElementListener { body -> feedItem?.link = body }
			item.getChild(DESCRIPTION).setEndTextElementListener { body -> feedItem?.description = body }

			val enclosure = item.getChild(ENCLOSURE)
			enclosure.setStartElementListener { attributes -> feedItem?.fileSize = attributes.getValue(LENGTH).toLongOrNull() ?: 0 }

			Xml.parse(responseBody.byteStream(), Xml.Encoding.UTF_8, root.contentHandler)

			callback(FeedResponse(itemList))
		}

		override fun onFailure(call: Call, e: IOException)
		{
			callback(FeedResponse(null))
		}
	})
}