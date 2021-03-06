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
        public Paint predicterPaint;
        public Paint catapultPaint;

        public String successString;
        public String failureString;
        public String completionString;


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
        public double braceAngle = 15;
        public double beamLength = 100;
        public double springLengthChange = 15;

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
            outerButtonPaint = new Paint(outerPaint);

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
            successString = "SUCCESS";
            failureString = "FAILURE";
            completionString = "COMPLETE VICTORY";


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
                        LoadProjectile();
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

            }
            return true;
        }



        //on canvas draw
        protected void onDraw(Canvas c)
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
                Barrier barrier = (Barrier) barriers.get(i);
                drawBarrier(c,barrier.GetBarrierShape());
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

                drawCircle(c, initialLoc, radius,2, innerPaint, outerPaint);
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

                    drawCircle(c,currentLoc,radius,2, innerPaint, outerPaint);

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

            barrierLevels = getBarrierList();
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
        protected void drawBarrier(Canvas c, RectF barrier)
        {
            c.drawRect(barrier, outerPaint); //draw outside in black
            RectF intBarrier = new RectF(barrier);
            intBarrier.left += 10;
            intBarrier.top += 10;
            intBarrier.right -= 10;
            intBarrier.bottom -= 10;

            c.drawRect(intBarrier, innerPaint);
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



        protected ArrayList<ArrayList<Barrier>> getBarrierList()
        {

            //initEndWall = new Barrier(w - 200, -10, 100, 400);
            //scndEndWall = new Barrier(w - 200, h - 290, 100, 300);

            final ArrayList<Barrier> levelOneBarriers = new ArrayList<Barrier>(){{
                add(new Barrier(w - 200, -10, 100, 400));
                add(new Barrier(w - 200, h - 290, 100, 300));
                //  add(new Barrier(0, h + radius, w, 1));
            }};

            final ArrayList<Barrier> levelTwoBarriers = new ArrayList<Barrier>(){{
                add(new Barrier(w - 200, -10, 100, 400));
                add(new Barrier(w - 200, h - 290, 100, 300));
                // add(new Barrier(0, h + radius, w, 1));
                add(new Barrier(w - 600, h - 800, 200, 200));
            }};

            final ArrayList<Barrier> levelThreeBarriers = new ArrayList<Barrier>(){{
                add(new Barrier(w - 200, -10, 100, 300));
                add(new Barrier(w - 200, h - 190, 100, 200));
                add(new Barrier(w - 200, h - 490, 100, 100));
                //add(new Barrier(0, h + radius, w, 1));
            }};

            final ArrayList<Barrier> levelFourBarriers = new ArrayList<Barrier>(){{
                add(new Barrier(w - 200, - 10, 100, 200));
                add(new Barrier(w - 200, 400, 100, 200));
                add(new Barrier(w - 200, 800, 100, 200));
            }};

            final ArrayList<Barrier> levelFiveBarriers = new ArrayList<Barrier>(){{
                add(new Barrier(w - 200, -10, 100, 400));
                add(new Barrier(w - 200, h - 290, 100, 300));
                add(new Barrier(w - 400, 300, 100, 200));
            }};

            final ArrayList<Barrier> levelSixBarriers = new ArrayList<Barrier>(){{
                add(new Barrier(w - 200, -10, 100, 300));
                add(new Barrier(w - 400, -10, 100, 400));
                add(new Barrier(w - 200, h - 490, 100, 100));
                add(new Barrier(w - 400, h - 490, 100, 500));
            }};

            final ArrayList<Barrier> levelSevenBarriers = new ArrayList<Barrier>(){{
                add(new Barrier(w - 200, -10, 100, 700));
                add(new Barrier(800, 500, 200, 200));
            }};

            final ArrayList<Barrier> levelEightBarriers = new ArrayList<Barrier>(){{
                add(new Barrier(w - 200, -10, 100, 750));
                add(new Barrier(800, 300, 200, 400));
            }};

            final ArrayList<Barrier> levelNineBarriers = new ArrayList<Barrier>(){{
                add(new Barrier(w - 200, -10, 100, 750));
                add(new Barrier(800, 300, 200, 400));
                add(new Barrier(w - 200, h - 100, 100, 110));
            }};

            final ArrayList<Barrier> levelTenBarriers = new ArrayList<Barrier>(){{
                add(new Barrier(w - 200, -10, 100, 450));
                add(new Barrier(800, 300, 100, h - 500));
                add(new Barrier(w - 200, h - 400, 100, 410));
            }};



            ArrayList<ArrayList<Barrier>> AllBarriers = new ArrayList<ArrayList<Barrier>>(){{
                add(levelOneBarriers);
                add(levelTwoBarriers);
                add(levelThreeBarriers);
                add(levelFourBarriers);
                add(levelFiveBarriers);
                add(levelSixBarriers);
                add(levelSevenBarriers);
                add(levelEightBarriers);
                add(levelNineBarriers);
                add(levelTenBarriers);
            }};

            return BarrierList(AllBarriers);
        }

        protected ArrayList<ArrayList<Barrier>> BarrierList(ArrayList<ArrayList<Barrier>> allLevels)
        {
            //ArrayList<ArrayList<Barrier>> returnBarriers = allLevels;
            Barrier baseBarrier = new Barrier(0, h + radius, w, 10000);
            Barrier capBarrier = new Barrier(w, -10000, 1000000, 10000);
            boolean first = true;

            for (ArrayList<Barrier> levelList: allLevels)
            {
                levelList.add(baseBarrier);

                if(first)
                {
                    first = !first;
                }
                else
                {
                    levelList.add(capBarrier);
                }

            }
//
//
//            for(int i = 0; i < allLevels.size(); i++)
//                if(!allLevels.get(i).contains(baseBarrier))
//                {
//                    ArrayList<Barrier> levelSpec = allLevels.get(i);
//                    levelSpec.add(baseBarrier);
//
//                    returnBarriers.remove(allLevels.get(i));
//                    returnBarriers.add(levelSpec);
//                }



//            for (ArrayList<Barrier> levelBarrier: allBarriers) {
//
//                if(!levelBarrier.contains(baseBarrier))
//                {
//
//                }
//                for (Barrier b: levelBarrier) {
//
//                }
//
//
//            }
//
//
//            for (int i = 0; i < allBarriers.size(); i++)
//            {
//                boolean testBool = allBarriers.contains(baseBarrier);
//            }

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
