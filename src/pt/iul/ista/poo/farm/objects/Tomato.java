package pt.iul.ista.poo.farm.objects;

import pt.iul.ista.poo.farm.Farm;
import pt.iul.ista.poo.gui.ImageMatrixGUI;
import pt.iul.ista.poo.utils.Direction;
import pt.iul.ista.poo.utils.Point2D;

public class Tomato extends Vegetable {

	private static final int HEALTHY_STATE_MINIMUM=15;
	private static final int UNHEALTHY_STATE=25;
	private static final int GROW_INCREMENT=0;
	private static final int COLLECT_POINTS=3;
	
	public Tomato(Point2D p) {
		super(p, COLLECT_POINTS,HEALTHY_STATE_MINIMUM,UNHEALTHY_STATE,GROW_INCREMENT);
	}
	
	@Override
	public void interact(FarmObject fo,int key) {
		super.interact(fo, key);
		if(fo instanceof Chicken) {
			if(getCanBeEaten()) {
				if(fo.getPosition().plus(((Chicken) fo).getRandom().asVector()).equals(getPosition())) {
					ImageMatrixGUI.getInstance().removeImage(this);
					Farm.getInstance().removeObject(this);
					((Chicken) fo).resetCiclos();
				}
			}
		}
	}
}