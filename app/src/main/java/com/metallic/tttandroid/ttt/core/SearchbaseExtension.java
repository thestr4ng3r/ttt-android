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
 * Created on 06.04.2006
 *
 * Author: Peter Ziewer, TU Munich, Germany - ziewer@in.tum.de
 */
package com.metallic.tttandroid.ttt.core;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Class parses the SeachbaseExtension from the ttt-File. This Class was adopted
 * from TTT
 * 
 * @author Thomas Krex
 * 
 */
public class SearchbaseExtension {
	//

	// read searchbase extension
	static public void readSearchbaseExtension(DataInputStream in, Index index)
			throws IOException {
		// NOTE: assumes header tag already read

		// number of index entries
		int size = in.readShort();

		if (size != index.size())
			throw new IOException(
					"Number of entries in SEARCHBASE EXTENSION does not match with index of recording!!!!!!!!!");

		// ratio
		// NOTE: This is Omnipage XML Document specific, where coordinates
		// differ from input screenshot.
		// However, it seems to be persistent for all pages (will fail, if not)
		double ratio = in.readDouble();

		// write searchbase for each index
		for (int i = 0; i < index.size(); i++)
			index.get(i).readSearchbase(in, ratio);

		index.searchbaseFormat = index.searchbaseFormatStored = Index.XML_SEARCHBASE;
	}

}
