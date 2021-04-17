import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.environment.TimeSteppedEnvironment;
import jason.environment.grid.Location;

public class DistributedTaxiEnvironment extends TimeSteppedEnvironment {
    private Logger logger = Logger.getLogger("naatho_ier.mas2j."+SmartShoppingEnvironment.class.getName());
    private HashMap<String, Integer> agentIds = new HashMap<String, Integer>();
    ReentrantLock lock = new ReentrantLock(true);
    private HashMap<String, Location> distributer = new HashMap<String, Location>();
    private HashMap<String, Integer> goHomeIds = new HashMap<String, Integer>();


    private SmartShopModel model;
    private SmartShopView view;

    private int numCustomer = 40;
    private int numCashier = 3;
    private int numDistributer = 1;

    static class Literals {
        private Literals() {}

        static final Term MOVE_UP = Literal.parseLiteral("move(up)");
        static final Term MOVE_DOWN = Literal.parseLiteral("move(down)");
        static final Term MOVE_RIGHT = Literal.parseLiteral("move(right)");
        static final Term MOVE_LEFT = Literal.parseLiteral("move(left)");
        static final Term GO_HOME = Literal.parseLiteral("go_home");

        private static Literal atLiteral(Location loc) {
            return Literal.parseLiteral(String.format("at(%d,%d)", loc.x, loc.y));
        }

        private static Literal canStepLiteral() {
            return Literal.parseLiteral(String.format("can_step"));
        }

        private static Literal cashierAtLiteral(String name, int x, int y) {
            return Literal.parseLiteral(String.format("cashier_at(%s, %d, %d)", name, x, y));
        }

        private static Literal distributerAtLiteral(int x, int y) {
            return Literal.parseLiteral(String.format("distributer_at(%d, %d)", x, y));
        }

        private static Literal nameLiteral(String name) {
            return Literal.parseLiteral(String.format("name(%s)", name));
        }

        private static Literal homeAtLiteral(int x, int y) {
            return Literal.parseLiteral(String.format("home_at(%d, %d)", x, y));
        }

        private static Literal removeLiteral(String name) {
            return Literal.parseLiteral(String.format("remove(%s)", name));
        }

        private static Literal custCountLiteral(Integer count) {
            return Literal.parseLiteral(String.format("custcount(%d)", count));
        }

    }


    @Override
    public void init(String[] args) {
        super.init(new String[] { "1000" } );
        setOverActionsPolicy(OverActionsPolicy.ignoreSecond);

        model = new SmartShopModel(numCustomer, numCashier, numDistributer);
        view = new SmartShopView(model, this);
        model.setView(view);
        int counter = 0;
        for (int i = 0; i < numCustomer; i++) {
            String agName = "customer" + (i+1);
            agentIds.put(agName, counter++);
            updatePercepts(agName);
        }

        for (int i = 0; i < numCashier; i++) {
            String agName = "cashier" + (i+1);
            agentIds.put(agName, counter++);
            updatePercepts(agName);
            addPercept(agName, Literals.custCountLiteral(0));
        }

        String agName = "distributer";
        agentIds.put(agName, counter);
        updatePercepts(agName);

    }

    private void updatePercepts(String agentName) {
        clearPercepts(agentName);
        int agentId = agentIds.get(agentName);
        Location agentPosition = model.getAgPos(agentId);

        addPercept(agentName, Literals.atLiteral(agentPosition));

        HashMap<Integer, Location> cashierLocations = model.getCashierAt();
        Iterator iterator = cashierLocations.entrySet().iterator();

        if (agentName.contains("customer")) {

            addPercept(agentName, Literals.distributerAtLiteral(16, 16));
            addPercept(agentName, Literals.nameLiteral(agentName));
            addPercept(agentName, Literals.canStepLiteral());
        }


        if (agentName.contains("distributer")) {
            int count = 1;
            while(iterator.hasNext()) {
                Map.Entry cashier = (Map.Entry) iterator.next();
                Location location = (Location) cashier.getValue();
                addPercept(agentName, Literals.cashierAtLiteral("cashier" + Integer.toString(count++), location.x, location.y));
            }
        }


        for (Map.Entry<String, Integer> entry : goHomeIds.entrySet()) {
            Location location = new Location(13, 30);
            addPercept(entry.getKey(), Literals.homeAtLiteral(location.x, location.y));
        }

    }



    @Override
    public boolean executeAction(String agName, Structure action) {
        lock.lock();
        Integer agentId = agentIds.get(agName);
        boolean successful = false;
        if (action.equals(Literals.MOVE_UP)) {
            successful = model.move(agentId, SmartShopModel.Direction.UP);
        } else if (action.equals(Literals.MOVE_DOWN)) {
            successful = model.move(agentId, SmartShopModel.Direction.DOWN);
        } else if (action.equals(Literals.MOVE_RIGHT)) {
            successful = model.move(agentId, SmartShopModel.Direction.RIGHT);
        } else if (action.equals(Literals.MOVE_LEFT)) {
            successful = model.move(agentId, SmartShopModel.Direction.LEFT);
        } else if (action.equals(Literals.GO_HOME)) {
            go_home(agName);
        } else if (action.equals(Literals.removeLiteral(agName))) {
            model.remove(2, 13, 30);
        }

        updatePercepts(agName);
        lock.unlock();
        return successful;
    }

    public void go_home(String agName) {
        goHomeIds.put(agName, agentIds.get(agName));
    }
}