// TeleTeachingTool - Presentation Recording With Automated Indexing
//
// Copyright (C) 2003-2008 Peter Ziewer - Technische Universit???t M???nchen
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

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import com.metallic.tttandroid.ttt.core.Constants;
import com.metallic.tttandroid.ttt.core.GraphicsContext;

/**
 * main messages for FrameBufferUpdates, most parts are adopted from the TTT
 * Java-Version.
 * 
 * @author Thomas Krex
 * 
 */
public class HextileMessage extends FramebufferUpdateMessage {
	// reading from stream is easier then reading from byte array
	private final ByteArrayInputStream byteArrayInputStream;
	private final DataInputStream is;

	// constructor
	public HextileMessage(int timestamp, int x, int y, int width, int height,
			byte[] data) {
		super(timestamp, x, y, width, height);
		// encoded message data without header
		this.data = data;

		// create streams to read data
		// NOTE: don't use buffered streams, because this will lead to heavy
		// memory usage;
		byteArrayInputStream = new ByteArrayInputStream(this.data);
		is = new DataInputStream(byteArrayInputStream);
	}

	// read from TTT input stream
	public HextileMessage(int timestamp, DataInputStream in, int size)
			throws IOException {
		this(timestamp, in.readShort(), in.readShort(), in.readShort(), in
				.readShort(), FramebufferUpdateMessage.readBytes(in, size - 8));

	}

	// constructor - reading message from RFB input stream and directly draw
	// rectangle to graphics context
	public HextileMessage(int timestamp, int x, int y, int width, int height,
			GraphicsContext graphicsContext, DataInputStream is)
			throws IOException {

		this(timestamp, x, y, width, height, handleAndBufferHextileRect(
				graphicsContext, is, x, y, width, height, false));

	}

	@Override
	public int getEncoding() {
		return Constants.EncodingHextile;
	}

