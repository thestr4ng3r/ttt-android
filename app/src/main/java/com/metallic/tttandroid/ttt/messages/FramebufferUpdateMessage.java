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

import android.graphics.Rect;

import java.io.DataInputStream;
import java.io.IOException;

import com.metallic.tttandroid.ttt.core.Constants;

/**
 * adopted from TTT, see above adjustments at the getBounds()-methode:
 * Rectangle2D-> Rect
 * 
 * @author Thomas Krex
 * 
 */
public abstract class FramebufferUpdateMessage extends Message {
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected byte[] data;

	public FramebufferUpdateMessage(int timestamp, int x, int y, int width,
			int height) {
		this.timestamp = timestamp;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	abstract public int getEncoding();

	// TODO: not that important - can be reduced to getBounds()
	public int getCoveredArea() {
		return width * height;
	}

	// was changed due to Rect format
	public Rect getBounds() {
		return new Rect(x, y, x + width, y + height);
	}

	@Override
	public String toString() {
		return super.toString() + "\tEncoding: "
				+ Constants.encodingToString(getEncoding()) + " - " + width
				+ " x " + height + " at (" + x + "," + y + ")";
	}

	// helper method to read byte array of given size
	static byte[] readBytes(DataInputStream in, int size) throws IOException {
		byte[] bytes = new byte[size];
		in.readFully(bytes);
		return bytes;
	}

	/*******************************************************************************************************************
	 * write message
	 ******************************************************************************************************************/

	// write message to TTT output stream

	// return size of message in bytes (if written to stream)
	@Override
	public int getSize() {
		return (data != null ? data.length : 0) + 9;
	}
}
