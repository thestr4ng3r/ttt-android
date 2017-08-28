package com.metallic.tttandroid.ttt.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.metallic.tttandroid.ttt.messages.annotations.Annotation;
import com.metallic.tttandroid.ttt.messages.annotations.HighlightAnnotation;
import com.metallic.tttandroid.ttt.shapes.MyRectF;
import com.metallic.tttandroid.ttt.utils.BitmapContainer;

/**
 * This class represents a index entry of the recording. It contains the
 * corresponding timestamp, the seacrhbasentries and the thumbnail. This Class
 * extends from ImageButton, so you add a IndexEntry to the ScrollView for
 * displaying the Thumbnail. Methods adopted from TTT will be marked with (TTT).
 * 
 * @author Thomas Krex
 * 
 */
@SuppressLint({ "DefaultLocale", "ViewConstructor" })
public class IndexEntry {
	private final int timestamp;
	private final Index index;
	private BitmapContainer bitmapContainer;
	@SuppressWarnings("unused")
	private String title = "";
	private String searchbase_of_page;
	private ArrayList<SearchBaseEntry> words;
	private ArrayList<Annotation> annotations;
	public static double THUMBNAIL_SCALE_FACTOR = 1;
	public static boolean SCALE_FACTOR_CALCULATED = false;

	private boolean hasThumbnail = false;

	public IndexEntry(final Index index, String title,
			int timestamp, String searchableText) {
		this.index = index;
		this.timestamp = timestamp;
		this.title = title;
		this.searchbase_of_page = searchableText;

		/*this.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				index.getRecording().setTime(IndexEntry.this.timestamp, true);

			}
		});*/
	}

	public IndexEntry(Index index) {
		this(index, "", 0, null);
	}

	public IndexEntry(Index index, int timestamp) {
		this(index, "", timestamp, null);
	}

	public int getTimestamp() {
		return timestamp;
	}

	/**
	 * assign thumbanil to the ImageButton
	 * 
	 * @param thumbnail
	 */
	public void setThumbnail(Bitmap thumbnail) {
		// set
		// create bitmap container only once
		if (!hasThumbnail) {
			bitmapContainer = new BitmapContainer(thumbnail);
			hasThumbnail = true;
		}

		updateThumbail();
	}

	/**
	 * paints annotations and searchBaseEntries on the bitmap and invalidates
	 * the ImageButton
	 */
	public void updateThumbail() {
		final Bitmap bitmap = bitmapContainer.getBitmap();
		Canvas canvas = new Canvas(bitmap);
		highlightSearchResults(canvas);
		paintAnnotationsToThumbnail(canvas);

		//invalidate();
	}

	public Bitmap getBitmap() {
		return bitmapContainer.getBitmap();
	}

	public void setAnnotations(ArrayList<Annotation> annotations) {
		this.annotations = new ArrayList<Annotation>(annotations);
	}

	public boolean hasThumbnail() {
		return hasThumbnail;
	}

	/**
	 * read words and coordinates for this index(TTT)
	 * 
	 * @param in
	 *            input stream of the recording
	 * @param ratio
	 *            given ratio
	 * @throws IOException
	 */
	public void readSearchbase(DataInputStream in, double ratio)
			throws IOException {
		// TODO: maybe compare or overwrite

		ArrayList<SearchBaseEntry> words = new ArrayList<SearchBaseEntry>();
		int number_of_words = in.readShort();

		for (int i = 0; i < number_of_words; i++)
			words.add(SearchBaseEntry.read(in, ratio));

		// now set words
		// NOTE: setting all at once will update searchbase of Index Extension
		// (used for backward compatibility)
		setSearchbase(words);
	}

	/**
	 * set ASCII searchbase(TTT)
	 * 
	 * @param searchbase
	 */
	public void setSearchbase(String searchbase) {
		// TODO: umlaut reduction
		this.searchbase_of_page = searchbase;
	}

	/**
	 * set searchbase with per word coordinates (from XML)(TTT)
	 * 
	 * @param words
	 */
	public void setSearchbase(ArrayList<SearchBaseEntry> words) {
		this.words = words;

		// overwrite searchbase string for page
		String page_string = "";
		for (int i = 0; i < words.size(); i++)
			page_string += words.get(i).searchText + " ";
		setSearchbase(page_string);
	}

	/**
	 * perform search for the index entry(TTT)
	 * 
	 * @param searchword
	 *            string of search query
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public boolean contains(String searchword) {
		// empty search
		if (searchword == null || searchword.equals("")) {
			results.clear();
			return false;
		}

		// ASCII searchbase
		if (words == null)
			return searchbase_of_page.toLowerCase().indexOf(
					searchword.toLowerCase()) >= 0;

		// XML searchbase
		else
			return getSearchResults(searchword);
	}

	private final ArrayList<MyRectF> results = new ArrayList<MyRectF>();

	/**
	 * perform advanced search (XML searchbase with coordinates)(TTT)
	 * 
	 * @param searchword
	 * @return
	 */
	private boolean getSearchResults(String searchword) {
		results.clear();

		if (words != null) {
			for (int i = 0; i < words.size(); i++)
				words.get(i).contains(searchword, results);
		}

		// any result found?
		return results.size() > 0;
	}

	/**
	 * draws the shapes of the search results on the given canvas
	 * 
	 * @param canvas
	 *            canvas to draw on
	 */
	public void highlightSearchResults(Canvas canvas) {
		if (results != null)
			for (int i = 0; i < results.size(); i++) {
				MyRectF shape = results.get(i);
				shape.draw(canvas);

			}

	}

	/**
	 * paints the annotation on the canvas of thumbnail if enabled. The
	 * difference to the paintAnnotation(Canvas) of the GraphicsContext is, that
	 * some annotations have their own paintToThumbnail() -method. This only
	 * influences the stroke width of an annotation
	 * 
	 * @param canvas
	 */
	void paintAnnotationsToThumbnail(Canvas canvas) {
		boolean paintHighlightAnnotations = index.getAnnotationsPaintMode() == Index.PAINT_ALL_ANNOTATIONS;

		if (annotations != null) {
			if (index.getAnnotationsPaintMode() != Index.PAINT_NO_ANNOTATIONS)
				for (int i = 0; i < annotations.size(); i++) {
					Annotation annotation = annotations.get(i);
					if (paintHighlightAnnotations
							|| !(annotation instanceof HighlightAnnotation)) {
						annotation.paintToThumbnail(canvas);
					}
				}
		}
	}
}
