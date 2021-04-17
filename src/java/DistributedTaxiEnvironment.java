

import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.environment.TimeSteppedEnvironment;
import jason.environment.grid.Location;

import java.util.HashMap;
import java.util.logging.Logger;

public class DistributedTaxiEnvironment extends TimeSteppedEnvironment {
    private static Logger logger = Logger.getLogger("distributed_taxi_system.mas2j."+DistributedTaxiEnvironment.class.getName());
    private HashMap<String, Integer> agentIds = new HashMap<String, Integer>();


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
    }

    @Override
    public void init(String[] args) {
        super.init(new String[] { "1000" });
//        TODO took this out
//        setOverActionsPolicy(OverActionsPolicy.ignoreSecond);
        setSleep(1000);
        int numClient = Integer.parseInt(args[0]);
        int numTaxi = Integer.parseInt(args[1]);

        model = new DistributedTaxiModel(numTaxi, numClient);
        view = new DistributedTaxiView(model, this);
        model.setView(view);
        int counter = 0;
        for (int i = 0; i < numTaxi; i++) {
            String agName = "taxi" + (i+1);
            agentIds.put(agName, counter);
            updatePercepts(agName);
            counter++;
        }

        for (int i = 0; i < numClient; i++) {
            String agName = "client" + (i+1);
            agentIds.put(agName, counter);
            addPercept(agName, Literals.gotoLiteral(model.getGotoLocation(counter)));
            updatePercepts(agName);
            counter++;
        }

        String agName = "broker";
        agentIds.put(agName, counter);
        addPercept(agName, Literals.taxiNumLiteral(model.getNumTaxi()));
        updatePercepts(agName);

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
//        logger.info(String.format("Added at percept for agent: %s", agentName));
        addPercept(agentName, Literals.atLiteral(agentPosition));
    }

    @Override
    public boolean executeAction(String agName, Structure action) {
        Integer agentId = agentIds.get(agName);
        boolean successful = false;
        if (action.equals(Literals.MOVE_UP)) {
            successful = model.move(agentId, DistributedTaxiModel.Direction.UP);
        } else if (action.equals(Literals.MOVE_DOWN)) {
            successful = model.move(agentId, DistributedTaxiModel.Direction.DOWN);
        } else if (action.equals(Literals.MOVE_RIGHT)) {
            successful = model.move(agentId, DistributedTaxiModel.Direction.RIGHT);
        } else if (action.equals(Literals.MOVE_LEFT)) {
            successful = model.move(agentId, DistributedTaxiModel.Direction.LEFT);
        }

        updatePercepts(agName);
        return successful;
    }
}