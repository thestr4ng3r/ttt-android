package com.metallic.tttandroid.utils

import android.util.Log

fun Any.log(message: String)
{
	Log.i((javaClass.enclosingClass ?: javaClass).name, message)
}

fun Any.logWarning(message: String)
{
	Log.w((javaClass.enclosingClass ?: javaClass).name, message)
}

fun Any.logError(message: String)
{
	Log.e((javaClass.enclosingClass ?: javaClass).name, message)
}