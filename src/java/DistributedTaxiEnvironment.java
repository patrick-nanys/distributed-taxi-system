import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.environment.TimeSteppedEnvironment;
import jason.environment.grid.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class DistributedTaxiEnvironment extends TimeSteppedEnvironment {
    private static Logger logger = Logger.getLogger("distributed_taxi_system.mas2j."+DistributedTaxiEnvironment.class.getName());
    private HashMap<String, Integer> agentIds = new HashMap<String, Integer>();
    private ArrayList<String> placeableClients = new ArrayList<>();
    private static int callCounter = 0;
    private int agentCounter = 0;

    private DistributedTaxiModel model;
    private DistributedTaxiView view;

    static class Literals {
        private Literals() {}

        static final Term MOVE_UP = Literal.parseLiteral("move(up)");
        static final Term MOVE_DOWN = Literal.parseLiteral("move(down)");
        static final Term MOVE_RIGHT = Literal.parseLiteral("move(right)");
        static final Term MOVE_LEFT = Literal.parseLiteral("move(left)");

        private static Literal atLiteral(Location loc) {
            if(loc == null)
                return null;
            return Literal.parseLiteral(String.format("at(%d,%d)", loc.x, loc.y));
        }

        private static Literal taxiNumLiteral(int taxiNum) {
            return Literal.parseLiteral(String.format("taxi_num(%d)", taxiNum));
        }

        private static Literal gotoLiteral(Location loc) {
            return Literal.parseLiteral(String.format("go_to(%d,%d)", loc.x, loc.y));
        }

        private static Literal removeLiteral(String agentName) {
            return Literal.parseLiteral(String.format("remove(%s)", agentName));
        }

        private static Literal setupLiteral() {
            return Literal.parseLiteral(String.format("setup"));
        }
    }

    @Override
    public void init(String[] args) {
        super.init(new String[] { "75" });
//        TODO took this out
//        setOverActionsPolicy(OverActionsPolicy.ignoreSecond);
        int numClient = Integer.parseInt(args[0]);
        int numTaxi = Integer.parseInt(args[1]);

        model = new DistributedTaxiModel(numTaxi, numClient);
        view = new DistributedTaxiView(model, this);
        model.setView(view);

        String agName = "broker";
        agentIds.put(agName, agentCounter);
        addPercept(agName, Literals.taxiNumLiteral(model.getNumTaxi()));
        updatePercepts(agName);

        for (int i = 0; i < numTaxi; i++) {
            agName = "taxi" + (i+1);
            agentIds.put(agName, ++agentCounter);
            updatePercepts(agName);
        }

        for (int i = 0; i < numClient; i++) {
            agName = "client" + (i+1);
            agentIds.put(agName, ++agentCounter);
            addPercept(agName, Literals.gotoLiteral(model.getGoodLocation()));
            updatePercepts(agName);
        }

    }

    private void updatePercepts(String agentName) {
//        clearPercepts(agentName);
        int agentId = agentIds.get(agentName);
        Location prevAgentPosition = model.getPrevAgentLocation(agentId);
        Location agentPosition = model.getAgPos(agentId);

        Literal prevPositionLiteral = Literals.atLiteral(prevAgentPosition);
        if(prevPositionLiteral != null) {
//            logger.info(String.format("Removed at percept for agent: %s", agentName));
            removePercept(agentName, prevPositionLiteral);
        }
//        logger.info(String.format("Added at percept for agent: %s (%d,%d)", agentName, agentPosition.x, agentPosition.y));
        addPercept(agentName, Literals.atLiteral(agentPosition));
    }

    @Override
    public boolean executeAction(String agName, Structure action) {
        int agentId = agentIds.get(agName);
        boolean successful = false;
        if (action.equals(Literals.MOVE_UP)) {
            successful = model.move(agentId, DistributedTaxiModel.Direction.UP);
        } else if (action.equals(Literals.MOVE_DOWN)) {
            successful = model.move(agentId, DistributedTaxiModel.Direction.DOWN);
        } else if (action.equals(Literals.MOVE_RIGHT)) {
            successful = model.move(agentId, DistributedTaxiModel.Direction.RIGHT);
        } else if (action.equals(Literals.MOVE_LEFT)) {
            successful = model.move(agentId, DistributedTaxiModel.Direction.LEFT);
        } else if (action.getFunctor().equals("remove")) {
            String clientName = String.valueOf(action.getTerm(0));
            int clientId = agentIds.get(clientName);

            model.removeClient(agentId, clientId);
            placeableClients.add(clientName);

            successful = true;
        }
        callCounter++;
        if (callCounter % 85 == 0) {
            if (placeableClients.size() > 0) {
                String clientToPlace = placeableClients.get(0);
                placeableClients.remove(0);
                int clientToPlaceId = agentIds.get(clientToPlace);

                clearPercepts(clientToPlace);
//                logger.info(String.format("Cleared all percepts for agent: %s", clientToPlace));
                model.placeClient(clientToPlaceId);
                updatePercepts(clientToPlace);
                addPercept(clientToPlace, Literals.gotoLiteral(model.getGoodLocation()));
                addPercept(clientToPlace, Literals.setupLiteral());
            }
        }
//        logger.info(String.format("Action functor is: %s", action.getFunctor()));

        updatePercepts(agName);
        return successful;
    }
}