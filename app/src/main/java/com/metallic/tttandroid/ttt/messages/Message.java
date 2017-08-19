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
import com.metallic.tttandroid.ttt.core.ProtocolPreferences;
import com.metallic.tttandroid.ttt.messages.annotations.DeleteAllAnnotation;
import com.metallic.tttandroid.ttt.messages.annotations.DeleteAnnotation;
import com.metallic.tttandroid.ttt.messages.annotations.FreehandAnnotation;
import com.metallic.tttandroid.ttt.messages.annotations.HighlightAnnotation;
import com.metallic.tttandroid.ttt.messages.annotations.LineAnnotation;
import com.metallic.tttandroid.ttt.messages.annotations.RectangleAnnotation;

public abstract class Message {

	// /////////////////////////////////////////////////
	// distinguish messages
	// /////////////////////////////////////////////////

	// only used for framebufferupdates, but defined here to avoid numerous
	// casts
	abstract public int getEncoding();

	// /////////////////////////////////////////////////
	// timestamp
	// /////////////////////////////////////////////////
	public int timestamp;

	// editor reimport code
	public int area = 0;

	public int getTimestampWithoutSync() {
		return timestamp;
	}

	// editor reimport code end

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	// /////////////////////////////////////////////////
	// update
	// /////////////////////////////////////////////////
	boolean updateFlag;

	// /////////////////////////////////////////////////
	// display message
	// /////////////////////////////////////////////////
	abstract public void paint(GraphicsContext graphicsContext);

	// return size of message in bytes (if written to stream)
	abstract public int getSize();

	// /////////////////////////////////////////////////
	// String output
	// /////////////////////////////////////////////////

	@Override
	public String toString() {
		return "Message: [" + Constants.getStringFromTime(timestamp, true)
				+ "]";
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// read from stream and create message
	// //////////////////////////////////////////////////////////////////////////////////////////////////

	// read message
	static public Message readMessage(DataInputStream in,
			ProtocolPreferences prefs) throws IOException {
		Message message;

		// read message header
		int size = in.readInt();
		// TODO: maybe readUnsignedByte()
		int encoding = in.readByte();
		size--; // size minus encoding

		// read timestamp
		// TODO: maybe unset timestamp can be -1
		int timestamp = 0;
		if ((encoding & Constants.EncodingFlagTimestamp) != 0) {
			// timestamp bit set -> message contains timestamp

			// read (and fix) timestamp
			timestamp = Math.max(timestamp, in.readInt());

			size -= 4; // size - timestamp
		}

		boolean updateFlag = false;
		if ((encoding & Constants.EncodingFlagUpdate) != 0) {
			// TODO: handle update flag
			updateFlag = true;
		}

		// remove flags
		encoding &= Constants.EncodingMask;

		// read body
		switch (encoding) {
		case Constants.AnnotationRectangle:
			message = new RectangleAnnotation(timestamp, in);
			break;
		//
		case Constants.AnnotationHighlight:
			message = new HighlightAnnotation(timestamp, in);
			break;
		//
		case Constants.AnnotationLine:
			message = new LineAnnotation(timestamp, in);
			break;
		//
		case Constants.AnnotationFreehand:
			message = new FreehandAnnotation(timestamp, in);
			break;
		//
		// case Constants.AnnotationImage: // MODMSG
		// message = new ImageAnnotation(timestamp, in);
		// break;
		//
		// case Constants.AnnotationText:
		// message = new TextAnnotation(timestamp, in); // MODMSG
		// break;
		//
		case Constants.AnnotationDelete:
			message = new DeleteAnnotation(timestamp, in);
			break;
		//
		case Constants.AnnotationDeleteAll:
			message = new DeleteAllAnnotation(timestamp);
			break;
		//
		// case Constants.EncodingTTTCursorPosition:
		// message = new CursorPositionMessage(timestamp, in);
		// break;
		//
		case Constants.EncodingWhiteboard:
			message = new WhiteboardMessage(timestamp, in.readByte(), prefs);
			break;

		case Constants.EncodingHextile:
			message = new HextileMessage(timestamp, in, size);
			break;
		//
		case Constants.EncodingRaw:
			message = new RawMessage(timestamp, in, size);
			break;

		// case Constants.EncodingInterlacedRaw:
		// // message = new InterlacedRawMessage(timestamp,x,y,w,h,msg);
		// // TODO: maybe return empty message instead of null
		// message = null;
		// break;

		// case Constants.EncodingTTTRichCursor:
		// case Constants.EncodingTTTXCursor:
		// message = new CursorMessage(timestamp, encoding, in, size);
		// break;

		default:
			// System.out.println("skipping unsupported message: Encoding = "
			// + Constants.encodingToString(encoding) + "\t" + size
			// + " bytes");
			do {
				size -= in.skipBytes(size);
			} while (size > 0);

			// TODO: maybe return empty message instead of null
			message = null;
			break;

		}

		if (message != null)
			message.updateFlag = updateFlag;

		return message;
	}
}
