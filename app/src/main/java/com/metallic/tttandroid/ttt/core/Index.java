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
 * Created on 27.02.2006
 *
 * Author: Peter Ziewer, TU Munich, Germany - ziewer@in.tum.de
 */
package com.metallic.tttandroid.ttt.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.metallic.tttandroid.ttt.messages.FramebufferUpdateMessage;
import com.metallic.tttandroid.ttt.messages.Message;
import com.metallic.tttandroid.ttt.messages.WhiteboardMessage;
import com.metallic.tttandroid.ttt.messages.annotations.Annotation;
import com.metallic.tttandroid.ttt.messages.annotations.DeleteAllAnnotation;

/**
 * class for handling index of the recording,consist of all index entries.
 * handles updating the running index, initialize scrollview and handles the
 * focusing of the current indexEntry. Adopted parts from TTT will be marked
 * with (TTT)
 *
 * @author Thomas Krex
 *
 */
public class Index {

	final public static int NO_SEARCHBASE = 0;
	final public static int ASCII_SEARCHBASE = 1;
	final public static int XML_SEARCHBASE = 2;

	public ArrayList<IndexEntry> index = new ArrayList<IndexEntry>();
	ArrayList<IndexEntry> search_index = new ArrayList<IndexEntry>();

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	// constructors
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////

	private Recording recording;
	private final Context context;
	private final ScrollView indexViewer;
	private final Handler scrollHandler;
	public int scrollPos;
	int getWidth() {
		return recording.getProtocolPreferences().framebufferWidth;
	}

	public Recording getRecording() {
		return recording;
	}

