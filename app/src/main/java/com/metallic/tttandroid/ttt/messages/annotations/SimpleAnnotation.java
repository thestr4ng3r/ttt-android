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
 * Created on 31.01.2007
 *
 * Author: Peter Ziewer, TU Munich, Germany - ziewer@in.tum.de
 */
package com.metallic.tttandroid.ttt.messages.annotations;

import android.graphics.Point;
import android.graphics.Rect;

import com.metallic.tttandroid.ttt.shapes.MyShape;

/**
 * @see Annotation
 * @author Thomas Krex
 * 
 */
public abstract class SimpleAnnotation extends Annotation {
	int color;

	// coordinates
	int startx;
	int starty;
	int endx;
	int endy;

	MyShape shape, thumbShape;

	SimpleAnnotation(int timestamp, int color, int startx, int starty,
			int endx, int endy) {
		this.timestamp = timestamp;
		this.color = color;
		this.startx = startx;
		this.starty = starty;
		this.endx = endx;
		this.endy = endy;

		computeShape();
	}

	// MODMSG
	/**
	 * constructor for use in parsing messaging xml
	 */
	SimpleAnnotation(org.w3c.dom.Element xmlNode) {
		this.color = Integer.parseInt(xmlNode.getAttribute("color"));
		this.startx = Integer.parseInt(xmlNode.getAttribute("startx"));
		this.starty = Integer.parseInt(xmlNode.getAttribute("starty"));
		this.endx = Integer.parseInt(xmlNode.getAttribute("endx"));
		this.endy = Integer.parseInt(xmlNode.getAttribute("endy"));
		computeShape();
	}

	abstract void computeShape();

	@Override
	public Rect getBounds() {
		Rect bounds = new Rect(startx, starty, startx + endx, starty + endy);
		return bounds;
	}

	// modified for android
	@Override
	public boolean contains(int x, int y) {
		boolean contains = x > startx && x < endx && y > starty && y < endy;
		return contains;
	}

	/*******************************************************************************************************************
	 * getter/setter needed for painting
	 ******************************************************************************************************************/

	public void setEndPoint(int x, int y) {
		endx = x;
		endy = y;
		computeShape();
	}

	public Point getStartPoint() {
		return new Point(startx, starty);
	}

}
