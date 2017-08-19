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
 * Created on 13.03.2006
 *
 * Author: Peter Ziewer, TU Munich, Germany - ziewer@in.tum.de
 */
package com.metallic.tttandroid.ttt.messages;

import com.metallic.tttandroid.ttt.core.Constants;
import com.metallic.tttandroid.ttt.core.GraphicsContext;
import com.metallic.tttandroid.ttt.core.ProtocolPreferences;

/**
 * Adopted by TTT Java-Version
 * 
 * @author Thomas Krex
 * 
 */
// must extend FramebufferUpdate or index generation will not detect Whiteboard
// pages
public class WhiteboardMessage extends FramebufferUpdateMessage {

	// page number
	// > 0: whiteboard enabled
	// <=0: desktop enabled
	// NOTE: only one desktop is support by now
	int pageNumber;

	public WhiteboardMessage(int timestamp, int pageNumber,
			ProtocolPreferences prefs) {
		super(timestamp, 0, 0, prefs.framebufferWidth, prefs.framebufferHeight);
		this.pageNumber = pageNumber;
	}

	@Override
	public int getEncoding() {
		return Constants.EncodingWhiteboard;
	}

	@Override
	public void paint(GraphicsContext graphicsContext) {
		graphicsContext.setWhiteboardPage(pageNumber);

	}

	public boolean isWhiteboardEnabled() {
		return pageNumber > 0;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	@Override
	public String toString() {
		return "Message: [" + Constants.getStringFromTime(timestamp, true)
				+ "]" + "\tEncoding: "
				+ Constants.encodingToString(getEncoding()) + " (page "
				+ getPageNumber() + ")" + " - " + width + " x " + height
				+ " at (" + x + "," + y + ")";
	}

}
