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
 * Created on 05.01.2006
 *
 * Author: Peter Ziewer, TU Munich, Germany - ziewer@in.tum.de
 */
package com.metallic.tttandroid.ttt.core;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.InflaterInputStream;

import com.metallic.tttandroid.ttt.messages.Message;
import com.metallic.tttandroid.ttt.messages.MessageProducerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.widget.ImageView;
import android.widget.ScrollView;

/**
 * main class for controlling the replay of an lecture. It is parsing the
 * recording file. the resulting information are messages, the protocol
 * preferences, the index with all index entries. After that, it initialize the
 * index Viewer. This class also contains the main loop for the replay. Methods
 * that are adopted from TTT will be marked with "(TTT)".
 * 
 * 
 * 
 * @author Thomas Krex
 * 
 */
public class Recording extends MessageProducerAdapter implements Runnable
{
	private final ProtocolPreferences prefs;
	private final GraphicsContext graphicsContext;
	private final MediaPlayer audioPlayer;

	private final Messages messages;
	//private final Index index;
	private File tttFile;

	public Messages getMessages() {
		return messages;
	}

	public Index getIndex() {
		return null;
	}

	public GraphicsContext getGraphicsContext() {
		return this.graphicsContext;
	}

	public void setMessages(ArrayList<Message> list) {
		messages.setmessages(list);
	}

	public Recording(File tttFile, MediaPlayer audioPlayer) throws IOException
	{
		this.tttFile = tttFile;
		// read
		messages = new Messages(this);
		prefs = new ProtocolPreferences();
		this.audioPlayer = audioPlayer;
		//index = new Index(this, scrollView, context);

		// read messages + extensions
		read(this.tttFile);

		graphicsContext = new GraphicsContext(this);
		// compute Index if not available in extensions
		/*if (!index.isValid())
			index.computeIndex();
		index.extractAnnotations();
		if (!index.thumbnailsAvailable())
			index.createScreenshots();
		index.initIndexView();*/

		graphicsContext.enableRefresh(true);
		setTime(0, true);
	}

