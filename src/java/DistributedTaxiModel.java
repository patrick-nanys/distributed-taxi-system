import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;

public class DistributedTaxiModel extends GridWorldModel {
//    public static final int CUST  = 16;
    public static final int GSize = 30;
    private int numTaxi;
    private int numClient;
    private HashMap<Integer, Location> gotoLocations = new HashMap<Integer, Location>();
    private HashMap<Integer, Location> prevLocations = new HashMap<Integer, Location>();

    protected DistributedTaxiModel(int numTaxi, int numClient) {
        super(GSize, GSize, 44);
        this.numTaxi = numTaxi;
        this.numClient = numClient;

        // set broker position
        setAgPos(0, 0, 0);

        // set taxi and client positions
        for(int i = 1; i < 1 + this.numTaxi + this.numClient; i++) {
            setAgPos(i, getFreePos());
        }
        // set go to positions
        for(int i = 1 + this.numTaxi; i < 1 + this.numTaxi + this.numClient; i++) {
            gotoLocations.put(i, getFreePos());
        }
    }

    public void placeAgent(int agentId) {
        setAgPos(agentId, getFreePos());
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
        if(newLoc == null || !inGrid(newLoc) || !isFree(newLoc)) {
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

    public Location getGotoLocation(int agent_id) {
        return gotoLocations.get(agent_id);
    }

    public Location getAgentLocation(int agentId) {
        return getAgPos(agentId);
    }
}