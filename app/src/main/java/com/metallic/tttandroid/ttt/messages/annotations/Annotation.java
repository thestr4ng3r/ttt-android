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
import android.graphics.Rect;

import com.metallic.tttandroid.ttt.core.Constants;
import com.metallic.tttandroid.ttt.core.GraphicsContext;
import com.metallic.tttandroid.ttt.messages.Message;

/**
 * All Annotations were adopted from TTT Java-Version. The Colors of the
 * Annotations were converted from RGBA to ARGB. The paint(GraphicsContex
 * context) was not changed. paint(Canvas canvas) replaces paint(Graphics
 * graphics). Drawing Rects and Paths was adjusted to work on Android. The Paint
 * class defines the style,color, strokeWidth, cap and join. To store all
 * information in one class, the myshape class was created.
 * 
 * 
 * @author Thomas Krex
 * 
 */
public abstract class Annotation extends Message {

	// return the outer bounds of the annotation
	abstract public Rect getBounds();

	// add annotation to graphicContext
	@Override
	public void paint(GraphicsContext graphicsContext) {
		graphicsContext.addAnnotation(this);
	}

	// draw annotation to graphics
	abstract public void paint(Canvas canvas);

	// used for showing temporary annotations during painting
	public boolean temporary;

	// special thumbnail drawing if needed (to avoid ugly scaling)
	public void paintToThumbnail(Canvas canvas) {
		paint(canvas);
	}

	abstract public boolean contains(int x, int y);

	@Override
	abstract public int getEncoding();

	@Override
	public String toString() {
		return super.toString() + ": Annotation-"
				+ Constants.encodingToString(getEncoding());
	}

	// MODMSG
	/**
	 * get XML String for messaging
	 */

	/*******************************************************************************************************************
	 * Default Color Table for annotations *
	 ******************************************************************************************************************/

	public final static int White = 0;
	public final static int DarkGray = 4;
	public final static int Gray = 8;
	public final static int LightGray = 12;
	public final static int Black = 16;
	public final static int Orange = 20;
	public final static int Pink = 24;
	public final static int Blue = 28;
	public final static int Red = 32;
	public final static int Green = 36;
	public final static int Magenta = 40;
	public final static int Yellow = 44;
	public final static int Cyan = 48;

	public static int RGBAtoARGB(int r, int g, int b, int a) {
		return Color.argb(a, r, g, b);

	}

