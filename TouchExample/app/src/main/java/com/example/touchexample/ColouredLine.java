package com.example.touchexample;

import android.graphics.Color;
import android.graphics.Path;

public class ColouredLine 
{
	public Path line;
	
	public int color;
	
	public float width;
	
	
	public ColouredLine( Path newLine, int newColour, float newWidth  )
	{
		line = newLine;
		
		color = newColour;
		
		width = newWidth;
	}
}
