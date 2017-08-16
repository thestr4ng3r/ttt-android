package com.metallic.tttandroid.request

import android.sax.RootElement
import android.util.Xml
import com.metallic.tttandroid.model.FeedItem
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
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
private val PUBDATE = "pubDate"

private val dateFormat = SimpleDateFormat("dd. MMM. yyyy", Locale.ENGLISH)

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

			// partially from old ttt app
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
			}

			item.getChild(PUBDATE).setEndTextElementListener { body ->
				feedItem?.date = try { dateFormat.parse(body) } catch (e: ParseException) { Date() }
			}

			item.getChild(LINK).setEndTextElementListener { body -> feedItem?.link = body }
			item.getChild(DESCRIPTION).setEndTextElementListener { body -> feedItem?.description = body }

			item.getChild(ENCLOSURE).setStartElementListener { attributes ->
				feedItem?.fileSize = attributes.getValue(LENGTH).toLongOrNull() ?: 0
			}

			Xml.parse(responseBody.byteStream(), Xml.Encoding.UTF_8, root.contentHandler)

			callback(FeedResponse(itemList))
		}

		override fun onFailure(call: Call, e: IOException)
		{
			callback(FeedResponse(null))
		}
	})
}