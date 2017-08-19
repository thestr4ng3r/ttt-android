package com.metallic.tttandroid.ttt.utils;

import android.graphics.Bitmap;

/**
 * Helper class that stores the pixel of the FrameBuffer
 * 
 * @author Thomas Krex
 * 
 */
public class BitmapContainer
{
	private final Bitmap image;
	private final int width, height;
	private final int[] pixels;

	public BitmapContainer(Bitmap _source) {
		image = _source;
		width = image.getWidth();
		height = image.getHeight();
		pixels = new int[width * height];
		image.getPixels(pixels, 0, width, 0, 0, width, height);
	}

	public int getPixel(int x, int y) {
		return pixels[x + y * width];
	}

	public void setPixel(int x, int y, int color) {
		pixels[x + y * width] = color;
	}

	/**
	 * Set the pixel of the Bitmap and returns it
	 * 
	 * @return bitmap without any overlays
	 * 
	 */
	public Bitmap getBitmap() {
		image.setPixels(pixels, 0, width, 0, 0, width, height);
		return image;
	}

	public int getWidth() {
		return image.getWidth();
	}

	public int getHeight() {
		return image.getHeight();
	}

	public int[] getPixels() {
		return pixels;
	}
}
