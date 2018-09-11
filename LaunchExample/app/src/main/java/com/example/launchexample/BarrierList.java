package com.example.launchexample;

import java.util.ArrayList;



public class BarrierList {

    ArrayList<ArrayList<Barrier>> AllBarriers = new ArrayList<ArrayList<Barrier>>();

    int h;
    int w;


    public BarrierList()
    {
        this.h = 500;
        this.w = 1000;

        FillBarrierList();
    }

    public BarrierList(int h, int w)
    {
        this.h = h;
        this.w = w;

        FillBarrierList();
    }

    public void updateSizes(int h, int w)
    {
        this.h = h;
        this.w = w;
        AllBarriers.clear();
        FillBarrierList();
    }

    protected void FillBarrierList()
    {

        //test barrier
        AllBarriers.add(new ArrayList<Barrier>(){{
            add(new Barrier(w - 200, -10, 100, 400, 2));
            add(new Barrier(w - 200, h - 290, 100, 300, 2));
            add(new Barrier(200,-10,100,800,2));
            add(new Barrier(800,700,100,300,2));
            add(new Barrier(500,h-250,200,100,3));
        }});

        AllBarriers.add(new ArrayList<Barrier>(){{
            add(new Barrier(w - 200, -10, 100, 400, 2));
            add(new Barrier(w - 200, h - 290, 100, 300, 2));
        }});

        AllBarriers.add(new ArrayList<Barrier>(){{
            add(new Barrier(w - 200, -10, 100, 400, 2));
            add(new Barrier(w - 200, h - 290, 100, 300, 2));
            add(new Barrier(w - 600, h - 800, 200, 200, 2));
        }});

        AllBarriers.add(new ArrayList<Barrier>(){{
            add(new Barrier(w - 200, -10, 100, 300, 2));
            add(new Barrier(w - 200, h - 190, 100, 200, 2));
            add(new Barrier(w - 200, h - 490, 100, 100, 2));
        }});

        AllBarriers.add(new ArrayList<Barrier>(){{
            add(new Barrier(w - 200, - 10, 100, 200, 2));
            add(new Barrier(w - 200, 400, 100, 200, 2));
            add(new Barrier(w - 200, 800, 100, 200, 2));
        }});

        AllBarriers.add(new ArrayList<Barrier>(){{
            add(new Barrier(w - 200, -10, 100, 400, 2));
            add(new Barrier(w - 200, h - 290, 100, 300, 2));
            add(new Barrier(w - 400, 300, 100, 200, 2));
        }});

        AllBarriers.add(new ArrayList<Barrier>(){{
            add(new Barrier(w - 200, -10, 100, 300, 2));
            add(new Barrier(w - 400, -10, 100, 400, 2));
            add(new Barrier(w - 200, h - 490, 100, 100, 2));
            add(new Barrier(w - 400, h - 490, 100, 500, 2));
        }});

        AllBarriers.add(new ArrayList<Barrier>(){{
            add(new Barrier(w - 200, -10, 100, 700, 2));
            add(new Barrier(800, 500, 200, 200, 2));
        }});

        AllBarriers.add(new ArrayList<Barrier>(){{
            add(new Barrier(w - 200, -10, 100, 750, 2));
            add(new Barrier(800, 300, 200, 400, 2));
        }});

        AllBarriers.add(new ArrayList<Barrier>(){{
            add(new Barrier(w - 200, -10, 100, 750, 2));
            add(new Barrier(800, 300, 200, 400, 2));
            add(new Barrier(w - 200, h - 100, 100, 110, 2));
        }});

        AllBarriers.add(new ArrayList<Barrier>(){{
            add(new Barrier(w - 200, -10, 100, 450, 2));
            add(new Barrier(800, 300, 100, h - 500, 2));
            add(new Barrier(w - 200, h - 400, 100, 410, 2));
        }});

    }

    protected ArrayList<ArrayList<Barrier>> getBarriers()
    {
        return AllBarriers;
    }
}
