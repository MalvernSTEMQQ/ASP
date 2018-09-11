package com.example.launchexample;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;



public class ProjectileSimulation
{
    public double springLengthChange;

    private double TIMESTEP = 0.1;
    private double screenHeight;
    private double screenWidth;

    public ProjectileSimulation(double springChange)
    {
        this.springLengthChange = springChange;
    }

    public Projectile GenerateStartConditions(Projectile projectile, Launcher launcher, double screenHeight, double screenWidth, double gravityAcceleration)
    {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        double speed = springLengthChange * Math.sqrt((3 * launcher.SpringConstant())/projectile.getMass());
        double releaseAngle = (Math.PI)/2 - launcher.BraceAngle();
        double beamLength = launcher.BeamLength();
        double releaseAltitiude = beamLength * Math.sin(launcher.BraceAngle()); //from base
        double releaseXPosition = beamLength * (1 - Math.cos(launcher.BraceAngle()));

        double x = releaseXPosition;
        double y = screenHeight - releaseAltitiude;
        double vx = speed * Math.cos(releaseAngle);
        double vy = - speed * Math.sin(releaseAngle);
        double ax = 0;
        double ay = gravityAcceleration;

        projectile.SetLaunchConditions(x,y,vx,vy,ax,ay);

        return projectile;
    }

    public LocationVector ReturnInitialLocation(Projectile projectile, Launcher launcher, double screenHeight, double screenWidth, double gravityAcceleration)
    {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        double speed = springLengthChange * Math.sqrt((3 * launcher.SpringConstant())/projectile.getMass());
        double releaseAngle = (Math.PI)/2 - launcher.BraceAngle();
        double beamLength = launcher.BeamLength();
        double releaseAltitiude = beamLength * Math.sin(launcher.BraceAngle()); //from base
        double releaseXPosition = beamLength * (1 - Math.cos(launcher.BraceAngle()));

        double x = releaseXPosition;
        double y = screenHeight - releaseAltitiude;
        double vx = speed * Math.cos(releaseAngle);
        double vy = - speed * Math.sin(releaseAngle);
        double ax = 0;
        double ay = gravityAcceleration;

        return new LocationVector(x,y,vx,vy,ax,ay);
    }




    public Projectile GenerateLocations(Projectile projectile)
    {
        double time = 0; //useful? add to LocationVector class
        int iter = 0;

        boolean simRunning = true;
        double initHeight = projectile.GetCurrentLocation(0).y;

        while(simRunning) //no exit conditions //when height == height 0
        {
            time += TIMESTEP;
            LocationVector currentLoc = projectile.GetCurrentLocation(iter);

            double x = currentLoc.x + currentLoc.vx * TIMESTEP;
            double y = currentLoc.y + currentLoc.vy * TIMESTEP;

            double vx = currentLoc.vx + currentLoc.ax * TIMESTEP;
            double vy = currentLoc.vy + currentLoc.ay * TIMESTEP; //check this maths c/w pythonic stuff
            double ax = currentLoc.ax;
            double ay = currentLoc.ay;


            LocationVector newLocation = new LocationVector(x, y, vx, vy, ax, ay);
            projectile.AddPositionData(newLocation);

            if((y >= screenHeight) || (x <= 0) || (x >= screenWidth && y >= 0))
            {
                simRunning = false;
            }

            iter++;
        }
        return projectile;
    }

    public Boolean CheckLocations(Projectile projectile)
    {
        double time = 0; //useful? add to LocationVector class
        int iter = 0;

        boolean simRunning = true;
        boolean success = false;
        double initHeight = projectile.GetCurrentLocation(0).y;

        while(simRunning) //no exit conditions //when height == height 0
        {
            time += TIMESTEP;
            LocationVector currentLoc = projectile.GetCurrentLocation(iter);

            double x = currentLoc.x + currentLoc.vx * TIMESTEP;
            double y = currentLoc.y + currentLoc.vy * TIMESTEP;

            double vx = currentLoc.vx + currentLoc.ax * TIMESTEP;
            double vy = currentLoc.vy + currentLoc.ay * TIMESTEP; //check this maths c/w pythonic stuff
            double ax = currentLoc.ax;
            double ay = currentLoc.ay;


            LocationVector newLocation = new LocationVector(x, y, vx, vy, ax, ay);
            projectile.AddPositionData(newLocation);

            if(y >= screenHeight)// || x >= screenWidth)
            {
                success = false;
                simRunning = false;
            }
            else if(x >= screenWidth)
            {
                success = true;
                simRunning = false;
            }

            iter++;
        }
        return success;
    }


    public int CheckForCollisions(Projectile projectile, List<Barrier> barriers)
    {
        int collisionIndex = -1;
        int positionIndex = 0;
        Boolean prematureCollision = false;
        if(projectile.GetNumberOfLocations() <= 2)
        {
            collisionIndex = 0;
            prematureCollision = true;
        }
        for (LocationVector projectileLocation : projectile.GetLocations())
        {
            if(prematureCollision)
            {
                break;
            }
            for (Barrier barrier : barriers)
            {
                if (CollisionCalculator(projectileLocation, projectile.getRadius(), barrier)) {
                    prematureCollision = true;
                    collisionIndex = positionIndex;
                    break;
                }
            }

            positionIndex++;
        }

        return collisionIndex;
    }


    private Boolean CollisionCalculator(LocationVector location, int radius, Barrier barrier)
    {

        Boolean collided = barrier.IncludesLocation(location.x, location.y, radius);

        return collided;
    }

    public void setSpringLengthChange(double slc)
    {
        this.springLengthChange = slc;
    }
}

