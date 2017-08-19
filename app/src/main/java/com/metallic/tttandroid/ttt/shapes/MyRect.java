package com.metallic.tttandroid.ttt.shapes;

import android.graphics.Canvas;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Rect;

/**
 * @see MyShape
 * @author Thomas Krex
 * 
 */
public class MyRect extends MyShape {
	private final Rect rect;

	public MyRect(Rect rect, int color, Style style, float strokeWidth,
			Cap cap, Join join) {
		super(color, style, strokeWidth, cap, join);
		this.rect = rect;

	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawRect(rect, paint);
	}
}