	/**
	 * read all information of the recording (TTT)
	 * 
	 * @param file
	 *            ttt file
	 * @throws IOException
	 */
	void read(File file) throws IOException {

		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(file)));

		// read version
		byte[] b = new byte[12];
		in.readFully(b);

		// read compressed data
		in = new DataInputStream(new BufferedInputStream(
				new InflaterInputStream(in)));

		// read init parameters
		readServerInit(in, prefs);

		// read and parse all known extensions
		// e.g. index table, thumbnails, searchbase
		readExtensions(in);

		// read time of recording
		prefs.starttime = in.readLong();

		// read body of recording
		messages.readMessages(in);

	}

	@Override
	public ProtocolPreferences getProtocolPreferences() {
		return prefs;
	}

	public void close() {
		// stop thread
		if (thread != null && thread.isAlive()) {
			// end Thread
			running = false;

			// leave wait()
			synchronized (this) {
				notify();
			}

			// wait until finished to avoid exceptions
			while (thread != null && thread.isAlive())
				Thread.yield();
		}

	}

	/*******************************************************************************************************************
	 * Initialisation * (TTT)
	 ******************************************************************************************************************/

	/**
	 * Parse the ProtocolPreferenves(TTT)
	 * 
	 * @param in
	 * @param prefs
	 * @throws IOException
	 */
	static private void readServerInit(DataInputStream in,
			ProtocolPreferences prefs) throws IOException {
		prefs.framebufferWidth = in.readUnsignedShort();
		prefs.framebufferHeight = in.readUnsignedShort();
		prefs.bitsPerPixel = in.readUnsignedByte();
		switch (prefs.bitsPerPixel) {
		case 8:
			prefs.bytesPerPixel = 1;
			break;
		case 16:
			prefs.bytesPerPixel = 2;
			break;
		default:
			prefs.bytesPerPixel = 4;
			break;
		}
		prefs.depth = in.readUnsignedByte();
		prefs.bigEndian = (in.readUnsignedByte() != 0);
		prefs.trueColour = (in.readUnsignedByte() != 0);
		prefs.redMax = in.readUnsignedShort();
		prefs.greenMax = in.readUnsignedShort();
		prefs.blueMax = in.readUnsignedShort();
		prefs.redShift = in.readUnsignedByte();
		prefs.greenShift = in.readUnsignedByte();
		prefs.blueShift = in.readUnsignedByte();
		// padding
		in.skipBytes(3);
		int nameLength = in.readInt();
		byte[] name = new byte[nameLength];
		in.readFully(name);
		prefs.name = new String(name);
	}

	/*******************************************************************************************************************
	 * read extensions * (TTT)
	 ******************************************************************************************************************/

	// list of extensions
	private ArrayList<byte[]> extensions = new ArrayList<byte[]>();

	public ArrayList<byte[]> getExtensions() {
		return extensions;
	}

	public void setExtensions(ArrayList<byte[]> ext) {
		extensions = ext;
	}

	private void readExtensions(DataInputStream in) throws IOException {
		// new format without total length of all extensions
		int len;
		while ((len = in.readInt()) > 0) {
			byte[] extension = new byte[len];
			in.readFully(extension);

			extensions.add(extension);
		}

		parseExtensions();

	}

	/**
	 * parse the index tabale and searchbase table (if available)(TTT)
	 * 
	 * @throws IOException
	 */
	private void parseExtensions() throws IOException {
		for (int i = 0; i < extensions.size(); i++) {
			byte[] extension = extensions.get(i);
			DataInputStream ext_in = new DataInputStream(
					new ByteArrayInputStream(extension));
			int tag = ext_in.readByte();
			switch (tag) {
			case Constants.EXTENSION_INDEX_TABLE:
				// if (TTT.verbose)
				// System.out
				// .println("\n-----------------------------------------------\nReading Index Table\n");
				try {
					//index.readIndexExtension(ext_in);
				} catch (Exception e) {
					System.out
							.println("READING OF INDEX TABLE  EXTENSION FAILED: "
									+ e);
					// if (TTT.debug)
					// e.printStackTrace();
				}
				break;

			case Constants.EXTENSION_SEARCHBASE_TABLE_WITH_COORDINATES:
				if (true)
					System.out
							.println("\n-----------------------------------------------\nReading Searchbase Extension\n");
				try {
					//SearchbaseExtension.readSearchbaseExtension(ext_in, index);
					// extensions.remove(i);
				} catch (Exception e) {
					System.out
							.println("READING OF SEARCHBASE EXTENSION FAILED: "
									+ e);
					if (true)
						e.printStackTrace();
				}
				break;

			default:
				System.out
						.println("\n-----------------------------------------------\nUNKNOWN EXTENSION (["
								+ tag + "] " + extension.length + " bytes)\n");
				break;
			}
		}
	}

	/*******************************************************************************************************************
	 * playback
	 ******************************************************************************************************************/

	// flags
	private boolean running = true;
	private boolean interrupted;
	private boolean paused;
	public boolean adjusting;

	public int next_message;

	public boolean isPlaying() {
		return audioPlayer.isPlaying();
	}

	// main loop
	// display next messages
	// synchronize message and audio/video stream
	// (TTT)
	@Override
	public void run() {
		long t = System.currentTimeMillis();
		Message message = messages.get(next_message);
		while (running) {
			try {
				synchronized (this) {
					// wait if pause mode
					while (running && (paused || adjusting)) {
						wait();
					}

					// closing
					if (!running)
						break;

					//
					// // next message
					//
					// synchronize message and audio/video player
					// delay if too early
					int time = audioPlayer.getCurrentPosition();

					if (messages.get(next_message).timestamp <= time) {

						message = messages.get(next_message);

						// closing
						if (!running)
							break;
						//
						// // state changed - active message may be outdated

						// // abort
						if (interrupted || adjusting) {
							interrupted = false;
							continue;
						}

						// display message
						deliverMessage(message);

						// update index viewer
						//index.updateRunningIndex(message.getTimestamp());

						// increase message counter
						next_message++;
					}

				}
			} catch (Exception e) {
				stop();
				System.out.println("Replay Failed " + e.toString());
				t = System.currentTimeMillis() - t;
				// System.out.println("done elapsed: " +
				// Constants.getStringFromTime((int) t));
				t = System.currentTimeMillis();
			}
		}
	}

	// used by acuitus.com
	public void setNextMessage(int next) {
		next_message = next;
	}

	public void highlightSearchResults(Canvas canvas) {
		//index.highlightSearchResultsOfCurrentIndex(canvas);

	}

	/*******************************************************************************************************************
	 * playback control *
	 ******************************************************************************************************************/
	private Thread thread;

	// start playback
	synchronized public void play() {
		// ensure thread is running
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}

		paused = false;
		if (audioPlayer != null)
			audioPlayer.start();
		interrupt();
	}

	//
	// pause playback
	synchronized public void pause() {
		// fireTimeChangedEvent(TimeChangedListener.PAUSE);
		paused = true;
		if (audioPlayer != null)
			audioPlayer.pause();
		interrupt();
	}

	//
	public boolean paused() {
		return paused;
	}

	//
	// set playback to next index
	synchronized public void next() {
		//setTime(index.getNextIndex().getTimestamp(), true);
	}

	//
	// set playback to previous index
	synchronized public void previous() {
		//setTime(index.getPreviousIndex().getTimestamp(), true);
	}

	synchronized public void stop() {
		pause();

	}

	/**
	 * method to set time of the playback. This includes the collecting of all
	 * relevant messages and adjust the mediaPlayer
	 * 
	 * @param time
	 *            time the playback should be set to
	 * @param refresh
	 *            determines if the imageview should be updated, too
	 */
	public void setTime(int time, boolean refresh) {

		// refresh determine if display is updated or not

		// loop is stopped due to performance problems
		if (!adjusting)
			setAdjusting(true);

		// paint offscreen
		messages.setTime_full_frame_check(time);
		if (refresh) {

			// focusCurrentIndexEntry(time);
			//index.setCorrespondingIndex(time);
			setAudioPlayerTime(time);

			// TODO
			/*if (time == 0)
				graphicsContext.updateView(true);
			else
				graphicsContext.updateView(false);*/
			graphicsContext.updateImage();
		}

		// while loop is started again
		if (adjusting)
			setAdjusting(false);

	}

	// distinguish notify from timeout after wait
	synchronized private void interrupt() {
		interrupted = true;
		notify();
	}

	synchronized void setAdjusting(boolean state) {
		adjusting = state;
		notify();

	}

	/**
	 * set the time of the MediaPlayer
	 * 
	 * @param time
	 */
	public void setAudioPlayerTime(int time) {
		// synchronize audio/video
		if (audioPlayer != null)
			audioPlayer.seekTo(time);

		// notify playback loop
		// interrupt();
	}

	//
	/**
	 * returns playback time(TTT)
	 */
	synchronized public int getTime() {
		if (audioPlayer != null)
			// playback time
			return audioPlayer.getCurrentPosition();
		else
			// not exact as some time has may have passed since message was
			// displayed
			return messages.get(next_message).getTimestamp();
	}

	//
	// playback duration
	public int getDuration() {
		if (audioPlayer != null)
			return audioPlayer.getDuration();
		return messages.get(messages.size() - 1).getTimestamp();

	}

	// ///////////////////////////////////////////////////////////////////////
	// event handling
	// ///////////////////////////////////////////////////////////////////////

	/**
	 * reacts on the seekto method og the media controller in the
	 * PlayerActivity. Sets the time and update the ImageView
	 * 
	 * @param pos
	 */
	public void sliderStateChanged(int pos) {

		// bitmap is set and display is updated
		setTime(pos, true);

	}

}