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
    private boolean takenPositions[][] = new boolean[GSize][GSize];
    private HashMap<Integer, Location> prevLocations = new HashMap<Integer, Location>();

    protected DistributedTaxiModel(int numTaxi, int numClient) {
        super(GSize, GSize, 44);
        this.numTaxi = numTaxi;
        this.numClient = numClient;

        // set broker position
        setAgPos(0, 0, 0);
        takenPositions[0][0] = true;

        // set taxi and client positions
        for(int i = 1; i < 1 + this.numTaxi + this.numClient; i++)
            placeAgent(i);
    }

    public void placeAgent(int agentId) {
        setAgPos(agentId, getGoodLocation());
    }

    public void removeAgent(int agentId) {
        Location agentLocation = getAgPos(agentId);
        remove(AGENT, agentLocation.x, agentLocation.y);
        takenPositions[agentLocation.x][agentLocation.y] = false;
    }

    public Location getGoodLocation() {
        Location location = getFreePos();
        while (takenInColumnOrRow(location.x, location.y)) {
            location = getFreePos();
        }
        takenPositions[location.x][location.y] = true;
        return location;
    }

    private boolean takenInColumnOrRow(int x, int y) {
        for (int i = 0; i < GSize; i++)
            if (takenPositions[x][i] || takenPositions[i][y])
                return true;

        return false;
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

        // update taken positions
        takenPositions[location.x][location.y] = false;
        takenPositions[newLoc.x][newLoc.y] = true;

        prevLocations.put(agentId, location);
        setAgPos(agentId, newLoc);
        return true;
    }

    public Location getPrevAgentLocation(int agent_id) {
        return prevLocations.get(agent_id);
    }

    public Location getAgentLocation(int agentId) {
        return getAgPos(agentId);
    }
}