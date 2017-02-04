/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.nott.cs.g53dia.multidemo;

import java.util.ArrayList;
import java.util.HashMap;
import uk.ac.nott.cs.g53dia.multilibrary.*;

/**
 *
 * @author Sylvia
 */
public class WorldModel {
    private WorldModel()
    {}
	
    private static WorldModel uniqueInstance = null;
    HashMap<Station, AccessablePoint> stationList = new HashMap<>();
    HashMap<Station, Integer> taskList = new HashMap<>();
    HashMap<Well, AccessablePoint> wellsList = new HashMap<>();
//    HashMap<SmartTanker, Boolean> taskStatus = new HashMap<>();
    ArrayList<Station> curTaskStations = new ArrayList();
    Fleet fleet;
    
    
    public HashMap<Station, AccessablePoint> getStationList() {
        return stationList;
    }

    public HashMap<Station, Integer> getTaskList() {
        return taskList;
    }

    public HashMap<Well, AccessablePoint> getWellList() {
        return wellsList;
    }
    
    synchronized public static WorldModel getInstance(){  
        if(uniqueInstance==null)      
        	uniqueInstance=new WorldModel();                 

        return uniqueInstance;  
    }   
}
