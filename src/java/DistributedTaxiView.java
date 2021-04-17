

import jason.environment.grid.GridWorldModel;
import jason.environment.grid.GridWorldView;

import java.awt.Color;
import java.awt.Graphics;

public class DistributedTaxiView extends GridWorldView {

    DistributedTaxiModel model;

    // TODO check if env var is needed
    public DistributedTaxiView(DistributedTaxiModel model, final DistributedTaxiEnvironment env) {
        super(model, "Distributed taxi system", 500);
        this.model = model;
        setVisible(true);
        repaint();
    }

    @Override
    public void drawAgent(Graphics g, int x, int y, Color c, int id) {
        if (id < model.getNumTaxi()) {
            super.drawAgent(g, x, y, Color.YELLOW, id);
            g.setColor(Color.YELLOW);
        }
        if (id < (model.getNumTaxi() + model.getNumClient()) && (id >= model.getNumTaxi())) {
            super.drawAgent(g, x, y, Color.GREEN, id);
            g.setColor(Color.GREEN);
        }
        if (id >= (model.getNumTaxi() + model.getNumClient())) {
            super.drawAgent(g, x, y, Color.BLACK, id);
            g.setColor(Color.BLACK);
        }
    }
}