	static public int[] annotationColors = {
			RGBAtoARGB(255, 255, 255, 255), // white
			RGBAtoARGB(255, 255, 255, 192),
			RGBAtoARGB(255, 255, 255, 128),
			RGBAtoARGB(255, 255, 255, 64),
			RGBAtoARGB(64, 64, 64, 255),
			// darkGray
			RGBAtoARGB(64, 64, 64, 192),
			RGBAtoARGB(64, 64, 64, 128),
			RGBAtoARGB(64, 64, 64, 64),
			RGBAtoARGB(128, 128, 128, 255),
			// gray
			RGBAtoARGB(128, 128, 128, 192),
			RGBAtoARGB(128, 128, 128, 128),
			RGBAtoARGB(128, 128, 128, 64),
			RGBAtoARGB(192, 192, 192, 255),
			// lightGray
			RGBAtoARGB(192, 192, 192, 192),
			RGBAtoARGB(192, 192, 192, 128),
			RGBAtoARGB(192, 192, 192, 64),
			RGBAtoARGB(0, 0, 0, 255),
			// black
			RGBAtoARGB(0, 0, 0, 192),
			RGBAtoARGB(0, 0, 0, 128),
			RGBAtoARGB(0, 0, 0, 64),
			RGBAtoARGB(255, 200, 0, 255),
			// orange
			RGBAtoARGB(255, 200, 0, 192),
			RGBAtoARGB(255, 200, 0, 128),
			RGBAtoARGB(255, 200, 0, 64),
			RGBAtoARGB(255, 175, 175, 255),
			// pink
			RGBAtoARGB(255, 175, 175, 192),
			RGBAtoARGB(255, 175, 175, 128),
			RGBAtoARGB(255, 175, 175, 64),
			RGBAtoARGB(0, 0, 255, 255),
			// blue
			RGBAtoARGB(0, 0, 255, 192),
			RGBAtoARGB(0, 0, 255, 128),
			RGBAtoARGB(0, 0, 255, 64),
			RGBAtoARGB(255, 0, 0, 255),
			// red
			RGBAtoARGB(255, 0, 0, 192),
			RGBAtoARGB(255, 0, 0, 128),
			RGBAtoARGB(255, 0, 0, 64),
			RGBAtoARGB(0, 255, 0, 255),
			// green
			RGBAtoARGB(0, 255, 0, 192),
			RGBAtoARGB(0, 255, 0, 128),
			RGBAtoARGB(0, 255, 0, 64),
			RGBAtoARGB(255, 0, 255, 255),
			// magenta
			RGBAtoARGB(255, 0, 255, 192),
			RGBAtoARGB(255, 0, 255, 128),
			RGBAtoARGB(255, 0, 255, 64),
			RGBAtoARGB(255, 255, 0, 255),
			// yellow
			RGBAtoARGB(255, 255, 0, 192),
			RGBAtoARGB(255, 255, 0, 128),
			RGBAtoARGB(255, 255, 0, 64),
			RGBAtoARGB(0, 255, 255, 255),
			// cyan
			RGBAtoARGB(0, 255, 255, 192),
			RGBAtoARGB(0, 255, 255, 128),
			RGBAtoARGB(0, 255, 255, 64),
			RGBAtoARGB(0, 0, 153, 255),
			// dark blue
			RGBAtoARGB(0, 0, 153, 192),
			RGBAtoARGB(0, 0, 153, 128),
			RGBAtoARGB(0, 0, 153, 64),
			RGBAtoARGB(102, 102, 255, 255),
			// light blue
			RGBAtoARGB(102, 102, 255, 192),
			RGBAtoARGB(102, 102, 255, 128),
			RGBAtoARGB(102, 102, 255, 64),
			RGBAtoARGB(204, 204, 255, 255),
			// very light blue
			RGBAtoARGB(204, 204, 255, 192),
			RGBAtoARGB(204, 204, 255, 128),
			RGBAtoARGB(204, 204, 255, 64),
			RGBAtoARGB(255, 102, 102, 255),
			// light red
			RGBAtoARGB(255, 102, 102, 192),
			RGBAtoARGB(255, 102, 102, 128),
			RGBAtoARGB(255, 102, 102, 64),
			RGBAtoARGB(255, 204, 204, 255),
			// very light red
			RGBAtoARGB(255, 204, 204, 192),
			RGBAtoARGB(255, 204, 204, 128),
			RGBAtoARGB(255, 204, 204, 64),
			RGBAtoARGB(0, 102, 0, 255),
			// dark green
			RGBAtoARGB(0, 102, 0, 192),
			RGBAtoARGB(0, 102, 0, 128),
			RGBAtoARGB(0, 102, 0, 64),
			RGBAtoARGB(102, 255, 102, 255),
			// light green
			RGBAtoARGB(102, 255, 102, 192),
			RGBAtoARGB(102, 255, 102, 128),
			RGBAtoARGB(102, 255, 102, 64),
			RGBAtoARGB(204, 255, 204, 255),
			// very light green
			RGBAtoARGB(204, 255, 204, 192),
			RGBAtoARGB(204, 255, 204, 128),
			RGBAtoARGB(204, 255, 204, 64),
			RGBAtoARGB(102, 0, 102, 255),
			// dark rose
			RGBAtoARGB(102, 0, 102, 192),
			RGBAtoARGB(102, 0, 102, 128),
			RGBAtoARGB(102, 0, 102, 64),
			RGBAtoARGB(255, 0, 255, 255),
			// rose
			RGBAtoARGB(255, 0, 255, 192),
			RGBAtoARGB(255, 0, 255, 128),
			RGBAtoARGB(255, 0, 255, 64),
			RGBAtoARGB(255, 102, 255, 255),
			// light rose
			RGBAtoARGB(255, 102, 255, 192),
			RGBAtoARGB(255, 102, 255, 128),
			RGBAtoARGB(255, 102, 255, 64),
			RGBAtoARGB(255, 204, 255, 255),
			// very light rose
			RGBAtoARGB(255, 204, 255, 192),
			RGBAtoARGB(255, 204, 255, 128),
			RGBAtoARGB(255, 204, 255, 64),
			RGBAtoARGB(102, 102, 0, 255),
			// dark yellow
			RGBAtoARGB(102, 102, 0, 192),
			RGBAtoARGB(102, 102, 0, 128),
			RGBAtoARGB(102, 102, 0, 64),
			RGBAtoARGB(255, 255, 102, 255),
			// light yellow
			RGBAtoARGB(255, 255, 102, 192),
			RGBAtoARGB(255, 255, 102, 128),
			RGBAtoARGB(255, 255, 102, 64),
			RGBAtoARGB(255, 255, 204, 255),
			// very light yellow
			RGBAtoARGB(255, 255, 204, 192),
			RGBAtoARGB(255, 255, 204, 128),
			RGBAtoARGB(255, 255, 204, 64),
			RGBAtoARGB(0, 0, 102, 255),
			// dark turquoise
			RGBAtoARGB(0, 0, 102, 192),
			RGBAtoARGB(0, 0, 102, 128),
			RGBAtoARGB(0, 0, 102, 64),
			RGBAtoARGB(102, 255, 255, 255),
			// light turquoise
			RGBAtoARGB(102, 255, 255, 192),
			RGBAtoARGB(102, 255, 255, 128),
			RGBAtoARGB(102, 255, 255, 64),
			RGBAtoARGB(204, 255, 255, 255),
			// very light turquoise
			RGBAtoARGB(204, 255, 255, 192),
			RGBAtoARGB(204, 255, 255, 128),
			RGBAtoARGB(204, 255, 255, 64),
			RGBAtoARGB(153, 0, 255, 255),
			// violet
			RGBAtoARGB(153, 0, 255, 192),
			RGBAtoARGB(153, 0, 255, 128),
			RGBAtoARGB(153, 0, 255, 64),
			RGBAtoARGB(102, 0, 153, 255),
			// dark violet
			RGBAtoARGB(102, 0, 153, 192),
			RGBAtoARGB(102, 0, 153, 128),
			RGBAtoARGB(102, 0, 153, 64),
			RGBAtoARGB(153, 102, 255, 255),
			// blueish light violet
			RGBAtoARGB(153, 102, 255, 192),
			RGBAtoARGB(153, 102, 255, 128),
			RGBAtoARGB(153, 102, 255, 64),
			RGBAtoARGB(204, 102, 255, 255),
			// redish light violet
			RGBAtoARGB(204, 102, 255, 192),
			RGBAtoARGB(204, 102, 255, 128),
			RGBAtoARGB(204, 102, 255, 64),
			RGBAtoARGB(204, 102, 0, 255),
			// light brown
			RGBAtoARGB(204, 102, 0, 192),
			RGBAtoARGB(204, 102, 0, 128),
			RGBAtoARGB(204, 102, 0, 64),
			RGBAtoARGB(255, 102, 51, 255),
			// dark orange
			RGBAtoARGB(255, 102, 51, 192),
			RGBAtoARGB(255, 102, 51, 128),
			RGBAtoARGB(255, 102, 51, 64),
			RGBAtoARGB(255, 204, 153, 255),
			// light orange
			RGBAtoARGB(255, 204, 153, 192),
			RGBAtoARGB(255, 204, 153, 128),
			RGBAtoARGB(255, 204, 153, 64),
			RGBAtoARGB(255, 215, 0, 255),
			// gold
			RGBAtoARGB(255, 215, 0, 192),
			RGBAtoARGB(255, 215, 0, 128),
			RGBAtoARGB(255, 215, 0, 64),
			RGBAtoARGB(240, 230, 140, 255),
			// khaki
			RGBAtoARGB(240, 230, 140, 192),
			RGBAtoARGB(240, 230, 140, 128),
			RGBAtoARGB(240, 230, 140, 64),
			RGBAtoARGB(218, 165, 32, 255),
			// goldenrod
			RGBAtoARGB(218, 165, 32, 192),
			RGBAtoARGB(218, 165, 32, 128),
			RGBAtoARGB(218, 165, 32, 64),
			RGBAtoARGB(245, 245, 220, 255),
			// beige
			RGBAtoARGB(245, 245, 220, 192),
			RGBAtoARGB(245, 245, 220, 128),
			RGBAtoARGB(245, 245, 220, 64),
			RGBAtoARGB(255, 228, 181, 255),
			// moccasin
			RGBAtoARGB(255, 228, 181, 192),
			RGBAtoARGB(255, 228, 181, 128),
			RGBAtoARGB(255, 228, 181, 64),
			RGBAtoARGB(255, 99, 71, 255),
			// tomato
			RGBAtoARGB(255, 99, 71, 192),
			RGBAtoARGB(255, 99, 71, 128),
			RGBAtoARGB(255, 99, 71, 64),
			RGBAtoARGB(255, 140, 0, 255),
			// darkorange
			RGBAtoARGB(255, 140, 0, 192),
			RGBAtoARGB(255, 140, 0, 128),
			RGBAtoARGB(255, 140, 0, 64),
			RGBAtoARGB(220, 20, 60, 255),
			// crimson
			RGBAtoARGB(220, 20, 60, 192),
			RGBAtoARGB(220, 20, 60, 128),
			RGBAtoARGB(220, 20, 60, 64),
			RGBAtoARGB(70, 130, 180, 255),
			// steelblue
			RGBAtoARGB(70, 130, 180, 192),
			RGBAtoARGB(70, 130, 180, 128),
			RGBAtoARGB(70, 130, 180, 64),
			RGBAtoARGB(65, 105, 225, 255),
			// royalblue
			RGBAtoARGB(65, 105, 225, 192),
			RGBAtoARGB(65, 105, 225, 128),
			RGBAtoARGB(65, 105, 225, 64),
			RGBAtoARGB(123, 104, 238, 255),
			// medslateblue
			RGBAtoARGB(123, 104, 238, 192),
			RGBAtoARGB(123, 104, 238, 128),
			RGBAtoARGB(123, 104, 238, 64),
			RGBAtoARGB(127, 255, 212, 255),
			// aquamarine
			RGBAtoARGB(127, 255, 212, 192),
			RGBAtoARGB(127, 255, 212, 128),
			RGBAtoARGB(127, 255, 212, 64),
			RGBAtoARGB(0, 255, 127, 255),
			// springgreen
			RGBAtoARGB(0, 255, 127, 192),
			RGBAtoARGB(0, 255, 127, 128),
			RGBAtoARGB(0, 255, 127, 64),
			RGBAtoARGB(150, 205, 50, 255),
			// yellowgreen
			RGBAtoARGB(150, 205, 50, 192),
			RGBAtoARGB(150, 205, 50, 128),
			RGBAtoARGB(150, 205, 50, 64),
			RGBAtoARGB(216, 191, 216, 255),
			// thistle
			RGBAtoARGB(216, 191, 216, 192),
			RGBAtoARGB(216, 191, 216, 128),
			RGBAtoARGB(216, 191, 216, 64),
			RGBAtoARGB(245, 222, 179, 255),
			// wheat
			RGBAtoARGB(245, 222, 179, 192),
			RGBAtoARGB(245, 222, 179, 128),
			RGBAtoARGB(245, 222, 179, 64),
			RGBAtoARGB(160, 82, 45, 255),
			// siena
			RGBAtoARGB(160, 82, 45, 192),
			RGBAtoARGB(160, 82, 45, 128),
			RGBAtoARGB(160, 82, 45, 64),
			RGBAtoARGB(233, 150, 122, 255),
			// darksalmon
			RGBAtoARGB(233, 150, 122, 192),
			RGBAtoARGB(233, 150, 122, 128),
			RGBAtoARGB(233, 150, 122, 64),
			RGBAtoARGB(165, 42, 42, 255),
			// brown
			RGBAtoARGB(165, 42, 42, 192),
			RGBAtoARGB(165, 42, 42, 128),
			RGBAtoARGB(165, 42, 42, 64),
			RGBAtoARGB(210, 105, 30, 255),
			// chocolate
			RGBAtoARGB(210, 105, 30, 192),
			RGBAtoARGB(210, 105, 30, 128),
			RGBAtoARGB(210, 105, 30, 64),
			RGBAtoARGB(244, 164, 96, 255),
			// sandybrown
			RGBAtoARGB(244, 164, 96, 192),
			RGBAtoARGB(244, 164, 96, 128),
			RGBAtoARGB(244, 164, 96, 64),
			RGBAtoARGB(255, 20, 147, 255),
			// deeppink
			RGBAtoARGB(255, 20, 147, 192),
			RGBAtoARGB(255, 20, 147, 128),
			RGBAtoARGB(255, 20, 147, 64),
			RGBAtoARGB(255, 105, 180, 255),
			// hotpink
			RGBAtoARGB(255, 105, 180, 192),
			RGBAtoARGB(255, 105, 180, 128),
			RGBAtoARGB(255, 105, 180, 64),
			RGBAtoARGB(221, 160, 221, 255),
			// plum
			RGBAtoARGB(221, 160, 221, 192), RGBAtoARGB(221, 160, 221, 128),
			RGBAtoARGB(221, 160, 221, 64),
			RGBAtoARGB(186, 85, 211, 255),
			// medorchid
			RGBAtoARGB(186, 85, 211, 192), RGBAtoARGB(186, 85, 211, 128),
			RGBAtoARGB(186, 85, 211, 64), RGBAtoARGB(112, 128, 144, 255),
			// slategray
			RGBAtoARGB(112, 128, 144, 192), RGBAtoARGB(112, 128, 144, 128),
			RGBAtoARGB(112, 128, 144, 64) };
}
