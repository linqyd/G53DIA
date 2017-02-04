package uk.ac.nott.cs.g53dia.multidemo;
import java.util.ArrayList;
import uk.ac.nott.cs.g53dia.multilibrary.*;

public class SmartTanker extends Tanker {    
    public SmartTanker(int initDir, int id, WorldModel wm) {
        this.pcp = new Percept(this);
        this.wm = wm;
        this.pl = new Planner(this, initDir);
        this.id = id;
    }
    
    private static final AccessablePoint FUEL_STATION = new AccessablePoint(0, 0);
    private MyTask sharedTask;
    Percept pcp;
    WorldModel wm;
    Planner pl;
    int id;
    AccessablePoint currentPos = new AccessablePoint(0, 0);
    Station currentTaskStation;
    Well nearestWell;
    ArrayList<MyTask> privateTaskList = new ArrayList<>();
    private boolean isSharingTask = false;
        
    public void setSharedTask(MyTask sharedTask) {
        this.sharedTask = sharedTask;
        if(sharedTask != null)
        {
            this.currentTaskStation = this.sharedTask.getStation();
            this.isSharingTask = true;
        }
        else
        {
            this.isSharingTask = false;
        }
    }
    
    public void setWaterReq (int waterReq)
    {
        this.sharedTask.setWaterReq(waterReq);
    }

    public MyTask getSharedTask() {
        return this.sharedTask;
    }
    
    public boolean getIsSharingTask()
    {
        return this.isSharingTask;
    }
    
    @Override
    public Action senseAndAct(Cell[][] view, long timestep) {
        // new percept -> update knowledgebase
        pcp.acceptNewPercept(view);   
        return pl.actionSelector(view);
    }
}
