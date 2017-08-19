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
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Rect;

import java.io.DataInputStream;
import java.io.IOException;

import com.metallic.tttandroid.ttt.core.Constants;
import com.metallic.tttandroid.ttt.shapes.MyRect;

/**
 * Rectangle Annotations
 * 
 * @author Thomas Krex
 * 
 */
public class RectangleAnnotation extends SimpleAnnotation {

	public RectangleAnnotation(int timestamp, int color, int startx,
			int starty, int endx, int endy) {
		super(timestamp, color, startx, starty, endx, endy);
	}

	public RectangleAnnotation(int timestamp, DataInputStream in)
			throws IOException {
		this(timestamp, in.readUnsignedByte(), in.readUnsignedShort(), in
				.readUnsignedShort(), in.readUnsignedShort(), in
				.readUnsignedShort());
	}

	// MOD TD
	/**
	 * creates RectangleAnnotation from corresponding XML Element. (used by
	 * messaging)
	 */
	public RectangleAnnotation(org.w3c.dom.Element xmlNode) {
		super(xmlNode);
	}

	@Override
	public int getEncoding() {
		return Constants.AnnotationRectangle;
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

		// create shape
		shape = new MyRect(new Rect(x, y, x + width, y + height),
				annotationColors[color], Style.STROKE, 2.5f, Cap.ROUND,
				Join.ROUND);
		thumbShape = new MyRect(new Rect(x, y, x + width, y + height),
				annotationColors[color], Style.STROKE, 6f, Cap.ROUND,
				Join.ROUND);

	}

	@Override
	public void paintToThumbnail(Canvas canvas) {
		thumbShape.draw(canvas);

	}

	@Override
	public void paint(Canvas canvas) {
		shape.draw(canvas);
	}

	@Override
	public String toString() {
		Rect bounds = getBounds();
		return super.toString()
				+ (bounds != null ? "  [" + bounds.width() + " x "
						+ bounds.height() + " at (" + bounds.left + ","
						+ bounds.top + ")]" : "");
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

}
