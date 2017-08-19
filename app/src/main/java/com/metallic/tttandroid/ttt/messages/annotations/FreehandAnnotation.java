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
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

import java.io.DataInputStream;
import java.io.IOException;

import com.metallic.tttandroid.ttt.core.Constants;
import com.metallic.tttandroid.ttt.shapes.MyPath;

/**
 * Additional adjustmennts at contains() methode. The android.graphics.Path
 * doesn't have this methods. The Workauround compute the bounds in the form of
 * a rectangle, and chekcs if the point is inside this rectangle
 * 
 * @see Annotation
 * @author Thomas Krex
 * 
 */
public class FreehandAnnotation extends Annotation {

	private final int color;
	private MyPath myPath;
	private final Path path = new Path();
	private int count;

	// private double scale;

	// empty freehand annotation
	public FreehandAnnotation(int timestamp, int color) {
		this.timestamp = timestamp;
		this.color = color;
	}

	public FreehandAnnotation(int timestamp, DataInputStream in)
			throws IOException {
		this(timestamp, in.readUnsignedByte());

		// read points
		int number = in.readUnsignedShort();
		for (int i = 0; i < number; i++)
			addPoint(in.readUnsignedShort(), in.readUnsignedShort());
	}

	// MODMSG
	/**
	 * constructor for use in parsing messaging xml
	 */
	public FreehandAnnotation(org.w3c.dom.Element xmlNode) {
		this.color = Integer.parseInt(xmlNode.getAttribute("color"));

		// parse point paths
		org.w3c.dom.Element elPath = (org.w3c.dom.Element) xmlNode
				.getElementsByTagName("path").item(0);
		String[] points = elPath.getAttribute("data").split("\\s");
		path.moveTo(Float.parseFloat(points[0]), Float.parseFloat(points[1]));
		for (int i = 2; i < points.length; i += 2) {
			path.lineTo(Float.parseFloat(points[i]),
					Float.parseFloat(points[i + 1]));
		}
		this.count = points.length / 2;

		myPath = new MyPath(path, annotationColors[color], Style.STROKE, 2.5f,
				Cap.ROUND, Join.ROUND);
	}

	// add point to freehand annotation
	public void addPoint(int x, int y) {
		if (count++ == 0)
			path.moveTo(x, y);
		else
			path.lineTo(x, y);

		myPath = new MyPath(path, annotationColors[color], Style.STROKE, 2.5f,
				Cap.ROUND, Join.ROUND);
	}

	@Override
	public Rect getBounds() {
		RectF boundsF = new RectF();
		Rect bounds = new Rect();
		path.computeBounds(boundsF, true);

		boundsF.round(bounds);
		return bounds;

	}

	@Override
	public void paint(Canvas canvas) {
		myPath.draw(canvas);

	}

	// TODO
	@Override
	public boolean contains(int x, int y) {

		return getBounds().contains(x, y);

	}

	@Override
	public int getEncoding() {
		return Constants.AnnotationFreehand;
	}

	@Override
	public String toString() {
		return super.toString() + " [" + count + " points]";
	}

	/*******************************************************************************************************************
	 * write message
	 ******************************************************************************************************************/

	// return size of message in bytes (if written to stream)
	@Override
	public int getSize() {
		return 4 + 4 * count;
	}

}
