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
import android.graphics.Rect;

import com.metallic.tttandroid.ttt.core.Constants;
import com.metallic.tttandroid.ttt.core.GraphicsContext;

public class DeleteAllAnnotation extends Annotation {

	public DeleteAllAnnotation(int timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public void paint(GraphicsContext graphicsContext) {
		// TODO test
		graphicsContext.clearAnnotations();
		// graphicsContext.updateCanvas();
	}

	@Override
	public void paint(Canvas canvas) {
		// should never be called for DeleteAll
	}

	public void paint(Canvas canvas, double scale) {
		paint(canvas);
	}

	@Override
	public Rect getBounds() {
		return null;
	}

	@Override
	public boolean contains(int x, int y) {
		return false;
	}

	@Override
	public int getEncoding() {
		return Constants.AnnotationDeleteAll;
	}

	/*******************************************************************************************************************
	 * write message
	 ******************************************************************************************************************/

	// write message to TTT output stream

	// return size of message in bytes (if written to stream)
	@Override
	public int getSize() {
		return 1;
	}

}
