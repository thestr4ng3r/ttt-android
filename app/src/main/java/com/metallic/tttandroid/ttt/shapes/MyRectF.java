package com.metallic.tttandroid.ttt.shapes;

import android.graphics.Canvas;
import android.graphics.Paint.Style;
import android.graphics.RectF;

/**
 * @see MyShape
 * @author Thomas Krex
 * 
 */
public class MyRectF extends MyShape {
	private final RectF rectF;

	public MyRectF(RectF rectF, int color, Style style) {
		super(color, style);
		this.rectF = rectF;

	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawRect(rectF, paint);
	}

}
