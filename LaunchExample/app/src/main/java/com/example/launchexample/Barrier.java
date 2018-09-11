package com.example.launchexample;

import android.graphics.RectF;

/**
 * Created by QQ STEM on 30/07/2017.
 */

public class Barrier {
    private double left;
    private double top;
    private double width;
    private double height;

    ///Left, Top
    public Barrier(double left, double top, double width, double height)
    {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
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
