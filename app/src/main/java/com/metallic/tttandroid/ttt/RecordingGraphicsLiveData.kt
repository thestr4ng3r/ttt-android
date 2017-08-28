package com.metallic.tttandroid.ttt

import android.arch.lifecycle.LiveData
import android.graphics.Bitmap
import com.metallic.tttandroid.ttt.core.GraphicsContext

class RecordingGraphicsLiveData(val graphicsContext: GraphicsContext): LiveData<Bitmap>()
{
	init
	{
		value = graphicsContext.bitmap
	}

	val graphicsContextListener = GraphicsContext.Listener { bitmap ->
		postValue(bitmap)
	}

	override fun onActive()
	{
		graphicsContext.addListener(graphicsContextListener)
	}

	override fun onInactive()
	{
		graphicsContext.removeListener(graphicsContextListener)
	}
}