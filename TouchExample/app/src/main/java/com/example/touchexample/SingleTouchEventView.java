package com.example.touchexample;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class SingleTouchEventView extends View 
{
	// used to remember the lines that have already been drawn
	List<ColouredLine> listOfLinesToDraw = new ArrayList<ColouredLine>();

	// rectangles to drawn on the screen
	RectF redRectangle = new RectF(0, 0, 100, 100);
	RectF greenRectangle = new RectF(100, 0, 200, 100);
	RectF blueRectangle = new RectF(200, 0, 300, 100);

	// Paints are like a brush, where we use brush for a specific colour - you don't need to change this
	private Paint paint = new Paint();
	private Paint old_paint = new Paint();
	private Paint box_paint = new Paint();
	private Paint numbers_paint = new Paint();

	private Path path = new Path();

	// this is where we have clicked the screen - you don't need to change this
	float eventX;
	float eventY;

	boolean isTouched = false;

	/**
	 * Start up the touch screen - you don't need to change this
	 * @param context
	 * @param attrs
	 */
	public SingleTouchEventView(Context context, AttributeSet attrs) {
		super(context, attrs);

		SetupPaint(paint);
		SetupPaint(old_paint);
		SetupPaint(box_paint);
		SetupPaint(numbers_paint);

		numbers_paint.setStrokeWidth(2f);
		numbers_paint.setStyle(Paint.Style.FILL);

		// make it fill the box, stroke = draw edge, fill = fill the box
		box_paint.setStyle(Paint.Style.FILL_AND_STROKE);

		// set the starting colour
		SetColourAndWidth( Color.BLACK , 4.0F );
	}

	/**
	 * Sets up the brushes - you don't need to change this
	 * @param paint
	 */
	void SetupPaint( Paint paint )	
	{
		paint.setAntiAlias(true);
		paint.setStrokeWidth(4f);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setTextSize(50);
	}

	/**
	 * Start a new line of the new colour - you don't need to change this
	 * @param color
	 */
	public void SetColourAndWidth( int color, float width )
	{
		path = new Path();
		ColouredLine colouredLine = new ColouredLine( path, color, width );
		listOfLinesToDraw.add(colouredLine);

		paint.setColor(color);
		invalidate();
	}

	/**
	 * Clear the display and set line colour back to black - you don't need to change this
	 */
	public void ClearDisplay()
	{
		listOfLinesToDraw.clear();		
		SetColourAndWidth(Color.BLACK, 4.0F );		
	}

	/**
	 * Check if the point touched is within the box - you don't need to change this
	 * @param x
	 * @param y
	 * @param box
	 * @return
	 */
	public Boolean IsWithinBox( float x, float y , RectF box )
	{
		// check horizontally
		if ( x < box.left) return false;
		if ( x > box.right ) return false;

		// check vertically
		if ( y < box.top) return false;
		if ( y > box.bottom ) return false;

		return true;
	}

	private void DrawEverythingElse( Canvas canvas)
	{		
		// for all the lines in the list draw then to the screen - leave this bit alone
		for( ColouredLine p: listOfLinesToDraw )
		{
			old_paint.setColor(p.color);
			old_paint.setStrokeWidth(p.width);
			canvas.drawPath(p.line, old_paint);
		}

		// if the screen is touched draw a circle at the touch point, how could you chnage this?
		if ( isTouched == true )
		{
			canvas.drawCircle(eventX, eventY, 50, paint);
		}

		// draw to the screen the coordinates where we have touched the screen
		canvas.drawText((int) eventX + " " + (int) eventY, this.getWidth() - 230 , 50 , numbers_paint);
	}

	@Override
			protected void onDraw(Canvas canvas) {
		//draw blue rectangle
		box_paint.setColor(Color.BLUE);
		canvas.drawRect(blueRectangle, box_paint);

		// draw the red rectangle
		box_paint.setColor(Color.RED);
		canvas.drawRect(redRectangle, box_paint);

		// draw the green rectangle
		box_paint.setColor(Color.GREEN);
		canvas.drawRect(greenRectangle, box_paint);


		DrawEverythingElse(canvas);

	}






	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// get the points where you have touched the screen
		eventX = event.getX();
		eventY = event.getY();

		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:

			isTouched = true;

			if ( eventY < 100 & eventX < 200 & eventX > 100)
			{
				SetColourAndWidth( Color.GREEN, 12.0F );
			}

			if ( eventY < 100 & eventX < 100 & eventX > 0)
			{
				SetColourAndWidth( Color.RED, 12.0F );
			}

			path.moveTo(eventX, eventY);
			return true;

		case MotionEvent.ACTION_MOVE:
			path.lineTo(eventX, eventY);
			break;

		case MotionEvent.ACTION_UP:
			// nothing to do
			isTouched = false;
			break;
		default:
			return false;
		}

		// Redraw the screen
		invalidate();
		return true;
	}
} 
