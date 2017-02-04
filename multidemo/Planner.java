package uk.ac.nott.cs.g53dia.multidemo;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import uk.ac.nott.cs.g53dia.multilibrary.*;


/**
 *
 * @author Sylvia
 */
public class Planner {
    public Planner(SmartTanker tanker, int initDir) {
        dirMappingConstructor();
        this.owner = tanker;
        this.currentDir = initDir;
    }
    
    private SmartTanker owner;    
    private static final AccessablePoint FUEL_STATION = new AccessablePoint(0, 0);
    private boolean isCommitingTask = false;
    private boolean isCommitingSharedTask = false;
    private boolean isCollectingWater = false;
    private int remainInteration = 1;
    private int nextInteration = 2;
    private int currentDir;
    private HashMap<Integer,Integer> directionMapping = new HashMap<>();


    private void dirMappingConstructor()
    {
        directionMapping.put(0, 0);
        directionMapping.put(1, 4);
        directionMapping.put(2, 2);
        directionMapping.put(3, 6);
        directionMapping.put(4, 1);
        directionMapping.put(5, 7);
        directionMapping.put(6, 3);
        directionMapping.put(7, 5);
    }
    
    public Action actionSelector(Cell[][] view){
        System.out.println("agent " + owner.id + " cur pos " + 
                owner.currentPos.toString() + " " + owner.getPosition().toString());
        if((owner.getWaterLevel() < owner.MAX_WATER) && (owner.getCurrentCell(view) instanceof Well))
        {
            owner.pcp.nearestWell = null;
            isCollectingWater = false;
            return new LoadWaterAction();
        }
        
        // help friend first
        if(!isCommitingTask && owner.getIsSharingTask())
        {
            isCommitingSharedTask = isCommitingTask = true;
        }
        
        if(isCommitingTask && (owner.currentTaskStation.getTask() != null)) 
        {
            if(isCollectingWater)
            {
                System.out.println("agent " + owner.id + " 1");
                AccessablePoint des = owner.wm.getWellList().get(owner.nearestWell);
                int dir = positionToDirection(des);
                updatePosition(dir);
                return new MoveTowardsAction(owner.nearestWell.getPoint());
            }
            // reach the task station, deliver water
            else if(owner.getPosition().equals(owner.currentTaskStation.getPoint()))
            {
                isCommitingTask = false;
                // can be shared task or private task
                if(isCommitingSharedTask)
                {
                    isCommitingSharedTask = false;
                    int waterRemain = owner.getWaterLevel() - 
                            owner.currentTaskStation.getTask().getRequired();
                    // finish shared task
                    if(waterRemain >= 0)                        
                        owner.wm.curTaskStations.remove(owner.currentTaskStation);                                              
                    else                       
                        taskAllocating(owner.getSharedTask());
                    
                    owner.setSharedTask(null);
                }
                else
                {
                    owner.wm.curTaskStations.remove(owner.currentTaskStation);
                    owner.wm.getTaskList().replace(owner.currentTaskStation, -1);    
                }
                // waiting for task
//                owner.wm.taskStatus.replace(owner, Boolean.FALSE);                
                return new DeliverWaterAction(owner.currentTaskStation.getTask());
            }
            // not reach the task station yet
            else
            {
                System.out.println("agent " + owner.id +"2");
                AccessablePoint des = owner.wm.getStationList().get(owner.currentTaskStation);
                int dir = positionToDirection(des);
                updatePosition(dir);
                return new MoveTowardsAction(owner.currentTaskStation.getPoint());
            }
        }
                    
        // if doesn't commit task and has task, select a task
        if((!isCommitingTask) && hasTask())
        {
            MyTask task = taskSelector();
            // task available && execute the task
            if (task != null)   
            { 
                // commiting task
                owner.currentTaskStation = task.getStation();
                owner.wm.curTaskStations.add(owner.currentTaskStation);
                isCommitingTask = true;

                if (task.getNearestWell() != null)
                {
                    isCollectingWater = true;
                    // go to the well first
                    owner.nearestWell = task.getNearestWell();
                    AccessablePoint des = owner.wm.getWellList().get(owner.nearestWell);                    
                    if(owner.currentPos.equals(des))
                    {
                        isCollectingWater = false;
                        return new LoadWaterAction();
                    }
                    else
                    {
                        System.out.println("agent " + owner.id +"3");
                        int dir = positionToDirection(des);
                        updatePosition(dir);
                        return new MoveTowardsAction(task.getNearestWell().getPoint());
                    }
                }
                else if(! owner.getPosition().equals(owner.currentTaskStation.getPoint()))
                {
                    
                    System.out.println("agent " + owner.id +"4");
                    // go to the station directly.
                    AccessablePoint des = owner.wm.getStationList().get(owner.currentTaskStation);
                    int dir = positionToDirection(des);
                    updatePosition(dir);    
                    return new MoveTowardsAction(owner.currentTaskStation.getPoint());
                }
                else
                {
                    owner.wm.curTaskStations.remove(owner.currentTaskStation);
                    owner.wm.getTaskList().replace(owner.currentTaskStation, -1);
                    isCommitingTask = false;
                    return new DeliverWaterAction(owner.currentTaskStation.getTask());
                }
            }
            else if (owner.getWaterLevel() > 1000)
            {
                // choose alternative task and ask help
                task = altTaskSelector();
                // deliver the task
                if(task != null)
                {
                    System.out.println("agent " + owner.id +"5");
                    isCollectingWater = false;
                    owner.currentTaskStation = task.getStation();
                    owner.wm.curTaskStations.add(owner.currentTaskStation);
                    owner.setSharedTask(task);
                    isCommitingSharedTask = isCommitingTask = true;                    
                    AccessablePoint des = owner.wm.getStationList().get(owner.currentTaskStation);
                    int dir = positionToDirection(des);
                    updatePosition(dir);    
                    return new MoveTowardsAction(owner.currentTaskStation.getPoint());
                }
            }   
        }

        // if the fuel is low
        if ((owner.getFuelLevel() < (owner.MAX_FUEL/2) + 5) && (!isCommitingTask))
        {
            if((owner.getCurrentCell(view) instanceof FuelPump))
            {
                return new RefuelAction();
            }
            else
            {
                System.out.println("agent " + owner.id +"6");
                int dir = positionToDirection(FUEL_STATION);
                updatePosition(dir);
                return new MoveTowardsAction(owner.FUEL_PUMP_LOCATION);  
            }
        }
        // keep exploration
        else
        {
            // find well and collect water
            if((owner.getWaterLevel() < owner.MAX_WATER) && 
                    owner.pcp.nearestWell != null)
            {
                System.out.println("agent " + owner.id + " 7");
                AccessablePoint des = owner.wm.getWellList().get(owner.pcp.nearestWell);
                int dir = positionToDirection(des);
                updatePosition(dir);
                return new MoveTowardsAction(owner.pcp.nearestWell.getPoint());
            }
            
            remainInteration--;
            if(remainInteration == 0)
            {
                currentDir = (currentDir + 2) % 8;
                
                if(nextInteration > 49)
                {
                    remainInteration = 15;
                    nextInteration = 16;
                }
                else
                {
                    remainInteration = nextInteration;
                    nextInteration++;
                }
            }
            // compute actual direction and move to that direction
            updatePosition(currentDir);
            return new MoveAction(directionMapping.get(currentDir));
        }    
    }
    