	// draw rectangle to graphics context
	@Override
	public void paint(GraphicsContext graphicsContext) {

		try {
			byteArrayInputStream.reset();

			// fill the pixel array
			handleHextileRect(graphicsContext, is, null, x, y, width, height,
					updateFlag);
			// change pixels of bitmap
//			graphicsContext.handleUpdatedPixels(x, y, width, height);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	Random random = new Random();

	static private byte[] handleAndBufferHextileRect(
			GraphicsContext graphicsContext, DataInputStream is, int x, int y,
			int w, int h, boolean updateFlag) throws IOException {
		// create buffer
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream buffer = new DataOutputStream(
				new BufferedOutputStream(byteArrayOutputStream));

		// handle
		handleHextileRect(graphicsContext, is, buffer, x, y, w, h, updateFlag);

		// write buffer
		buffer.flush();
		return byteArrayOutputStream.toByteArray();
	}

	// These colors should be kept between handleHextileSubrect() calls.
	static private void handleHextileRect(GraphicsContext graphicsContext,
			DataInputStream is, DataOutputStream os, int x, int y, int w,
			int h, boolean updateFlag) throws IOException {

		byte[] hextile_bg_encoded = graphicsContext.getHextile_bg_encoded();
		byte[] hextile_fg_encoded = graphicsContext.getHextile_fg_encoded();

		// scan hextiles
		for (int ty = y; ty < y + h; ty += 16) {
			int th = 16;
			if (y + h - ty < 16)
				th = y + h - ty;

			for (int tx = x; tx < x + w; tx += 16) {
				int tw = 16;
				if (x + w - tx < 16)
					tw = x + w - tx;
				handleHextileSubrect(graphicsContext, is, os, tx, ty, tw, th,
						hextile_bg_encoded, hextile_fg_encoded);
			}
		}

	}

	// Handle one tile in the Hextile-encoded data.
	static private void handleHextileSubrect(GraphicsContext graphicsContext,
			DataInputStream is, DataOutputStream os, int tx, int ty, int tw,
			int th, byte[] hextile_bg_encoded, byte[] hextile_fg_encoded) throws IOException {

		int subencoding = is.readUnsignedByte();

		// buffering
		if (os != null)
			os.writeByte(subencoding);

		// Is it a raw-encoded sub-rectangle?
		if ((subencoding & Constants.HextileRaw) != 0) {
			handleRawRect(graphicsContext, is, os, tx, ty, tw, th);
			return;
		}

		// TODO: what if bytesPerPixel of message differ from
		// graphicsContext???????????
		// Read and draw the background if specified.
		byte[] cbuf = new byte[graphicsContext.getPrefs().bytesPerPixel];

		if ((subencoding & Constants.HextileBackgroundSpecified) != 0) {
			is.readFully(cbuf);

			// buffering
			if (os != null)
				os.write(cbuf);

			// store encoded background color
			graphicsContext.setBackground(cbuf, 0);
		}

		fillRect(graphicsContext, tx, ty, tw, th,
				hextile_bg_encoded);

		// Read the foreground color if specified.
		if ((subencoding & Constants.HextileForegroundSpecified) != 0) {
			is.readFully(cbuf);

			// buffering
			if (os != null)
				os.write(cbuf);

			// store encoded foreground color
			graphicsContext.setForeground(cbuf, 0);
		}

		// Done with this tile if there is no sub-rectangles.
		if ((subencoding & Constants.HextileAnySubrects) == 0)
			return;

		int nSubrects = is.readUnsignedByte();
		int bufsize = nSubrects * 2;

		if ((subencoding & Constants.HextileSubrectsColoured) != 0) {
			bufsize += nSubrects * graphicsContext.getPrefs().bytesPerPixel;
		}
		byte[] buf = new byte[bufsize];
		is.readFully(buf);

		// buffering
		if (os != null) {
			os.writeByte(nSubrects);
			os.write(buf);
		}

		int b1, b2, sx, sy, sw, sh;
		int i = 0;

		for (int j = 0; j < nSubrects; j++) {
			if ((subencoding & Constants.HextileSubrectsColoured) != 0) {
				// store encoded foreground color
				graphicsContext.setForeground(buf, i);

				i += graphicsContext.getPrefs().bytesPerPixel;
			}
			// decode subrect
			b1 = buf[i++] & 0xFF;
			b2 = buf[i++] & 0xFF;
			sx = tx + (b1 >> 4);
			sy = ty + (b1 & 0xf);
			sw = (b2 >> 4) + 1;
			sh = (b2 & 0xf) + 1;

			fillRect(graphicsContext, sx, sy, sw, sh,
					hextile_fg_encoded);
		}
	}

	// handle raw encoded sub-rectangle
	static private void handleRawRect(GraphicsContext graphicsContext,
			DataInputStream is, DataOutputStream os, int x, int y, int w, int h)
			throws IOException {

		// TODO Android: support more bytes per pixel

		switch (graphicsContext.getPrefs().bytesPerPixel) {
		case 2:
			byte[] buf = new byte[2];
			// Paint p = new Paint();
			int color;
			for (int dy = y; dy < y + h; dy++) {
				for (int i = x; i < x + w; i++) {

					// TODO make buffer containing the whole rect, not only 2
					// bytes... Android

					is.readFully(buf);

					// buffering
					if (os != null)
						os.write(buf);
					color = graphicsContext.decodeColor(buf);
					graphicsContext.getPixels()[dy
							* graphicsContext.getPrefs().framebufferWidth + i] = color;

				}
			}
			break;

		}
	}

	// paint sub-reactangle
	static private void fillRect(GraphicsContext graphicsContext, int x, int y,
			int w, int h, byte[] colorField) {
		int color = graphicsContext.decodeColor(colorField);

		int screenWidth = graphicsContext.getPrefs().framebufferWidth;
		int[] array = graphicsContext.getPixels();

		for (int i = y; i < y + h; i++) {
			int offset = i * screenWidth + x;
			for (int j = 0; j < w; j++) {
				array[offset + j] = color;
			}
		}

	}
}
