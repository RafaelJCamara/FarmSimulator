package pt.iul.ista.poo.farm.objects;

import Audio.AePlayWave;
import pt.iul.ista.poo.farm.Farm;
import pt.iul.ista.poo.utils.Direction;
import pt.iul.ista.poo.utils.Point2D;

public class Sheep extends Animal{

	private static final int FEEDED_POINTS=1;
	private static final int FEED_MINIMUM=10;
	private static final int FEED_MAXIMUM=20;
	private boolean alimentada=true;
	
	public Sheep(Point2D p) {
		super(p);
	}
	
	public void changeAlimentada(boolean b) {
		alimentada=b;
	}
	
	public boolean getAlimentada() {
		return alimentada;
	}
	
	@Override
	public void interact(FarmObject fo, int key) {
		if(fo instanceof Farmer) {
			if(getPosition().equals(((Farmer)fo).getPosition().plus(Direction.directionFor(key).asVector()))) {
				if(((Farmer) fo).getInteraction()) {
					resetCiclos();
					alimentada=true;
					setInteracted(true);
					if(getName().equals(("famished_").concat(getClass().getSimpleName().toLowerCase()))) {
						changeState(getClass().getSimpleName().toLowerCase());
						AePlayWave feedSheep=new AePlayWave("imSorry.wav");
						feedSheep.start();
					}
				}
			}
		}
	}

	@Override
	public void update() {
		super.update();
		if(getCiclos()>FEED_MINIMUM && getCiclos()<=FEED_MAXIMUM) {
			if(Farm.getInstance().canMove(this, getRandom().asVector())) {
				if(!alimentada) {
					move();
				}
			}
			alimentada=false;
		}
		if(getCiclos()>FEED_MAXIMUM) {
			changeState("famished_".concat(getClass().getSimpleName().toLowerCase()));
			alimentada=false;
			setInteracted(false);
		}
		if(alimentada) {
			Farm.getInstance().addPoints(FEEDED_POINTS);
		}
	}
}