    private PriorityQueue<MyTask> taskFilter(int waterVal)
    {
        PriorityQueue<MyTask> taskList = new PriorityQueue<>(new TaskRatioComparator());
        // go through all the tasks to select tasks doesn't need refill water
        
        for (Map.Entry<Station, Integer> entry : owner.wm.getTaskList().entrySet()) {
            Station station = entry.getKey();
            Integer task = entry.getValue();
            if(task == -1)
                continue;
            
            if (task < waterVal)
            {
                int distance = owner.currentPos.distance(owner.wm.getStationList().get(station));
                int fuelConsumption = distance + 1 + 
                        owner.wm.getStationList().get(station).distance(FUEL_STATION) + 1;
                // able to complete the task
                if(fuelConsumption <= owner.getFuelLevel())
                {
                    double ratio = distance == 0 ? 1000000000 : task / distance;
                    taskList.add(new MyTask(station, ratio, task, distance));
                }
            }
        } 
        return taskList;
    }
    
    private MyTask taskSelector()
    {
        PriorityQueue<MyTask> taskList;
        // task list doesn't need refill water
        taskList = taskFilter(owner.getWaterLevel());
        // if no task which doesn't need refill water, search other tasks
        if(taskList.isEmpty())
        {
            // do not have tasks without refilling water
            for (Map.Entry<Station, Integer> entry : owner.wm.getTaskList().entrySet()) 
            {
                Station station = entry.getKey();
                Integer task = entry.getValue();
                if (task == -1)
                    continue;
                
                int minDistance = 1000000;
                Well tempNearestWell = null;

                if (!owner.wm.getWellList().isEmpty())
                {    
                    // find nearest well
                    for (Map.Entry<Well, AccessablePoint> entry1 : owner.wm.getWellList().entrySet()) 
                    {
                        Well well = entry1.getKey();
                        AccessablePoint wellPoint = entry1.getValue();

                        if (tempNearestWell == null)
                            tempNearestWell = well;

                        if(owner.currentPos.withinBlock(
                                owner.wm.getStationList().get(station), wellPoint))
                        {
                            minDistance = 0;
                            tempNearestWell = well;
                            break;
                        }
                        else
                        {
                            int distance = wellPoint.distance(owner.wm.getStationList().get(station));
                            if (distance < minDistance)
                            {
                                minDistance = distance;
                                tempNearestWell = well;
                            }
                        }
                    }

                    AccessablePoint desWell = owner.wm.getWellList().get(tempNearestWell);
                    
                    int distance = owner.currentPos.distance(desWell) + 2 
                            + owner.wm.getStationList().get(station).
                                    distance(owner.wm.getWellList().get(tempNearestWell)) + 2;

                    int fuelConsumption = distance + 
                                owner.wm.getStationList().get(station).distance(FUEL_STATION) + 3                   ;

                    // able to complete the task
                    if(fuelConsumption < owner.getFuelLevel())
                    {
                        double ratio = task / distance;
                        taskList.add(new MyTask(station, ratio, distance, task, 
                                tempNearestWell));
                    }
                }
            }
        }
        
        if(!taskList.isEmpty())
        {
            owner.wm.getTaskList().remove(taskList.peek().getStation());
        }
        
        return taskList.poll();
    }
    
