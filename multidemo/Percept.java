/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.nott.cs.g53dia.multidemo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import uk.ac.nott.cs.g53dia.multilibrary.*;

/**
 *
 * @author Sylvia
 */
public class Percept {
    private SmartTanker owner;
    Well nearestWell = null;
    
    public Percept(SmartTanker owner) {
        this.owner = owner;
    }
    
    public void acceptNewPercept(Cell[][] view)
    {
        AccessablePoint curPos = owner.currentPos;
        // new percept
        int shortestDis = 5;
        
        for (int i = 0; i < view.length; i++) {
            for (int j = 0; j < view[i].length; j++) {
                if ((view[i][j] instanceof Station))
                {
                    Station curStation = (Station) view[i][j];
                    Task curTask= curStation.getTask();
                    int stationX = curPos.getX() - 12 + i;
                    int stationY = curPos.getY() + 12 - j;
                    AccessablePoint stationPoint = new AccessablePoint(stationX, stationY);
                    
                    if(!owner.wm.curTaskStations.contains(curStation))
                    {
                        appendStation(curStation, stationPoint);                    
                        appendTask(curStation);
                    }
//                    else
//                    {
//                        System.out.println("owner " + owner.id + "other tanker executing!");
//                    }
                }
                if (view[i][j] instanceof Well)
                {
                    Well curWell = (Well) view[i][j];
                    int wellX = curPos.getX() - 12 + i;
                    int wellY = curPos.getY() + 12 - j;
                    AccessablePoint wellPoint = new AccessablePoint(wellX, wellY);
                    
                    if(wellPoint.distance(curPos) < shortestDis)
                    {
                        shortestDis = wellPoint.distance(curPos);
                        nearestWell = curWell;
                    }
                    appendWell(curWell, wellPoint);
                }  
            }
        }
        
//        if(nearestWell != null)
//            System.out.println("agent " + owner.id + " shortest dis is " + shortestDis);
    }

    private void appendStation(Station station, AccessablePoint point)
    {
        // if the input station is the current task station, update current
        // task station
        if(owner.currentTaskStation != null 
                && station.equals(owner.currentTaskStation))
            owner.currentTaskStation = station;            
        
        for (Station s : owner.wm.curTaskStations) {
            if(s.equals(station))
            {    
                owner.wm.curTaskStations.remove(s);
                owner.wm.curTaskStations.add(station); 
            }    
        }
           
        HashMap stationList = owner.wm.getStationList();
        Iterator it = stationList.entrySet().iterator();
            while (it.hasNext()) 
            {
                Map.Entry pairs = (Map.Entry) it.next();
                Station station1 = (Station) pairs.getKey();
                                    
                if (station1.equals(station))
                    it.remove();
            }
        stationList.put(station, point);
    }
    
    private void appendTask(Station station)
    {
//    	System.out.println("append task!");
        int task = station.getTask() == null ? -1 : station.getTask().getRequired();
                
        HashMap taskList = owner.wm.getTaskList();
        Iterator it = taskList.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                Station station1 = (Station) pairs.getKey();

                if (station1.equals(station))
                    it.remove();
            }
            
        taskList.put(station, task);
//        System.out.println("owner " + owner.id + " appends task " + station.getPoint().toString());
    }        
    
    private void appendWell(Well well, AccessablePoint point)
    {
        HashMap wellList = owner.wm.getWellList();
        Iterator it = wellList.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                Well well1 = (Well) pairs.getKey();
                                
                if (well1.equals(well))
                    it.remove();
                
                if(well.equals(owner.nearestWell))
                    owner.nearestWell = well;
            }
        wellList.put(well, point);        
    }

}
