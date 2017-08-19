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
import android.graphics.Paint.Style;

import java.io.DataInputStream;
import java.io.IOException;

import com.metallic.tttandroid.ttt.core.Constants;
import com.metallic.tttandroid.ttt.shapes.MyLine;

/**
 * 
 * @see Annotation
 * @author Thomas Krex
 * 
 */
public class LineAnnotation extends SimpleAnnotation {

	public LineAnnotation(int timestamp, int color, int startx, int starty,
			int endx, int endy) {
		super(timestamp, color, startx, starty, endx, endy);
	}

	public LineAnnotation(int timestamp, DataInputStream in) throws IOException {
		this(timestamp, in.readUnsignedByte(), in.readUnsignedShort(), in
				.readUnsignedShort(), in.readUnsignedShort(), in
				.readUnsignedShort());
	}

	// MODMSG
	/**
	 * create LineAnnotation of corresponding XML-Element (used by messaging)
	 */
	public LineAnnotation(org.w3c.dom.Element xmlNode) {
		super(xmlNode);
	}

	@Override
	public int getEncoding() {
		return Constants.AnnotationLine;
	}

	@Override
	void computeShape() {

		shape = new MyLine(startx, starty, endx, endy, color, Style.FILL, 2.5f);
	}

	@Override
	public void paint(Canvas canvas) {
		shape.draw(canvas);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString() + " [(" + startx + "," + starty + ") to ("
				+ endx + "," + endy + ")]";
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

}
