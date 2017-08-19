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
 * Created on 12.12.2005
 *
 * Author: Peter Ziewer, TU Munich, Germany - ziewer@in.tum.de
 */
package com.metallic.tttandroid.ttt.messages;

import java.io.DataInputStream;
import java.io.IOException;

import com.metallic.tttandroid.ttt.core.Constants;
import com.metallic.tttandroid.ttt.core.GraphicsContext;

public class CopyRectMessage extends FramebufferUpdateMessage {

	protected int sourceX;
	protected int sourceY;

	public CopyRectMessage(int timestamp, int destinationX, int destinationY,
			int width, int height, GraphicsContext graphicsContext,
			DataInputStream in) throws IOException {
		this(timestamp, destinationX, destinationY, width, height, in
				.readUnsignedShort(), in.readUnsignedShort());
		paint(graphicsContext);
	}

	public CopyRectMessage(int timestamp, int destinationX, int destinationY,
			int width, int height, int sourceX, int sourceY) {
		super(timestamp, destinationX, destinationY, width, height);
		this.sourceX = sourceX;
		this.sourceY = sourceY;
	}

	@Override
	public int getEncoding() {
		return Constants.EncodingCopyRect;
	}

	@Override
	public void paint(GraphicsContext graphicsContext) {

//		graphicsContext.handleUpdatedPixels(x, y, width, height);
	}

	@Override
	public String toString() {
		return super.toString() + " from (" + sourceX + "," + sourceY + ")";
	}

	/*******************************************************************************************************************
	 * write message
	 ******************************************************************************************************************/

	// write message to TTT output stream

	// return size of message in bytes (if written to stream)
	@Override
	public int getSize() {
		return 9;
	}
}
