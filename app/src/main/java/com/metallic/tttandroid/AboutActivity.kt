package com.metallic.tttandroid

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_about.*
import java.io.BufferedReader
import java.io.InputStreamReader

class AboutActivity: AppCompatActivity()
{
	@RequiresApi(Build.VERSION_CODES.N)
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_about)

		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		title = getString(R.string.activity_about_title)

		val reader = BufferedReader(InputStreamReader(assets.open("about.html")))
		val html = reader.readText()
		reader.close()

		text_view.movementMethod = LinkMovementMethod.getInstance()
		text_view.text = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean
	{
		when(item.itemId)
		{
			android.R.id.home ->
			{
				finish()
				return true
			}
		}

		return super.onOptionsItemSelected(item)
	}
}