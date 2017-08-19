package com.metallic.tttandroid.ttt.core;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.ImageView;

import java.util.ArrayList;

import com.metallic.tttandroid.ttt.messages.Message;
import com.metallic.tttandroid.ttt.messages.MessageConsumer;
import com.metallic.tttandroid.ttt.messages.MessageProducer;
import com.metallic.tttandroid.ttt.messages.WhiteboardMessage;
import com.metallic.tttandroid.ttt.messages.annotations.Annotation;
import com.metallic.tttandroid.ttt.utils.BitmapContainer;

/**
 * paint the messages to the bitmap. Bitmap shown in imageview. Annotations and
 * Whiteboard parts are adopted from TTT
 * 
 * 
 * @author Thomas Krex
 * 
 */
public class GraphicsContext implements MessageConsumer {

	private final BitmapContainer bitmapContainer;
	private ImageView imgView;

	private boolean refreshEnabled = false;
	private final MessageProducer producer;
	private final ProtocolPreferences prefs;
	private final Recording recording;

	private final byte[] hextile_bg_encoded;
	private final byte[] hextile_fg_encoded;
	private final int[] pixels;

	public GraphicsContext(ImageView imgV, Recording record) {
		setImageView(imgV);
		this.producer = record;
		this.recording = record;
		producer.addMessageConsumer(this);
		this.prefs = record.getProtocolPreferences();

		this.bitmapContainer = new BitmapContainer(Bitmap.createBitmap(
				prefs.framebufferWidth, prefs.framebufferHeight,
				Config.ARGB_4444));

		this.pixels = bitmapContainer.getPixels();

		hextile_bg_encoded = new byte[prefs.bytesPerPixel];
		hextile_fg_encoded = new byte[prefs.bytesPerPixel];

	}

	public void enableRefresh(boolean refresh) {
		this.refreshEnabled = refresh;
	}

	public boolean isRefreshEnabled() {
		return refreshEnabled;
	}

	public void setImageView(ImageView imgV) {
		this.imgView = imgV;
	}

	public int[] getPixels() {
		return this.pixels;
	}

	public ProtocolPreferences getPrefs() {
		return this.prefs;
	}

	public Recording getRecording() {
		return recording;
	}

	public byte[] getHextile_bg_encoded() {
		return hextile_bg_encoded;
	}

	public byte[] getHextile_fg_encoded() {
		return hextile_fg_encoded;
	}

	/**
	 * Painting of the bitmap is done here. After getting Bitmap from Bitmap
	 * containing, canvas is created on this bitmap annotations and
	 * highlightSearchResults are painted on this canvas
	 * 
	 * @param setBitmap
	 *            determines if bitmap has to be assign to imageView or not. It
	 *            has assign if the layout changed and a new imageView was
	 *            assigned to the graphicsContext. Otherwise the imageView just
	 *            has to be invalidate
	 * 
	 * 
	 */
	public void updateView(boolean setBitmap) {
		
		if (refreshEnabled) {
			// get Bitmap without Annotations or highlighted search results
			final Bitmap bitmap = bitmapContainer.getBitmap();
			// define canvas, which draws in the bitmap
			Canvas canvas = new Canvas(bitmap);

			if (isWhiteboardEnabled())
				paintWhiteboard(canvas);
			paintAnnotations(canvas);
			recording.highlightSearchResults(canvas);
			if (setBitmap)
				imgView.post(new Runnable() {

					@Override
					public void run() {
						// assign bitmap to imageview in UI-Thread
						imgView.setImageBitmap(bitmap);

					}
				});
			else
				// invalidate ImageView in UI-Thread
				imgView.postInvalidate();

		}
	}

	/**
	 * decoding Color from received colorfield of Hextile Message Copy from the
	 * TTTAndroidRecorder-App
	 * 
	 * @param colorField
	 *            colorField from Hextile Message
	 * @return int-value of decoded ARGB-color
	 */
	public int decodeColor(byte[] colorField) {
		int color = (colorField[1] & 0xFF) << 8 | (colorField[0] & 0xFF);

		int red = color;
		int green = color;
		int blue = color;

		red = red & 0x1f;
		green >>= 5;
		green = green & 0x1f;
		blue >>= 10;
		blue = blue & 0x3f;

		return Color.argb(255, 255 / 31 * red, 255 / 31 * green,
				255 / 63 * blue);

	}

