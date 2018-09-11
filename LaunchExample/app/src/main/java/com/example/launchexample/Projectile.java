package com.example.launchexample;

/**
 * Created by QQ STEM on 30/07/2017.
 */

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class Projectile {
    private double mass = 1; //by default

    private int radius = 30;
    private  List locations;

    public int getRadius()
    {
        return this.radius;
    }


    public Projectile()
    {
        locations = new ArrayList<LocationVector>();
    }

    public Projectile(double mass)
    {
        locations = new ArrayList<LocationVector>();
        if(mass == 0 )
        {
            this.mass = 1000;
        }
        else if(mass < 0.0001)
        {
            this.mass = 0.0001;
        }
        else
        {
            this.mass = mass;
        }
    }

    public void setMass(double mass)
    {
        if(mass == 0 )
        {
            this.mass = 1000;
        }
        else if(mass < 0.0001)
        {
            this.mass = 0.0001;
        }
        else
        {
            this.mass = mass;
        }

    }

    public double getMass()
    {
        return this.mass;
    }

    public List<LocationVector> GetLocations()
    {
        return locations;
    }

    public LocationVector GetCurrentLocation(int i)
    {
        LocationVector currentLocation = (LocationVector)locations.get(i);
        return currentLocation;
    }

    public void AddPositionData(LocationVector location)
    {
        LocationVector tempLocation = location;
        locations.add(tempLocation);
    }

    public int GetNumberOfLocations(){return locations.size();}


    public void UpdateStartLocation(double height)
    {
        this.GetCurrentLocation(0).y = height - this.GetCurrentLocation(0).y;
    }

    public void SetLocations(List<LocationVector> locations)
    {
        this.locations = locations;
    }

    public void AddPositionData(double x, double y, double vx, double vy, double ax, double ay)
    {
        LocationVector tempLocation = new LocationVector(x,y,vx,vy,ax,ay);
        locations.add(tempLocation);
    }

    public void SetLaunchConditions(double x, double y, double vx, double vy, double ax, double ay)
    {
//        locations.clear();
//        LocationVector firstLocation = new LocationVector(x, y, vx, vy, ax, ay);
//        locations.add(firstLocation);
        SetLaunchConditions(new LocationVector(x,y,vx,vy,ax,ay));
    }

    public void SetLaunchConditions(LocationVector firstLocation)
    {
        locations.clear();
        locations.add(firstLocation);
    }
}
