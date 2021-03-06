package com.abc.team.becathon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AJ on 12/29/2016.
 */

public class LocateUser
{
    static ArrayList<Pair<Integer, Integer>> beacon_pos = new ArrayList<Pair<Integer, Integer>>();
    static ArrayList<Pair<Integer, Integer>> room_pos = new ArrayList<Pair<Integer, Integer>>();
    static ArrayList<Pair<Integer, Integer>> dest_pos = new ArrayList<Pair<Integer, Integer>>();
    static ArrayList<Pair<Integer, Integer>> obstacle_pos = new ArrayList<Pair<Integer, Integer>>();

    public LocateUser()
    {
        this.beacon_pos.add(new Pair<Integer, Integer>(0,1));
        this.beacon_pos.add(new Pair<Integer, Integer>(4,1));
        this.beacon_pos.add(new Pair<Integer, Integer>(3,5));

        this.obstacle_pos.add(new Pair<Integer, Integer>(13,15));
        this.obstacle_pos.add(new Pair<Integer, Integer>(15,15));
        this.obstacle_pos.add(new Pair<Integer, Integer>(15, 13));
        this.obstacle_pos.add(new Pair<Integer, Integer>(13, 13));

        this.room_pos.add(new Pair<Integer, Integer>(0,24));
        this.room_pos.add(new Pair<Integer, Integer>(24,24));
        this.room_pos.add(new Pair<Integer, Integer>(24,0));
        this.room_pos.add(new Pair<Integer, Integer>(0,0));


        this.dest_pos.add(new Pair<Integer, Integer>(0,24));
        this.dest_pos.add(new Pair<Integer, Integer>(24,24));
    }

    public static int getPos(String s) {
        //return (int)(Math.random()*3);
        switch (s)
        {
            case "11111": return 2;
            case "22222": return 1;
            case "33333": return 0;
            default: return -1;
        }
    }

    public static Pair<Integer, Integer> getUserLocation(ArrayList<Double> beacon_dist, ArrayList<String> major)  //List of user distance from every beacon
    {
        //return new Pair<Integer, Integer>((int)(Math.random()*24),(int)(Math.random()*24));
        double fac1 = 1.5,fac2 = 1.5, fac3 = 1.5, fac4 = 1;
        //Uncomment this code for working beacons
        double p1, p2, q1, q2; //Point of intersection of two circles
        double d, l, h, x1 = -1, x2 = -1, y1=-1, y2= -1, x3 = -1, y3=-1;
        int found = 0;
        for(int i=0; i<3; i++)
        {
            if(beacon_dist.get(i) != -1)
                found++;
        }
        if(found < 2)
        {
            return new Pair<Integer, Integer>(-1000, -1000);
        }
        else
        {
            for(int i=0; i<beacon_dist.size(); i++)
            {
                switch(major.get(i))
                {
                    case "11111":  beacon_dist.set(i, beacon_dist.get(i)*1.8);
                        break;

                    case "22222" : beacon_dist.set(i, beacon_dist.get(i)*1.35);
                        break;

                    case "33333" : beacon_dist.set(i, beacon_dist.get(i)*1.2);
                        break;

                }
                int j =getPos(major.get(i));
                if(j == -1)
                {
                    continue;
                }
                if(beacon_dist.get(i) != -1) {
                    if (x1 == -1) {
                        x1 = beacon_pos.get(getPos(major.get(i))).getFirst();
                        y1 = beacon_pos.get(getPos(major.get(i))).getSecond();
                    } else if (x2 == -1) {
                        x2 = beacon_pos.get(getPos(major.get(i))).getFirst();
                        y2 = beacon_pos.get(getPos(major.get(i))).getSecond();
                    } else {
                        x3 = beacon_pos.get(j).getFirst();
                        y3 = beacon_pos.get(j).getSecond();
                    }
                }
            }
            d = Math.sqrt(Math.pow((x1-x2),2) + Math.pow((y1-y2),2));
            l = (Math.pow(beacon_dist.get(0),2) - Math.pow(beacon_dist.get(1),2) + d*d)/(2*d);
            h = Math.sqrt(Math.pow(beacon_dist.get(0),2) - l*l);

            p1 = (l/d)*(x2-x1) + (h/d)*(y2-y1) + x1;
            p2 = (l/d)*(x2-x1) - (h/d)*(y2-y1) + x1;
            q1 = (l/d)*(y2-y1) - (h/d)*(x2-x1) + y1;
            q2 = (l/d)*(y2-y1) + (h/d)*(x2-x1) + y1;

            if(found == 2)
            {
                int x = (int)(p1+p2/2), y = (int)((q1+q2)/2);
                return new Pair<Integer, Integer>(x, y);
            }
            else
            {
                double d1, d2;
                d1 = Math.sqrt(Math.pow((p1-x3),2) + Math.pow((q1-y3),2));
                d2 = Math.sqrt(Math.pow((p2-x3),2) + Math.pow((q2-y3),2));
                if(Math.abs(beacon_dist.get(2) - d1) > Math.abs(beacon_dist.get(2) - d2))
                {
                    return new Pair<Integer, Integer>((int)p2, (int)q2);
                }
                else
                {
                    return new Pair<Integer, Integer>((int)p1, (int)q1);
                }
            }


        }
    }

    public Pair<Double, ArrayList<String>> getDestinationInfo(Pair<Integer, Integer> user_loc, Pair<Integer, Integer> destination)
    {
        double slope;
        String instruction;
        ArrayList<String> directions = new ArrayList<String>();
        int x1 = user_loc.getFirst(), x2 = destination.getFirst(), y1 = user_loc.getSecond(), y2 = destination.getSecond();
        double dist = Math.sqrt(Math.pow((x2-x1),2) - Math.pow((y2-y1),2));
        if(user_loc.getFirst()  == destination.getFirst())
        {
            slope = -1000;
            if(y2 < y1)
            {
                instruction = "Move in East Direction";
            }
            else
            {
                instruction = "Move in West Direction";
            }
        }
        else
        {
            slope = (destination.getSecond() - user_loc.getSecond())/ (destination.getFirst() - user_loc.getFirst());
            double angle = Math.toDegrees(Math.atan(slope));
            if(slope < 0)
            {
                if(y2 > y1 && x2 < x1)
                {
                    instruction = "Move " + Math.abs(angle) + " degrees in North East Direction ";
                }
                else
                {
                    instruction = "Move " + Math.abs(angle) + " degrees in South West Direction";
                }
            }
            else if(slope > 0)
            {
                if(y2 > y1 && x2 > x1)
                {
                    instruction = "Move " + Math.abs(angle) + " degrees in South East Direction";
                }
                else
                {
                    instruction = "Move " + Math.abs(angle) + " degrees in North West Direction";
                }
            }
            else
            {
                if(x2 < x1)
                {
                    instruction = "Move in North Direction";
                }
                else
                {
                    instruction = "Move in South Direction";
                }
            }
        }

        ArrayList<Pair<Integer, Integer>> poi = new ArrayList<Pair<Integer,Integer>>();


        directions.add(instruction);



        return new Pair<Double, ArrayList<String>>(dist/4, directions); //Units to metre
    }

}