    private MyTask altTaskSelector()
    {
        PriorityQueue<MyTask> accessableTask = new PriorityQueue<>(new DistanceComparator());
//        int minDistance = 1000000;
//        Station tempNearestStation = null;
        // find the reachable task station
        for (Map.Entry<Station, Integer> entry : owner.wm.getTaskList().entrySet()) 
        {
            Station station = entry.getKey();
            Integer task = entry.getValue();
            if (task == -1)
                continue;
                
            int distanceToMe = owner.wm.stationList.get(station).distance(owner.currentPos);
            int distanceToPump = owner.wm.stationList.get(station).distance(FUEL_STATION);
            int fuelConsumption = distanceToMe + distanceToPump + 3;
            
//            int waterDemand = task - owner.getWaterLevel();
            
            if((distanceToMe + distanceToPump + 3) < owner.getFuelLevel() )
                accessableTask.add(new MyTask(station, task, fuelConsumption, owner));    
        }
        if(!accessableTask.isEmpty())
        {   
            owner.wm.getTaskList().remove(accessableTask.peek().getStation());
        }
        
        return accessableTask.poll();
    }
    
    public void taskAllocating(MyTask sharedTask)
    {
//        ArrayList<SmartTanker> vacantList = new ArrayList<>();
        
        int shortestDis = 1000000;
        SmartTanker tempNearest = null;
        
        for (Tanker tanker : owner.wm.fleet) {
            SmartTanker st = (SmartTanker) tanker;
            // skip if is committing task or is sharing task
            if ((st == owner) || st.pl.isCommitingTask || st.getIsSharingTask()
                    || (st.getWaterLevel()<=0))
                continue;
                
            Station station = sharedTask.getStation();
            int distanceToIt = owner.wm.stationList.get(station).distance(st.currentPos);
            int distanceToPump = owner.wm.stationList.get(station).distance(FUEL_STATION);
            int fuelConsumption = distanceToIt + distanceToPump + 3;
            // set fuelConsumption to the seleceted agent
            sharedTask.setFuelConsumption(fuelConsumption);
                
            // find the one nearest the task
            if(fuelConsumption < st.getFuelLevel() && distanceToIt < shortestDis)
            {
//                if(tempNearest == null)
//                    tempNearest = st;
//                                       
//                if(distanceToIt < shortestDis)
//                {
                    tempNearest = st;
                    shortestDis = distanceToIt;
//                }
            } 
        }
 
        // allocate the task
        if(tempNearest != null)
        {
            tempNearest.setSharedTask(sharedTask);
//            System.out.println("agent " + owner.id + " ask " + tempNearest.id + " for help ");
        }
        // no one can help
        else
        {
            owner.wm.curTaskStations.remove(owner.currentTaskStation);
        }         
    }
        
