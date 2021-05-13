import jason.environment.grid.GridWorldModel;
import jason.environment.grid.GridWorldView;

import java.awt.Color;
import java.awt.Graphics;

public class DistributedTaxiView extends GridWorldView {

    DistributedTaxiModel model;

    public DistributedTaxiView(DistributedTaxiModel model, final DistributedTaxiEnvironment env) {
        super(model, "Distributed taxi system", 1200);
        this.model = model;
        setVisible(true);
        repaint();
    }

    @Override
    public void drawAgent(Graphics g, int x, int y, Color c, int id) {
        if (id == 0) {
            super.drawAgent(g, x, y, Color.BLACK, id);
            g.setColor(Color.BLACK);
        } else if (0 < id && id < model.getNumTaxi() + 1) {
            super.drawAgent(g, x, y, Color.YELLOW, id);
            g.setColor(Color.YELLOW);
        } else {
            super.drawAgent(g, x, y, Color.GREEN, id);
            g.setColor(Color.GREEN);
        }
    }
}