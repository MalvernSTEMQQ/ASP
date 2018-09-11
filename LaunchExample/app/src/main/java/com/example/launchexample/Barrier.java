package com.example.launchexample;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;


/**
 * Barrier Types:
 * 1 = End zone
 * 2 = Solid
 * 3 = Vertical Boost
 *
 */
public class Barrier {
    private double left;
    private double top;
    private double width;
    private double height;
    private int barrierType;
    private Paint barrierColorInside;
    private Paint barrierColorOutside;

    ///Left, Top
    public Barrier(double left, double top, double width, double height, int type)
    {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        this.barrierType = type;
        this.barrierColorInside = new Paint();
        this.barrierColorOutside = new Paint();
        SetBarrierColors();
    }

    public Paint GetInsideColor()
    {
        return barrierColorInside;
    }

    public Paint GetOutsideColor()
    {
        return barrierColorOutside;
    }


    private void SetBarrierColors()
    {
        switch(barrierType)
        {
            case 1:
                barrierColorInside.setColor(Color.LTGRAY);
                barrierColorOutside.setColor(Color.LTGRAY);
                break;
                //end zone
            case 2:
                barrierColorInside.setColor(Color.GRAY);
                barrierColorOutside.setColor(Color.BLACK);
                break;
                //solid walls
            case 3:
                barrierColorInside.setColor(Color.BLUE);
                barrierColorOutside.setColor(Color.BLACK);
                //other barriers
        }
    }


    public int GetType()
    {
        return barrierType;
    }

    public RectF GetBarrierShape()
    {
        RectF barrierRect = new RectF((float)left, (float)top, (float)left + (float)width, (float)top + (float)height);

        return barrierRect;
    }

    public boolean IncludesLocation(double x, double y, int radius)
    {
        boolean inside = false;


        //main body
        if (x > left && x < left + width)
        {
            if (y > top - (double) radius && y < top + height + (double) radius)
            {
                inside = true;
            }
        }
        else if (x > left - (double) radius && x < left + width + (double) radius)
        {
            if (y > top && y < top + height)
            {
                inside = true;
            }
        }

        //checks the corner regions
        if (Hypotenuse(top, left, x, y) <= (double) radius ||
                Hypotenuse(top, left + width, x, y) <= (double) radius ||
                Hypotenuse(top + height, left, x, y) <= (double) radius ||
                Hypotenuse(top + height, left + width, x, y) <= (double) radius)
        {
            inside = true;
        }

        return inside;
    }

    private double Hypotenuse(double y1, double x1, double x2, double y2)
    {
        return (Math.sqrt(Math.pow((x1-x2),2) + Math.pow((y1-y2),2)));
    }
}
