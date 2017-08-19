package com.metallic.tttandroid.ttt.shapes;

import android.graphics.Canvas;
import android.graphics.Paint.Style;

/**
 * @see MyShape
 * @author Thomas Krex
 * 
 */
public class MyLine extends MyShape {

	private int startx, starty, endx, endy;

	public MyLine(int startx, int starty, int endx, int endy, int color,
			Style style, float strokeWidth) {
		super(color, style);
		paint.setStrokeWidth(strokeWidth);

	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawLine(startx, starty, endx, endy, paint);

	}

}
