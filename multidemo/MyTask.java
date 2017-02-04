/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.nott.cs.g53dia.multidemo;

import uk.ac.nott.cs.g53dia.multilibrary.*;;

/**
 *
 * @author Sylvia
 */
public class MyTask {
    private final Station station;
    private double ratio;
    private int fuelConsumption;
    private final Well nearestWell;
    private int waterReq;
//    private boolean isShared = false;
//    private boolean isComplete = false;
    private SmartTanker announcer;

    public MyTask(Station station, double ratio, int waterReq, int fuelConsumption, Well well) {
        this.station = station;
        this.ratio = ratio;
        this.waterReq = waterReq;
        this.fuelConsumption = fuelConsumption;
        this.nearestWell = well;
    }
    
    public MyTask(Station station, double ratio, int waterReq, int fuelConsumption) {
        this.station = station;
        this.ratio = ratio;
        this.waterReq = waterReq;
        this.fuelConsumption = fuelConsumption;
        this.nearestWell = null;
    } 
    
    public MyTask(Station station, int waterReq, int fuelConsumption, SmartTanker announcer) {
//        this.isShared = true;
//        this.isComplete = false;
        this.station = station;
        this.waterReq = waterReq;
        this.fuelConsumption = fuelConsumption;
        this.announcer = announcer;
        this.nearestWell = null;
    }

    public void setFuelConsumption(int fuelConsumption) {
        this.fuelConsumption = fuelConsumption;
    }
    
    public void setWaterReq(int waterReq) {
        this.waterReq = waterReq;
    }
    
    public int getWaterReq() {
        return waterReq;
    }
  
    public Station getStation() {
        return this.station;
    }

    public double getRatio() {
        return this.ratio;
    }

    public int getFuelConsumption() {
        return this.fuelConsumption;
    }

    public Well getNearestWell() {
        return this.nearestWell;
    }

//    public void setIsShared(boolean isShared) {
//        this.isShared = isShared;
//    }
//
//    public boolean getIsIsShared() {
//        return this.isShared;
//    }
//
//    public void setIsComplete(boolean isComplete) {
//        this.isComplete = isComplete;
//    }
//
//    public boolean getIsIsComplete() {
//        return this.isComplete;
//    }
}
