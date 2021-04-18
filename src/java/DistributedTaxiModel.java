import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;

public class DistributedTaxiModel extends GridWorldModel {
//    public static final int CUST  = 16;
    public static final int GSize = 60;
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
        for(int i = 1; i < 1 + this.numTaxi; i++)
            placeTaxi(i);
        for(int i = 1 + this.numTaxi; i < 1 + this.numTaxi + this.numClient; i++)
            placeClient(i);
    }

    public void placeTaxi(int agentId) {
        setAgPos(agentId, getGoodLocation());
    }

    public void placeClient(int agentId) {
        setAgPos(agentId, getGoodClientLocation());
    }

    public void removeClient(int agentId, int clientId) {
        Location agentLocation = getAgPos(agentId);
        Location clientLocation = getAgPos(clientId);
        remove(AGENT, clientLocation.x, clientLocation.y);

        // reset taken position of client that has been removed
        takenPositions[clientLocation.x][clientLocation.y] = false;
        if (inGrid(clientLocation.x, clientLocation.y-1))
            takenPositions[clientLocation.x][clientLocation.y-1] = false;
        if (inGrid(clientLocation.x, clientLocation.y+1))
            takenPositions[clientLocation.x][clientLocation.y+1] = false;

        // set taken position for taxi that removed the client
        takenPositions[agentLocation.x][agentLocation.y] = true;
    }

    public Location getGoodLocation() {
        Location location = getFreePos();
        while (takenInColumnOrRow(location.x, location.y)) {
            location = getFreePos();
        }
        takenPositions[location.x][location.y] = true;
        return location;
    }

    public Location getGoodClientLocation() {
        Location l = getFreePos();
        while (!goodLocationForClient(l)) {
            l = getFreePos();
        }

//        System.out.println("Taken positions");
//        for (int y = 0; y < GSize; y++)
//        {
//            for (int x = 0; x < GSize; x++)
//            {
////                System.out.print(x + "," + y + " ");
//                String out = takenPositions[x][y] ? "1" : "0";
//                System.out.print(out + " ");
//            }
//            System.out.println();
//        }
//        System.out.println("Going to take: " + l.x + ", " + l.y + "as client");

        takenPositions[l.x][l.y] = true;
        if (inGrid(l.x, l.y-1))
            takenPositions[l.x][l.y-1] = true;
        if (inGrid(l.x, l.y+1))
            takenPositions[l.x][l.y+1] = true;

        return l;
    }

    private boolean goodLocationForClient(Location l) {
        if (takenInColumnOrRow(l.x, l.y))
            return false;
        if (inGrid(l.x, l.y-1) && takenInColumnOrRow(l.x, l.y-1))
            return false;
        if (inGrid(l.x, l.y+1) && takenInColumnOrRow(l.x, l.y+1))
            return false;

        return true;
    }

    private boolean takenInColumnOrRow(int x, int y) {
        for (int i = 0; i < GSize; i++) {
            if (takenPositions[x][i] || takenPositions[i][y]) {
                return true;
            }
        }

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