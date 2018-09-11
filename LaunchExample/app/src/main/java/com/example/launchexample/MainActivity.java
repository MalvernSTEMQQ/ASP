package com.example.launchexample;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new MyView(this));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

    }
 

    class MyView extends View {

        private int currentLevel;
        private ArrayList<ArrayList<Barrier>> barrierLevels;

        //setup variables
        public Paint innerPaint;
        public Paint outerPaint;
        public Paint textPaint;
        public Paint innerButtonPaint;
        public Paint outerButtonPaint;
        public Paint innerProjPaint;
        public Paint outerProjPaint;
        public Paint predicterPaint;
        public Paint catapultPaint;

        public String successString;
        public String failureString;
        public String completionString;


        private double percentage;

        private int w;
        private int h;

        private int maxLevel = 0;

        private float textSize = 0f;

        private float[] catapultLine;
        private Path projectedLine;

        boolean fire = false;
        boolean load = false;

        private int it = 0;
        private int failureIndex = -2;
        private ArrayList<Barrier> barriers;





        //HOW DO CHANGING THESE AFFECT THINGS

        public double springConstant = 34;
        public double braceAngle = 65;
        public double beamLength = 100;
        public double springLengthChange = 10;

        private double gravityAcceleration = 9.81;






        String messageString;

        RectF LoadButton;
        RectF FireButton;

        RectF AngleUpButton;
        RectF AngleDownButton;
        RectF SpringLengthUpButton;
        RectF SpringLengthDownButton;

        double mass;

        Map<String, RectF> buttonMap = new HashMap<String,RectF>();

        //initialise Simulation classes
        Launcher catapult = new Launcher(validator(springConstant,1,1000),
                validator(braceAngle,0,90),validator(beamLength,1,500));
        Projectile projectile = new Projectile();

        BarrierList barrierList = new BarrierList();



        ProjectileSimulation ps = new ProjectileSimulation(PowerScaleUp(validator(springLengthChange,1,1000)));
        private int radius = projectile.getRadius();



        public double validator(double variable, double min, double max)
        {
            return Math.max(Math.min(variable,max),min);
        }





        public MyView(Context context)
        {
            super(context);


            //SET UP PAINTS
            innerPaint = new Paint();
            innerPaint.setColor(Color.GRAY);

            outerPaint = new Paint();
            outerPaint.setColor(Color.BLACK);

            textPaint = new Paint();
            textPaint.setColor(Color.BLACK);

            //currently these Button paints are identical to the other inner/outer paints. How could you change the colour of just the Buttons?
            innerButtonPaint = new Paint(innerPaint);
            innerButtonPaint.setColor(Color.rgb(192,192,192));
            outerButtonPaint = new Paint(outerPaint);

            innerProjPaint = new Paint(innerPaint);
            innerProjPaint.setColor(Color.rgb(64,43,253));
            outerProjPaint = new Paint(outerPaint);

            predicterPaint = new Paint();
            predicterPaint.setColor(Color.RED);
            predicterPaint.setStyle(Paint.Style.STROKE);
            predicterPaint.setStrokeWidth(3);
            predicterPaint.setPathEffect(new DashPathEffect(new float[] {10,10},0));

            catapultPaint = new Paint();
            catapultPaint.setColor(Color.BLACK);
            catapultPaint.setStrokeWidth(5);



            //WHAT HAPPENS IF THIS IS CHANGED??
            projectile.setMass(1);


            //SET UP STRINGS
            successString = "Congratulations - Level Complete";
            failureString = "Collision - Level Failed";
            completionString = "Congratulations - All Levels Completed!";


            mass = projectile.getMass();
        }




        @Override
        public boolean onTouchEvent(MotionEvent event)
        {
            float eventX = event.getX();
            float eventY = event.getY();

            switch (event.getAction())
            {
                //Boolean valueChanged = false;
                case MotionEvent.ACTION_DOWN:
                    Boolean valueChanged = false;

                    if(InsideRectangle( buttonMap.get("LoadButton"), eventX, eventY))
                    {
                        LoadProjectile2();
                    }
                    else if(InsideRectangle(buttonMap.get("FireButton"), eventX, eventY))
                    {
                        FireProjectile();
                    }
                    else if(InsideRectangle(buttonMap.get("AngleUpButton"), eventX, eventY))
                    {
                        //angle up

                        if (braceAngle < 90)
                        {
                            braceAngle = braceAngle + 1;
                            valueChanged = true;
                            braceAngle = catapult.UpdateBraceAngle(braceAngle);
                        }

                    }
                    else if(InsideRectangle(buttonMap.get("AngleDownButton"), eventX, eventY))
                    {
                        //angle down

                        if(braceAngle > 0)
                        {
                            braceAngle = braceAngle - 1;
                            valueChanged = true;
                            braceAngle = catapult.UpdateBraceAngle(braceAngle);
                        }
                    }
                    else if(InsideRectangle(buttonMap.get("SpringLengthUpButton"), eventX, eventY))
                    {
                        //spring length up - POWER UP

                        springLengthChange = springLengthChange + 1;

                        ps.springLengthChange = PowerScaleUp(springLengthChange);
                        valueChanged = true;

                    }
                    else if(InsideRectangle(buttonMap.get("SpringLengthDownButton"), eventX, eventY))
                    {
                        //spring length down - POWER DOWN

                        if(springLengthChange > 1)
                        {
                            springLengthChange = springLengthChange - 1;
                            valueChanged = true;
                            ps.springLengthChange = PowerScaleUp(springLengthChange);
                        }

                    }



                    if (valueChanged == true)
                    {
                        setCatapultLine();
                    }

                    break;
            }
            return true;
        }



        //on canvas draw - restructuring
        protected void onDraw(Canvas c)
        {
            int w = getWidth();
            int h = getHeight();

            if(textSize == 0)
            {
                textSize = textPaint.getTextSize();
                textSize *=3;
                textPaint.setTextSize(textSize);
            }

            //region Draw Objects

            //draw Barriers
            for(int i = 0; i < barriers.size(); i++)
            {
                Barrier barrier = barriers.get(i);
                drawBarrier(c, barrier);
            }

            //draw Catapult Line
            c.drawLines(catapultLine, catapultPaint);
            c.drawPath(projectedLine, predicterPaint);

            //draw Buttons
            drawRect(c, LoadButton);
            c.drawText("Load", 440, h-65, textPaint);

            drawRect(c, FireButton);

            drawRect(c, AngleUpButton);
            drawRect(c, AngleDownButton);
            drawRect(c, SpringLengthUpButton);
            drawRect(c, SpringLengthDownButton);

            c.drawText("Angle", 1005, h - 115, textPaint);
            c.drawText("Power", 1130, h - 115, textPaint);

            c.drawText("Angle " + (int) braceAngle + "   Power " + (int) validator(springLengthChange,1,1000), 20, 80, textPaint);
            c.drawText("Level " + (currentLevel + 1) + "  Max Level " + maxLevel, 20, 120, textPaint);

            if(messageString != null)
            {
                c.drawText(messageString, 20, 160, textPaint);
            }
            //endregion

            //Boolean inMotion = true; //replaced by fire
            int collisionIndex;

            if(load)
            {
                c.drawText("Fire", 655, h-65, textPaint);
                drawCircle(c, projectile.GetFirstLocation(), radius, 2, innerProjPaint, outerProjPaint);
                c.drawText((int) projectile.GetFirstLocation().x + " " + (int) projectile.GetFirstLocation().y,20,40,textPaint);

                c.drawText(percentage + "%",20,200,textPaint);
            }
            if(fire)
            {
                load = false;

                //get new location
                LocationVector currentLocation = projectile.GetNextLocation();

                // check if in barrier
                collisionIndex = projectile.CheckForCollisions(currentLocation, barriers, radius);


                //ISSUE: Winning as soon as win zone is touched - regardless of whether it will then hit a barrier
                //for a glancing blow
                switch (collisionIndex) {
                    case 0:
                        //no collision, continue
                        break;

                    case 1:
                        //game won
                        fire = false;
                        messageString = successString;
                        maxLevel = currentLevel;
                        //if not at end of levels, go to next level
                        if (currentLevel != barrierLevels.size() - 1)
                        {
                            messageString = successString;

                            currentLevel++;
                        }
                        else //go to first level
                        {
                            messageString = completionString;
                            currentLevel = 0;
                        }

                        barriers = barrierLevels.get(currentLevel);

                        break;

                    case 2:
                        //solid collision
                        fire = false;
                        messageString = failureString;

                        break;
                    case 3:
                        //Vertical Boost
                        projectile.verticalBoost = true;
                        break;
                    default:
                        //shouldn't be anything else at the moment

                        break;
                }


                //draw circle

                drawCircle(c, currentLocation, radius, 2, innerProjPaint, outerProjPaint);
                c.drawText((int) currentLocation.x + " " + (int) currentLocation.y,20,40,textPaint);
            }




            postInvalidateDelayed(0);
        }



        //on canvas draw
        protected void onDraw1(Canvas c)
        {
            int w = getWidth();
            int h = getHeight();

            if(textSize == 0)
            {
                textSize = textPaint.getTextSize();
                textSize *= 3;
                textPaint.setTextSize(textSize);
            }

            //region Draw Objects

            //draw Barriers
            for (int i = 0; i < barriers.size(); i++)
            {
                Barrier barrier = barriers.get(i);
                drawBarrier(c, barrier);
            }


            //draw Catapult Line
            c.drawLines(catapultLine, catapultPaint);
            c.drawPath(projectedLine,predicterPaint);


            //draw Buttons
            drawRect(c,LoadButton);
            c.drawText("Load", 440, h-65, textPaint);

            drawRect(c,FireButton);
            //no fire text as it appears if fireable.

            drawRect(c,AngleUpButton);
            drawRect(c,AngleDownButton);
            drawRect(c,SpringLengthUpButton);
            drawRect(c,SpringLengthDownButton);

            c.drawText("Angle", 1005, h - 115, textPaint);
            c.drawText("Power", 1130, h - 115, textPaint);


            //add data to top left of screen
            c.drawText("Angle " + (int) braceAngle + "   Power " + (int) validator(springLengthChange,1,1000), 20, 80, textPaint);
            c.drawText("Level " + (currentLevel + 1) + "  Max Level " + maxLevel, 20, 120, textPaint);

            if(messageString != null)
            {
                c.drawText(messageString, 20, 160, textPaint);
            }
            //endregion



            if(load)
            {
                c.drawText("Fire", 655, h-65, textPaint);
                LocationVector initialLoc = projectile.GetCurrentLocation(0);

                drawCircle(c, initialLoc, radius,2, innerProjPaint, outerProjPaint);
                c.drawText((int) initialLoc.x + " " + (int) initialLoc.y, 20, 40, textPaint);
            }

            if(fire)
            {
                load = false;
                int numLocations = projectile.GetNumberOfLocations();
                if(it >= numLocations)//end of run
                {
                    //success
                    //put some logic to check end location reasonable
                    maxLevel = Math.max(maxLevel,currentLevel + 1);

                    if(currentLevel != barrierLevels.size() - 1)
                    {
                        messageString = successString;
                        currentLevel++;
                    }
                    else
                    {
                        messageString = completionString;
                        currentLevel = 0;
                    }
                    barriers = barrierLevels.get(currentLevel);
                    projectile = new Projectile(mass);
                    ps = new ProjectileSimulation(PowerScaleUp(springLengthChange));
                    fire=false;

                }
                else if(it == failureIndex)
                {
                    //break out here as crashed!
                    messageString = failureString;
                    projectile = new Projectile(mass);
                    ps = new ProjectileSimulation(PowerScaleUp(springLengthChange));
                    fire = false;
                }
                else {
                    //plot current location, keep going
                    LocationVector currentLoc = projectile.GetCurrentLocation(it);

                    drawCircle(c,currentLoc,radius,2, innerProjPaint, outerProjPaint);

                    c.drawText((int) currentLoc.x + " " + (int) currentLoc.y, 20, 40, textPaint);
                    it++;
                }
            }


            //WHAT DOES CHANGING THIS NUMBER DO
            postInvalidateDelayed(20);

        }

        private void setCatapultLine()
        {
            double theta = Math.toRadians(braceAngle);
            double initialX = beamLength;
            double initialY = h;
            double finalX = initialX * (1 - Math.cos(theta));
            double finalY = initialY - initialX * Math.sin(theta);
            double projectedExtension = 100/15;

            initialX += 10 * Math.cos(theta);
            initialY += 10 * Math.sin(theta);

            //double endX = finalX + beamLength * Math.sin(theta);
            //double endY = finalY - beamLength * Math.cos(theta);
            double endX = finalX + springLengthChange * Math.sin(theta) * projectedExtension;
            double endY = finalY - springLengthChange * Math.cos(theta) * projectedExtension;

            setCatapultLine(initialX,initialY,finalX,finalY);
            setProjectedLine(finalX,finalY,endX,endY);
        }


        //load projectile method for generating on the fly
        private void LoadProjectile2()
        {
            load = true;
            fire = false;

            double yHeight = getHeight();
            double xWidth = getWidth();


            //PercentageCompletable();

            projectile.SetFirstLocation(ps.ReturnInitialLocation(projectile, catapult, yHeight,xWidth,validator(gravityAcceleration, -10000,10000)));


        }

        //finds what percentage of start conditions can win the game
        private void PercentageCompletable()
        {
            double count = 0;
            double winCount = 0;
            double lossCount = 0;

            ProjectileSimulation testPS = new ProjectileSimulation(PowerScaleUp(springLengthChange));
            Projectile testProj = new Projectile(mass);

            Launcher testLauncher = new Launcher(34,1,100);

            for(int angle = 1; angle < 90; angle++)
            {
                testLauncher.UpdateBraceAngle(angle);
                for(int power = 1; power < 100; power++)
                {
                    count++;

                    testPS.setSpringLengthChange(PowerScaleUp(power));

                    testProj.SetFirstLocation(testPS.ReturnInitialLocation(testProj, testLauncher, getHeight(), getWidth(), validator(gravityAcceleration, -10000,10000)));

                    Boolean testFiring = true;
                    int testIndex;

                    //enter a model run through - basically do the calculations to win for each angle/power

                    while(testFiring)
                    {
                        LocationVector currentLocation = testProj.GetNextLocation();
                        testIndex = testProj.CheckForCollisions(currentLocation,barriers,radius);

                        switch(testIndex)
                        {
                            case 0:
                                //no collision, cont
                                break;
                            case 1:
                                //won
                                winCount++;
                                testFiring = false;
                                break;
                            case 2:
                                //solid collision
                                lossCount++;
                                testFiring = false;
                                break;
                            case 3:
                                //Vertical Boost
                                testProj.verticalBoost = true;
                                break;
                            default:
                                break;
                        }
                    }


                }
            }

            percentage = 100 * winCount/count;



        }

        private void LoadProjectile()
        {
            load = true;
            double yHeight = getHeight();
            double xWidth = getWidth();

            ps.GenerateStartConditions(projectile, catapult, yHeight, xWidth, validator(gravityAcceleration,-10000,10000));
            ps.GenerateLocations(projectile);

            failureIndex = ps.CheckForCollisions(projectile, barriers);

            //reset firing
            fire = false;
            it = 0;
        }

        private void FireProjectile()
        {
            if(load)
            {
                fire = true;
            }
        }

        private void setProjectedLine(double initialX, double initialY, double finalX, double finalY)
        {
            projectedLine = new Path();
            projectedLine.moveTo((float)initialX, (float) initialY);
            projectedLine.lineTo((float)finalX, (float)finalY);
        }

        private void setCatapultLine(double initialX, double initialY, double finalX, double finalY)
        {
            catapultLine = new float[]{(float) initialX, (float) initialY, (float) finalX, (float) finalY};

//            catapultPath = new Path();
//            catapultPath.moveTo((float)initialX, (float) initialY);
//            catapultPath.lineTo((float)finalX, (float)finalY);


        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh)
        {
            //called once on app startup, allows for height/width of screen to be accounted for

            this.w = w;
            this.h = h;

            springLengthChange = validator(springLengthChange,1,1000);


            currentLevel = 0;

            barrierList.updateSizes(h,w);

            barrierLevels = FinishBarrierList(barrierList.getBarriers());
            barriers = barrierLevels.get(currentLevel);

            setCatapultLine();

            LoadButton = new RectF(400, h - 150, 570, h);
            FireButton = new RectF(600, h-150, 770,h);
            AngleDownButton = new RectF(1000, h - 100 , 1100, h );
            AngleUpButton = new RectF(1000, h - 250 , 1100, h - 150);
            SpringLengthDownButton = new RectF(1130, h - 100, 1230, h);
            SpringLengthUpButton = new RectF(1130, h - 250 , 1230, h - 150);

            buttonMap.put("LoadButton", LoadButton);
            buttonMap.put("FireButton", FireButton);
            buttonMap.put("AngleUpButton", AngleUpButton);
            buttonMap.put("AngleDownButton", AngleDownButton);
            buttonMap.put("SpringLengthUpButton", SpringLengthUpButton);
            buttonMap.put("SpringLengthDownButton",SpringLengthDownButton);




            super.onSizeChanged(w,h,oldw,oldh);
        }


        protected void drawBarrier(Canvas c, Barrier barrier)
        {
            c.drawRect(barrier.GetBarrierShape(), barrier.GetOutsideColor());
            RectF intBarrier = new RectF(barrier.GetBarrierShape());
            intBarrier.left += 10;
            intBarrier.top += 10;
            intBarrier.right -= 10;
            intBarrier.bottom -= 10;

            c.drawRect(intBarrier, barrier.GetInsideColor());

        }

        ///draws rectangle with specified border width
        protected void drawRect(Canvas c, RectF rectangle, Paint innerPaint, Paint outerPaint, int border)
        {
            c.drawRect(rectangle, outerPaint);

            RectF intRectangle = new RectF(rectangle);
            intRectangle.left += border;
            intRectangle.top += border;
            intRectangle.right -= border;
            intRectangle.bottom -= border;

            c.drawRect(intRectangle, innerPaint);
        }

        ///draws rectangle with default size 10 of border
        protected void drawRect(Canvas c, RectF rectangle, Paint innerPaint, Paint outerPaint)
        {
            drawRect(c,rectangle,innerPaint,outerPaint,10);
        }

        protected void drawRect(Canvas c, RectF rectangle)
        {
            drawRect(c,rectangle,innerButtonPaint, outerButtonPaint,10);
        }

        protected void drawCircle(Canvas c, LocationVector loc, int radius, Paint innerPaint, Paint outerPaint)
        {
            drawCircle(c, loc, radius, 2, innerPaint, outerPaint);
        }

        protected void drawCircle(Canvas c, LocationVector loc, int radius, int width, Paint innerPaint, Paint outerPaint)
        {
            c.drawCircle((float) loc.x, (float) loc.y, radius, outerPaint);
            c.drawCircle((float) loc.x, (float) loc.y, radius - width, innerPaint);
        }


        protected ArrayList<ArrayList<Barrier>> FinishBarrierList(ArrayList<ArrayList<Barrier>> allLevels)
        {
            //ArrayList<ArrayList<Barrier>> returnBarriers = allLevels;
            Barrier baseBarrier = new Barrier(0, h + radius, w, 10000, 2);
            Barrier capBarrier = new Barrier(w, -10000, 1000000, 10000, 2);
            Barrier winZone = new Barrier(w - 100, 0, 100, h, 1);
            boolean first = true;

            for (ArrayList<Barrier> levelList: allLevels)
            {
                levelList.add(baseBarrier);
                levelList.add(winZone);

                if(first)
                {
                    first = !first;
                }
                else
                {
                    levelList.add(capBarrier);
                }

            }

            return allLevels;
        }


        //returns true if position specified lies within rectangle boundaries.
        private Boolean InsideRectangle(RectF rectangle, double xPos, double yPos)
        {
            Boolean insideRect = false;

            if (xPos <= rectangle.right && xPos >= rectangle.left)
            {
                if (yPos <= rectangle.bottom && yPos >= rectangle.top)
                {
                    insideRect = true;
                }
            }

            return insideRect;
        }

        private double PowerScaleUp(double power)
        {
            if (power >= 10)
            {
                return 14 + (power - 10) / 5;
            }
            else
            {
                return 0.14 * power * power;
            }
        }

        private double PowerScaleDown(double power)
        {
            return 10 + 5 * (power - 14);
        }

    }
}