	public Index(Recording recording, ScrollView scrollView, Context context) {
		this.recording = recording;
		this.context = context;
		this.indexViewer = scrollView;
		index.add(new IndexEntry(context, this));
		scrollHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(android.os.Message inputMessage) {
				// Gets the image task from the incoming Message object.
				int indexNumber = inputMessage.arg1;
				int color = inputMessage.arg2;
				IndexEntry entry = get(indexNumber - 1);
				entry.setBackgroundColor(color);
				LinearLayout parent = (LinearLayout) entry.getParent();
				scrollPos=parent.getTop() - parent.getHeight()
						/ 2;
				indexViewer.scrollTo(0, scrollPos);

			}
		};
	}

	public void close() {
		recording = null;
		index.clear();
		index = null;
		search_index.clear();
		search_index = null;
	}

	// is this index filled with data - computed or read from extension
	private boolean valid;

	public boolean isValid() {
		return valid;
	}

	// check if thumbnail is available for each index
	public boolean thumbnailsAvailable() {
		for (int i = 0; i < index.size(); i++) {
			if (!index.get(i).hasThumbnail())
				return false;
		}
		return true;
	}

	// create index
	public void computeIndex() {
		computeIndex_regarding_length_of_sequence();

	}

	/**
	 * computing index if not available in extensions(TTT)
	 */
	void computeIndex_regarding_length_of_sequence() {
		// build slide index
		// containing all message which area covers minSlideArea and a time
		// interval of at least minSlideDiffMsecs

		if (true)
			System.out.println("\ncompute index table:\n");

		// delete index
		index.clear();

		int minSlideArea = recording.getProtocolPreferences().framebufferWidth
				* recording.getProtocolPreferences().framebufferHeight / 5;

		int minSlideDiffMsecs = 10000;
		int minSequenceLength = 5;

		// count sequence with gaps less than minSlideDiffMsecs
		int animationCount = 0;

		int timestamp = Integer.MIN_VALUE + 1;
		int previous_timestamp = -1;
		int area = 0;

		// build index based on covered area
		for (int i = 0; i < recording.getMessages().size(); i++) {
			Message message = recording.getMessages().get(i);

			// sum up area(s)
			if (message instanceof FramebufferUpdateMessage)
				area += ((FramebufferUpdateMessage) message).getCoveredArea();
			else if (area == 0)
				// only FramebufferUpdates are useful - skip others
				// Note: do not skip if same timestamp as previous
				// framebufferupdate
				continue;

			// cumulate areas of same timestamp
			if (i + 1 < recording.getMessages().size()
					&& message.getTimestamp() == recording.getMessages()
							.get(i + 1).getTimestamp())
				continue;

			// check size
			if (area > minSlideArea) {
				// no animation or first index
				if ((message.getTimestamp() - timestamp > minSlideDiffMsecs)
						|| index.size() == 0) {
					if (animationCount > 0
							&& animationCount < minSequenceLength
							&& previous_timestamp >= 0) {
						// no animation, take last message of sequence
						// (animations take first message of sequence as index)
						if (index.size() > 0)
							index.set(index.size() - 1, new IndexEntry(context,
									this, previous_timestamp));
						else
							// first index
							index.add(new IndexEntry(context, this,
									previous_timestamp));

						// if (TTT.verbose)
						// System.out.print(" RESET");
					}

					animationCount = 0;

					if (true)

						index.add(new IndexEntry(context, this, message
								.getTimestamp()));
				} else {
					// distinguish animations from multiple slide changes
					animationCount++;
					previous_timestamp = message.getTimestamp();

				}
				timestamp = message.getTimestamp();
			}

			// reset cumulated area
			area = 0;
		}

		// fix last index if needed
		if (animationCount > 0 && animationCount < minSequenceLength
				&& previous_timestamp >= 0 && index.size() > 0) {
			// no animation, take last message of sequence
			// (animations take first message of sequence as index)
			index.set(index.size() - 1, new IndexEntry(context, this,
					previous_timestamp));
			if (true)
				System.out.print(" RESET");
		}

		if ((index.size() > 0)
				&& (index.get(index.size() - 1).getTimestamp() >= recording
						.getMessages().get(recording.getMessages().size() - 1)
						.getTimestamp())) {
			index.remove(index.size() - 1);
			if (true)
				System.out
						.print(" - Removing last index, because it uses timestamp of last message.");
		}

		// add index at beginning if needed
		if (index.size() == 0 || index.get(0).getTimestamp() > 2000) {
			index.add(0, new IndexEntry(context, this, 0));
		}

	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	// file I/O
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Methods for extracting index information out of the ttt-File (TTT)
	 *
	 */

	// read from extension
	public void readIndexExtension(DataInputStream in) throws IOException {
		// remove current index
		index.clear();

		// NOTE: assumes header tag already read

		// header
		int number_of_table_entries = in.readShort();

		// index table
		for (int i = 0; i < number_of_table_entries; i++) {
			// timestamp
			int timestamp = in.readInt();
			// title
			int titelLength = in.readByte();
			byte[] titleArray = new byte[titelLength];
			in.readFully(titleArray);
			String title = new String(titleArray);
			// searchable text
			int searchableLength = in.readInt();
			if (searchableLength > 0)
				searchbaseFormat = searchbaseFormatStored = ASCII_SEARCHBASE;
			byte[] searchableArray = new byte[searchableLength];
			in.readFully(searchableArray);
			String searchable = new String(searchableArray);
			// TODO android workarround

			// BufferedImage image = readThumbnail(in);
			readThumbnail(in);

			// add index entry
			index.add(new IndexEntry(context, this, title, timestamp,
					searchable));

		}

		// check if valid
		valid = index.size() > 0;
		if (!valid)
			index.add(new IndexEntry(context, this));
	}

	private void readThumbnail(DataInputStream in) throws IOException {
		int image_size = in.readInt();
		if (image_size == 0) {
			// thumbnail not available
			// return null;
		} else {
			// thumbnail available
			byte[] image_array = new byte[image_size];
			in.readFully(image_array);
			// BufferedImage bufferedImage = ImageIO
			// .read(new ByteArrayInputStream(image_array));
			// thumbnail_scale_factor = recording.prefs.framebufferHeight
			// / bufferedImage.getHeight();
			// return bufferedImage;
		}
	}

	// //////////////////////////////////////////////
	// Annotations (TTT)
	// //////////////////////////////////////////////

	// displaying annotations on thumbnails
	static final public int PAINT_ALL_ANNOTATIONS = 0;
	static final public int PAINT_NO_ANNOTATIONS = 1;
	static final public int PAINT_NO_HIGHLIGHT_ANNOTATIONS = 2;
	// disabled because not working yet
	int annotationsPaintMode = PAINT_ALL_ANNOTATIONS;

	public int getAnnotationsPaintMode() {
		return annotationsPaintMode;
	}

	public void setAnnotationsPaintMode(int annotationsPaintMode) {
		this.annotationsPaintMode = annotationsPaintMode;
	}

	/**
	 * Method for extract Annotations(TTT)
	 */
	public void extractAnnotations() {
		// Buffer for annotations
		ArrayList<Annotation> annotations = new ArrayList<Annotation>();

		// message counter
		int message_nr = 0;

		// get annotations for each index
		for (int i = 0; i < recording.getIndex().size(); i++) {
			// get end time of index
			int end;
			if (i + 1 == recording.getIndex().size())
				// last index ends with last message
				end = recording.getMessages()
						.get(recording.getMessages().size() - 1).getTimestamp();
			else
				// all other end with the beginning of the following index
				end = recording.getIndex().get(i + 1).getTimestamp();

			// read message
			Message message = recording.getMessages().get(message_nr);
			int start = message.getTimestamp();



			// delete flag
			boolean marked_to_be_deleted = false;

			// gather annotations until end of index
			while (message.getTimestamp() < end) {
				// stop if switching to another whiteboard (or to desktop) after
				// the start
				// NOTE: otherwise anotations will not belong to thumbnail
				// screenshot
				// TODO
				if (message instanceof WhiteboardMessage
						&& message.getTimestamp() > start)
					break;
				//
				// delete events
				if (message instanceof DeleteAllAnnotation
						|| message instanceof WhiteboardMessage) {
					// Note: typically a new index is preceded by a
					// DeleteAllMessage
					// therefore only register delete events here
					// and only clear buffer if it was not the last one for this
					// index
					marked_to_be_deleted = true;
				}
				// other annotations
				if (message instanceof Annotation) {
					// delete buffered annotations of needed
					// Note: previous annotations are only deleted if the delete
					// event is suceeded by other annotations
					if (marked_to_be_deleted) {
						annotations.clear();
						marked_to_be_deleted = false;
					}
					// add annotations to buffer
					annotations.add((Annotation) message);
				}
				// read next message
				message_nr++;
				if (message_nr == recording.getMessages().size())
					break;
				message = recording.getMessages().get(message_nr);
			}

			// set collected annotations
			recording.getIndex().get(i).setAnnotations(annotations);
			Log.d("Index annotations", "index "+i+" start "+start+" end" +end+" annotations " +annotations.size());

			// clear buffer if index ended with a delete event, which was not
			// performed yet
			if (marked_to_be_deleted) {
				// delete buffered annotations
				annotations.clear();
				marked_to_be_deleted = false;
			}
		}
	}

	// //////////////////////////////////////////////////////////////////
	// thumbnails
	// //////////////////////////////////////////////////////////////////

	/**
	 * Create Thumnails for each index entry, time is set, and the bitmap
	 * created in graphicsContext
	 *
	 * @return
	 * @throws IOException
	 */

	public boolean createScreenshots() throws IOException {

		// measure time
		boolean isCanceled = false;

		// compute
		for (int i = 0; i < index.size(); i++) {

			// set time of index
			IndexEntry indexEntry = index.get(i);
			int timestamp = indexEntry.getTimestamp();
			// for screenshots only the pixel array has to set but not the
			// display/audio
			recording.setTime(timestamp, false);

			// create screenshot
			Bitmap screenshot = recording.getGraphicsContext()
					.createScreenshotWithoutAnnotations();

			// insert the bitmap in the indexEntry
			indexEntry.setThumbnail(screenshot);

		}

		// reset playback
		return isCanceled == false;
	}

	// //////////////////////////////////////////////////////////////////
	// Index View
	// //////////////////////////////////////////////////////////////////

	/**
	 * adds the index entries to the scrollview
	 */
	public void initIndexView() {

		// needed because scrollview can only have one direct child
		LinearLayout layout = new LinearLayout(context);
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setBackgroundColor(Color.WHITE);

		int length = size();

		for (int i = 0; i < length; i++) {
			IndexEntry entry = index.get(i);
			layout.addView(addTitle(i));
			layout.addView(insertEntry(entry));
		}

		indexViewer.addView(layout);

	}

	public View insertEntry(IndexEntry entry) {

		LinearLayout layout = new LinearLayout(context);
		layout.setBackgroundColor(Color.WHITE);
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		layout.setGravity(Gravity.CENTER);

		layout.addView(entry);
		return layout;
	}

	View addTitle(int number) {
		TextView title = new TextView(context);
		title.setText(String.valueOf(number + 1));
		title.setTextAppearance(context, android.R.style.TextAppearance_Large);
		title.setTextColor(Color.BLACK);
		return title;
	}

	// //////////////////////////////////////////////////////////////////
	// controlling
	// //////////////////////////////////////////////////////////////////

	private int nowPlayingIndex_startingAtZero;

	synchronized public IndexEntry getCurrentIndex() {
		return index.get(nowPlayingIndex_startingAtZero);
	}

	/**
	 * returns the number of the index entry for a certain time
	 *
	 * @param time
	 *            the timestamp
	 *
	 * @return IndexEntry for this timestamp
	 */
	synchronized public IndexEntry getCorrespondingIndex(int time) {
		// find corresponding index
		int i = 1;
		while (i < index.size() && time >= index.get(i).getTimestamp())
			i++;

		return index.get(--i);
	}

	/**
	 * setting the index entry for a certain time
	 *
	 * @param time
	 *            timestamp
	 */
	synchronized public void setCorrespondingIndex(int time) {
		// find corresponding index
		int i;
		for (i = index.size() - 1; i > 0; i--)
			if (index.get(i).getTimestamp() <= time)
				break;

		// set index
		nowPlayingIndex_startingAtZero = i;
		fireIndexChangedEvent(nowPlayingIndex_startingAtZero + 1);
	}

	public IndexEntry get(int i) {
		try {
			return index.get(i);
		} catch (Exception e) {
			return null;
		}
	}

	public int size() {
		return index.size();
	}

	/**
	 * get next Index Entry(TTT)
	 *
	 * @return index entry
	 *
	 */
	synchronized public IndexEntry getNextIndex() {
		if (nowPlayingIndex_startingAtZero + 1 < index.size())
			return index.get(nowPlayingIndex_startingAtZero + 1);
		else
			return index.get(0);
	}

	/**
	 * get previous IndexEntry(TTT)
	 *
	 * @return index entry
	 */
	synchronized public IndexEntry getPreviousIndex() {
		if (nowPlayingIndex_startingAtZero > 0)
			return index.get(nowPlayingIndex_startingAtZero - 1);
		else
			return index.get(0);
	}

	/**
	 * checks if current index has changed
	 *
	 * @param timestamp
	 *            current timestamp
	 */
	public void updateRunningIndex(int timestamp) {
		// set index marker if needed
		for (int i = 0; i < index.size(); i++)
			if (index.get(i).getTimestamp() == timestamp) {
				nowPlayingIndex_startingAtZero = i;

				// fire event (index event starting at one)
				fireIndexChangedEvent(nowPlayingIndex_startingAtZero + 1);

				break;
			}
	}

	// ///////////////////////////////////////////////////////////////////////////////////
	// Index Listeners
	// ///////////////////////////////////////////////////////////////////////////////////

	private int lastIndexFired = -1;
	public int getLastIndexFired() {
		return lastIndexFired;
	}

	/**
	 * if index current index has changed scrollview is scrolled to this view
	 * and index entry is highlighted
	 *
	 * @param indexNumber
	 *            number of the new current index
	 */
	public void fireIndexChangedEvent(int indexNumber) {
		if (lastIndexFired != indexNumber) {
			Log.d("Index Update","index number: "+indexNumber);
			// highlighting of index entry and scrolling has to be done in ui
			// thread
			if (lastIndexFired > 0) {
				android.os.Message unMarkMessage = scrollHandler.obtainMessage(
						0, lastIndexFired, Color.LTGRAY);
				unMarkMessage.sendToTarget();
			}

			lastIndexFired = indexNumber;
			android.os.Message markMessage = scrollHandler.obtainMessage(0,
					indexNumber, Color.RED);
			markMessage.sendToTarget();

		}
	}

	// //////////////////////////////////////////////////////////////////
	// search
	// //////////////////////////////////////////////////////////////////

	public int searchbaseFormatStored = NO_SEARCHBASE;
	public int searchbaseFormat = NO_SEARCHBASE;

	public int getSearchbaseFormat() {
		return searchbaseFormat;
	}

	public int getSearchbaseFormatStored() {
		return searchbaseFormatStored;
	}

	/**
	 * seacrhing for a given keyword(TTT). if index entry contains searchword ,
	 * it will be added to search_index
	 *
	 * @param searchword
	 */
	public void search(String searchword) {
		// clear old results
		search_index.clear();

		// perform search
		for (int i = 0; i < index.size(); i++)
			// add to search results
			if (index.get(i).contains(searchword))
				search_index.add(index.get(i));

	}

	/**
	 * set Time of Recording to the next IndexEntry that contains the current
	 * searchword(TTT)
	 */
	public void nextSearchResult() {
		int time = recording.getTime();
		for (int i = 0; i < search_index.size(); i++) {
			// found - set player to index with next search result
			if (search_index.get(i).getTimestamp() > time) {
				recording.setTime(search_index.get(i).getTimestamp(), true);
				break;
			}
			// reached end of recording - start from beginning
			if (i == search_index.size() - 1)
				recording.setTime(search_index.get(0).getTimestamp(), true);
		}

	}

	/**
	 * highlighting the searchResults of the current index on the main image
	 *
	 * @param canvas
	 *            canvas to paint on the main Bitmap
	 */
	public void highlightSearchResultsOfCurrentIndex(Canvas canvas) {
		getCurrentIndex().highlightSearchResults(canvas);
	}

	/**
	 * update the bitmaps of the all index entries after an new search has to be
	 * done
	 */
	public void updateThumbnails() {
		for (int i = 0; i < index.size(); i++) {
			index.get(i).updateThumbail();

		}
	}
}
