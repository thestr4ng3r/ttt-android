package com.metallic.tttandroid.ttt.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;

/**
 * helper class for painting shapes. It stores all required information and
 * provides a own draw method
 * 
 * @author Thomas Krex
 * 
 */
public abstract class MyShape {

	final Paint paint;

	public MyShape(int color, Style style) {
		paint = new Paint();
		paint.setColor(color);
		paint.setStyle(style);

	}

	public MyShape(int color, Style style, float strokeWidth, Cap cap, Join join) {
		this(color, style);
		paint.setStrokeWidth(strokeWidth);
		paint.setStrokeCap(cap);
		paint.setStrokeJoin(join);

	}

	public abstract void draw(Canvas canvas);

}
