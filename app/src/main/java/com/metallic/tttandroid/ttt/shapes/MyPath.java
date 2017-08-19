package com.metallic.tttandroid.ttt.shapes;

import android.graphics.Canvas;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;

/**
 * @see MyShape
 * @author Thomas Krex
 * 
 */
public class MyPath extends MyShape {
	public Path path;

	public MyPath(Path path, int color, Style style, float strokeWidth,
			Cap cap, Join join) {
		super(color, style, strokeWidth, cap, join);
		this.path = path;
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawPath(path, paint);

	}

}
