// TeleTeachingTool - Presentation Recording With Automated Indexing
//
// Copyright (C) 2003-2008 Peter Ziewer - Technische Universit?t M?nchen
// 
//    This file is part of TeleTeachingTool.
//
//    TeleTeachingTool is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    TeleTeachingTool is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with TeleTeachingTool.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Created on 13.01.2006
 *
 * Author: Peter Ziewer, TU Munich, Germany - ziewer@in.tum.de
 */
package com.metallic.tttandroid.ttt.messages.annotations;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.io.DataInputStream;
import java.io.IOException;

import com.metallic.tttandroid.ttt.core.Constants;
import com.metallic.tttandroid.ttt.core.IndexEntry;

/**
 * @see Annotation
 * @author Thomas Krex
 * 
 */
public class HighlightAnnotation extends SimpleAnnotation {

	private Rect rect;

	public HighlightAnnotation(int timestamp, int color, int startx,
			int starty, int endx, int endy) {
		super(timestamp, color, startx, starty, endx, endy);
	}

	public HighlightAnnotation(int timestamp, DataInputStream in)
			throws IOException {
		this(timestamp, in.readUnsignedByte(), in.readUnsignedShort(), in
				.readUnsignedShort(), in.readUnsignedShort(), in
				.readUnsignedShort());
	}

	public HighlightAnnotation(org.w3c.dom.Element xmlNode) {
		super(xmlNode);
	}

	@Override
	public int getEncoding() {
		return Constants.AnnotationHighlight;
	}

	@Override
	void computeShape() {
		// calculate x,y,width,height from startx/y and endx/y
		int x, y, width, height;

		// x,y koordinates are not ordered
		if (startx < endx) {
			if (starty < endy) {
				x = startx;
				y = starty;
				width = endx - startx;
				height = endy - starty;
			} else {
				x = startx;
				y = endy;
				width = endx - startx;
				height = starty - endy;
			}
		} else {
			if (starty < endy) {
				x = endx;
				y = starty;
				width = startx - endx;
				height = endy - starty;
			} else {
				x = endx;
				y = endy;
				width = startx - endx;
				height = starty - endy;
			}
		}

		rect = new Rect(x, y, x + width, y + height);
	}

	@Override
	public void paintToThumbnail(Canvas canvas) {

		int highlightColor = annotationColors[color];

		// create canvas to draw on Bitmap thumbnail

		// highlight area
		Paint highlightPaint = new Paint();
		highlightPaint.setColor(highlightColor);
		highlightPaint.setStrokeWidth(1);
		highlightPaint.setStyle(Paint.Style.FILL);

		// scale Rect for thumbnail
		RectF scaledBounds = new RectF(
				(float) (rect.left * IndexEntry.THUMBNAIL_SCALE_FACTOR),
				(float) (rect.top * IndexEntry.THUMBNAIL_SCALE_FACTOR),
				(float) (rect.right * IndexEntry.THUMBNAIL_SCALE_FACTOR),
				(float) (rect.left * IndexEntry.THUMBNAIL_SCALE_FACTOR));

		canvas.drawRect(scaledBounds, highlightPaint);

		// border highlighted area

		int borderColor = Color.argb(128, Color.red(highlightColor),
				Color.green(highlightColor), Color.blue(highlightColor));

		Paint borderPaint = new Paint();
		borderPaint.setStrokeWidth(3);
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setColor(borderColor);
		canvas.drawRect(getBounds(), borderPaint);
	}

	@Override
	public void paint(Canvas canvas) {

		int highlightColor = annotationColors[color];

		// highlight area
		Paint highlightPaint = new Paint();
		highlightPaint.setColor(highlightColor);
		highlightPaint.setStrokeWidth(1);
		highlightPaint.setStyle(Paint.Style.FILL);

		canvas.drawRect(rect, highlightPaint);

		// border highlighted area

		int borderColor = Color.argb(128, Color.red(highlightColor),
				Color.green(highlightColor), Color.blue(highlightColor));

		Paint borderPaint = new Paint();
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setColor(borderColor);
		canvas.drawRect(rect, borderPaint);
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}
}
