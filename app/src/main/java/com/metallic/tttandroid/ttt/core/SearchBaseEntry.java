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
 * Created on 08.09.2005
 *
 * Author: Peter Ziewer, TU Munich, Germany - ziewer@in.tum.de
 */
package com.metallic.tttandroid.ttt.core;

import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.RectF;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.metallic.tttandroid.ttt.shapes.MyRectF;

/**
 * @author ziewer
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * Class adopted from TTT, only the definitions of the rectangle and colors were
 * adjusted to android.
 * 
 * @author Thomas Krex
 * 
 */
public class SearchBaseEntry {

	private final int x, y, width, height;

	// TODO: maybe ratio should be handled in index
	private double ratio = 1;

	double getRatio() {
		return ratio;
	}

	String searchText = "";
	String searchTextOriginal = "";

	public SearchBaseEntry(String searchtext, int x, int y, int width,
			int height, double ratio) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		this.ratio = ratio;

		this.searchTextOriginal = searchtext;
		this.searchText = Constants.reduce(searchtext);
	}

	// for highlighting search results
	static int borderSize = 6;
	static int highlighColor = Color.argb(64, Color.red(Color.CYAN),
			Color.green(Color.CYAN), Color.blue(Color.CYAN));
	static int underlineColor = Color.RED;
	static int borderColor = Color.argb(192, Color.red(Color.RED),
			Color.green(Color.RED), Color.blue(Color.RED));

	// TODO: return value not used / think about naming of method
	// adds words from searchbase containing searchword to resultss
	public boolean contains(String searchword, ArrayList<MyRectF> results) {
		if (searchText.indexOf(Constants.reduce(searchword)) >= 0) {

			// highlight search results
			if (results != null) {
				// NOTE: explicit round to avoid java drawing bug (different
				// rounding if color is transparent)
				RectF rectangle = new RectF(
						(int) (getX() - borderSize),
						(int) (getY() - borderSize),
						(int) ((getX() - borderSize) + (getWidth() + 2 * borderSize)),
						(int) ((getY() - borderSize) + (getHeight() + 2 * borderSize)));

				// highlight
				results.add(new MyRectF(rectangle, highlighColor, Style.FILL));

				// border
				results.add(new MyRectF(rectangle, borderColor, Style.STROKE));

				// underline results
				boolean fixedSize = true;
				if (fixedSize) {
					// using fixed sized font
					// TODO: use character specific letter size - variable sized
					// font
					double sizeOfLetter = (getWidth()) / searchText.length();
					int indexOf = -1;
					// System.out.println("\"" + result.searchText + "\"");
					while (-1 != (indexOf = searchText.indexOf(searchword,
							indexOf + 1))) {
						// System.out.println(indexOf + "\t" + sizeOfLetter +
						// "\t" + (result.right - result.left));

						// from rectangle2d(x,y,width,height) to rect(left ,top,
						// right bottom)
						float left = (float) (getX() + sizeOfLetter * indexOf);
						float top = (float) (getY() + getHeight() + borderSize - 2);
						float right = left
								+ (float) (sizeOfLetter * (searchword.length()));
						float bottom = top + 4;
						results.add(new MyRectF(new RectF(left, top, right,
								bottom), underlineColor, Style.FILL));
					}

				} else {
					// proportional size
					int charWidths[] = new int[searchTextOriginal.length()];
					int overall = 0;
					for (int i = 0; i < charWidths.length; i++) {
						// determine character widths
						try {
							// charWidths[i] =
							// widths[searchTextOriginal.charAt(i)];
							charWidths[i] = 5;
							overall += charWidths[i];
						} catch (IndexOutOfBoundsException e) {
						}
					}
					double ratio = getWidth() / overall;

					// determine occurrences
					int indexOf = -1;
					// System.out.println("\"" + result.searchText + "\"");
					while (-1 != (indexOf = searchTextOriginal.toLowerCase()
							.indexOf(searchword.toLowerCase(), indexOf + 1))) {
						int start = 0;
						for (int i = 0; i < indexOf; i++)
							start += charWidths[i];
						int end = 0;
						for (int i = indexOf; i < indexOf + searchword.length(); i++)
							end += charWidths[i];

						// System.out.println(indexOf + "\t" + sizeOfLetter +
						// "\t" + (result.right - result.left));
						float left = (float) (getX() + start * ratio);
						float top = (float) (getY() + getHeight() + borderSize - 3);
						float right = left + (float) (end * ratio);
						float bottom = top + 5;
						results.add(new MyRectF(new RectF(left, top, right,
								bottom), underlineColor, Style.FILL));

					}

				}

			}
			return true;

		} else
			return false;
	}

	@Override
	public String toString() {
		return "\"" + searchText + "\" at (" + (int) getX() + ","
				+ (int) getY() + ") size " + (int) getWidth() + " x "
				+ (int) getHeight();
	}

	private double getX() {
		return x * ratio;
	}

	private double getY() {
		return y * ratio;
	}

	private double getWidth() {
		return width * ratio;

	}

	private double getHeight() {
		return height * ratio;
	}

	// ////////////////////////////////////////////////////
	// I/O used for searchbase extension
	// ////////////////////////////////////////////////////

	// read as part of searchbase extension
	static public SearchBaseEntry read(DataInputStream in, double ratio)
			throws IOException {
		int size = in.readShort();
		byte[] string = new byte[size];
		in.readFully(string);

		return new SearchBaseEntry(new String(string), in.readShort(),
				in.readShort(), in.readShort(), in.readShort(), ratio);
	}
}