    private boolean hasTask()
    {
        for (Map.Entry<Station, Integer> entry : owner.wm.getTaskList().entrySet()) {
            Integer integer = entry.getValue();
            // has task
            if (integer != -1)            
                return true;            
        } 
        return false;
    }
    
//    private void resetPosition()
//    {
//        owner.currentPos.setX(0);
//        owner.currentPos.setY(0);
//    }
    
    private void updatePosition(int dir){
        int prex, prey;
        switch (dir)
        {
            case 0:
                prey = owner.currentPos.getY();
                prey++;
                owner.currentPos.setY(prey);
                break;
            case 1:
                prex = owner.currentPos.getX();
                prey = owner.currentPos.getY();
                prex++;
                prey++;
                owner.currentPos.setX(prex);
                owner.currentPos.setY(prey);
                break;
            case 2:
                prex = owner.currentPos.getX();
                prex++;
                owner.currentPos.setX(prex);
                break;     
            case 3:
                prex = owner.currentPos.getX();
                prey = owner.currentPos.getY();
                prex++;
                prey--;
                owner.currentPos.setX(prex);
                owner.currentPos.setY(prey);
                break;
            case 4:
                prey = owner.currentPos.getY();
                prey--;
                owner.currentPos.setY(prey);
                break;
            case 5:
                prex = owner.currentPos.getX();
                prey = owner.currentPos.getY();
                prex--;
                prey--;
                owner.currentPos.setX(prex);
                owner.currentPos.setY(prey);
                break;
            case 6:
                prex = owner.currentPos.getX();
                prex--;
                owner.currentPos.setX(prex);
                break;
            case 7:
                prex = owner.currentPos.getX();
                prey = owner.currentPos.getY();
                prex--;
                prey++;
                owner.currentPos.setX(prex);
                owner.currentPos.setY(prey);
                break;   
        } 
                
    }
    
    private int positionToDirection(AccessablePoint des)
    {
        int dir;
        int xDiff = des.getX() 
                - owner.currentPos.getX();
        int yDiff = des.getY() - owner.currentPos.getY();
        
        if(xDiff < 0){
            if(yDiff < 0)	dir = 5;//SOUTHWEST
            else if(yDiff > 0)	dir = 7;//NORTHWEST
            else                dir = 6;//WEST
	}
        else if(xDiff > 0){
            if(yDiff < 0)	dir = 3;//SOUTHEAST
            else if(yDiff > 0)	dir = 1;//NORTHEAST
            else                dir = 2;//EAST
	}
        else
        {
            if(yDiff < 0)	dir = 4;//SOUTH
            else                dir = 0;//NORTH
        }
	return dir;
    } 
}

class TaskRatioComparator implements Comparator<MyTask>
{
    @Override
    public int compare(MyTask task1, MyTask task2) 
    {
        if(task1.getRatio() > task2.getRatio())        
            return 1;    
        else if (task1.getRatio() < task2.getRatio())
            return -1;
        
        return 0;
    }
}

class DistanceComparator implements Comparator<MyTask>
{
    @Override
    public int compare(MyTask t1, MyTask t2) {
        if(t1.getFuelConsumption() < t2.getFuelConsumption())        
            return 1;    
        else if (t1.getFuelConsumption() > t2.getFuelConsumption())
            return -1;
        
        return 0;
    }

}