	/**
	 * setter of background an foreground colors of hextile messages
	 */
	public void setForeground(byte[] color, int offset) {
		System.arraycopy(color, offset, hextile_fg_encoded, 0,
				prefs.bytesPerPixel);
	}

	public void setBackground(byte[] color, int offset) {
		System.arraycopy(color, offset, hextile_bg_encoded, 0,
				prefs.bytesPerPixel);
	}

	/**
	 * updateView if messages is an Annotation
	 */
	@Override
	public void handleMessage(Message message) {
		
		message.paint(this);
		updateView(false);

	}

	/**
	 * handle updated pixel of framebuffer params not needed, but maybe in
	 * future. params describe a rectangle of the update area.
	 * 
	 * @param x
	 *            x-coordinate of upper left corner
	 * @param y
	 *            y-cordinate of upper left corner
	 * @param w
	 *            width of rectangle
	 * @param h
	 *            height of rectangle
	 */
//	public void handleUpdatedPixels(int x, int y, int w, int h) {
//
//		// if recording is adjusting, messages are not painted one by one but
//		// only the final image
//		if (!recording.adjusting) {
//
//			updateView(false);
//		}
//	}

	/*******************************************************************************************************************
	 * Annotations *
	 ******************************************************************************************************************/

	private final ArrayList<Annotation> currentAnnotations = new ArrayList<Annotation>();

	// add annotations to annotation list
	synchronized public void addAnnotation(Annotation annotation) {
		currentAnnotations.add(annotation);
	}

	// remove all annotations
	synchronized public void clearAnnotations() {
		currentAnnotations.clear();
	}

	// find and remove annotations at given coordinates
	synchronized public void removeAnnotationsAt(int x, int y) {
		int i = 0;
		while (i < currentAnnotations.size()) {
			if (currentAnnotations.get(i).contains(x, y))
				currentAnnotations.remove(i);
			else
				i++;
		}
	}

	// MODMSG
	public Annotation[] getCurrentAnnotationsAsArray() {
		Annotation[] annots = null;
		synchronized (currentAnnotations) {
			annots = new Annotation[currentAnnotations.size()];
			currentAnnotations.toArray(annots);
		}
		return annots;
	}

	// display all annotations
	synchronized public void paintAnnotations(Canvas canvas) {

		for (int i = 0; i < currentAnnotations.size(); i++) {
			currentAnnotations.get(i).paint(canvas);
		}

	}

	/**
	 * creates bitmap from the pixel area, fills out the bitmap with white color
	 * if whiteboard has to be shown
	 * 
	 * @return
	 */
	public Bitmap createScreenshotWithoutAnnotations() {

		// create new mutable bitmap

		Bitmap screenshot = Bitmap.createBitmap(prefs.framebufferWidth,
				prefs.framebufferHeight, Config.ARGB_4444);

		// show blank page if whiteboard activated
		if (isWhiteboardEnabled()) {
			Canvas canvas = new Canvas(screenshot);
			paintWhiteboard(canvas);

		} else {
			screenshot.setPixels(pixels, 0, prefs.framebufferWidth, 0, 0,
					prefs.framebufferWidth, prefs.framebufferHeight);
		}
		return screenshot;

	}

	/*******************************************************************************************************************
	 * WhiteBoard *
	 ******************************************************************************************************************/

	// whiteboard (blank page for annotations)
	protected int whiteboardPage;

	public boolean isWhiteboardEnabled() {
		return whiteboardPage > 0;
	}

	// set whiteboard page and corresponding annotion buffer
	public void setWhiteboardPage(int whiteboardPage) {
		this.whiteboardPage = whiteboardPage;
		clearAnnotations();
		updateView(false);
	}

	// updates for late comers and recorders
	public Message getCurrentWhiteboardMessage() {
		return new WhiteboardMessage(0, whiteboardPage, prefs);
	}

	// fill the canvas with white color and set the page number
	public void paintWhiteboard(Canvas canvas) {

		canvas.drawColor(Color.WHITE);

		Paint p = new Paint(Color.BLACK);
		p.setTextSize(20);
		canvas.drawText("#" + whiteboardPage, prefs.framebufferWidth - 30, 20,
				p);

	}

	public void setBitmap() {
		imgView.post(new Runnable() {

			@Override
			public void run() {
				imgView.setImageBitmap(bitmapContainer.getBitmap());

			}
		});
	}
}