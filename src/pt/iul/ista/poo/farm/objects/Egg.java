package pt.iul.ista.poo.farm.objects;

import java.io.Serializable;

import Audio.AePlayWave;
import Interfaces.Interactable;
import Interfaces.Movable;
import Interfaces.UnMovable;
import Interfaces.Updatable;
import pt.iul.ista.poo.farm.Farm;
import pt.iul.ista.poo.gui.ImageMatrixGUI;
import pt.iul.ista.poo.utils.Direction;
import pt.iul.ista.poo.utils.Point2D;
import pt.iul.ista.poo.utils.Vector2D;

public class Egg extends FarmObject implements Updatable,Interactable,UnMovable{

	private int ciclos;
	private static final int COLLECT_POINTS=1;
	private Chicken chicken;
	private static final int EGG_PRIORITY=2;
	private static final int EGG_LAYER=2; 
	
	public Egg(Point2D p) {
		super(p);
	}

	@Override
	public void update() {
		ciclos++;
		if(ciclos==20) {
			if(Farm.getInstance().menorDistancia(getPosition())!=null) {
				Point2D ponto=Farm.getInstance().menorDistancia(getPosition());
				Vector2D vetor=new Vector2D(ponto.getX(),ponto.getY());
				if(Farm.getInstance().canMove(this, vetor)) {
					chicken=new Chicken(Farm.getInstance().menorDistancia(this.getPosition()));
					Farm.getInstance().addObject(chicken);
					ImageMatrixGUI.getInstance().addImage(chicken);
					Farm.getInstance().removeObject(this);
					ImageMatrixGUI.getInstance().removeImage(this);
				}
			}
		}
	}
	
	@Override
	public void interact(FarmObject fo, int key) {
		if(fo instanceof Farmer) {
			if(((Farmer) fo).getInteraction()) {
				if(getPosition().equals(fo.getPosition().plus(Direction.directionFor(key).asVector()))) {
					Farm.getInstance().addPoints(COLLECT_POINTS);
					Farm.getInstance().removeObject(this);
					ImageMatrixGUI.getInstance().removeImage(this);
					AePlayWave collectEgg=new AePlayWave("collectEggChicken.wav");
					collectEgg.start();
				}
			}
		}
	}

	@Override
	public int getImportancia() {
		return EGG_PRIORITY;
	}
	
	@Override
	public int getLayer() {
		return EGG_LAYER;
	}
}
