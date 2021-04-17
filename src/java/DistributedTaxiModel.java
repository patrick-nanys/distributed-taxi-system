package java;

import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;

public class DistributedTaxiModel extends GridWorldModel {
    public static final int CUST  = 16;
    public static final int GSize = 31;
    private int numTaxi;
    private int numClient;
    private HashMap<Integer, Location> prevLocations = new HashMap<Integer, Location>();

    protected DistributedTaxiModel(int numTaxi, int numClient) {
        super(GSize, GSize, 44);
        this.numTaxi = numTaxi;
        this.numClient = numClient;
        for(int i = 0; i < this.numTaxi + this.numClient; i++) {
            setAgPos(i, getFreePos());
        }
        // set broker position
        setAgPos(this.numTaxi + this.numClient, 0, 0);
    }

    enum Direction {
        UP, DOWN, LEFT, RIGHT;
    }

    public int getNumTaxi() {
        return numTaxi;
    }

    public int getNumClient() {
        return numClient;
    }

    public boolean move(int agentId, Direction direction) {
        Location location = getAgPos(agentId);
        Location newLoc = null;
        switch (direction) {
            case UP:
                newLoc = new Location(location.x, location.y - 1);
                break;
            case DOWN:
                newLoc = new Location(location.x, location.y + 1);
                break;
            case LEFT:
                newLoc = new Location(location.x - 1, location.y);
                break;
            case RIGHT:
                newLoc = new Location(location.x + 1, location.y);
                break;
        }
        if(newLoc == null || !inGrid(newLoc) || !isFreeOfObstacle(newLoc)) {
            return false;
        }
//        else if((data[newLoc.x][newLoc.y] & AGENT) != 0) {
//            return false;
//        }
        prevLocations.put(agentId, location);
        setAgPos(agentId, newLoc);
        return true;
    }

    public Location getPrevAgentLocation(int agent_id) {
        return prevLocations.get(agent_id);
    }
}