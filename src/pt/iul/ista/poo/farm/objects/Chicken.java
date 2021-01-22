package pt.iul.ista.poo.farm.objects;

import java.util.ArrayList;
import java.util.List;

import Audio.AePlayWave;
import pt.iul.ista.poo.farm.Farm;
import pt.iul.ista.poo.gui.ImageMatrixGUI;
import pt.iul.ista.poo.utils.Direction;
import pt.iul.ista.poo.utils.Point2D;
import pt.iul.ista.poo.utils.Vector2D;

public class Chicken extends Animal{
	
	private static final int CATCH_POINTS=2;
	private List<Egg> lista=new ArrayList<>();
 	
	public Chicken(Point2D p) {
		super(p);
	}
	
	@Override
	public void update() {
		super.update();
		if(Farm.getInstance().canMove(this,getRandom().asVector())) {
			if(getCiclos()!=0) {
				if(getCiclos()%2==0) {
					move();
				}
			}
		}
	
		if((getCiclos()%10)==0) {
			if(Farm.getInstance().menorDistancia(getPosition())!=null) {
				Point2D ponto=Farm.getInstance().menorDistancia(getPosition());
				Vector2D vetor=new Vector2D(ponto.getX(),ponto.getY());
				if(Farm.getInstance().canMove(this, vetor)) {
					Egg egg=new Egg(Farm.getInstance().menorDistancia(getPosition()));
					lista.add(egg);
					Farm.getInstance().addObject(egg);
					ImageMatrixGUI.getInstance().addImage(egg);
					AePlayWave eggDrop=new AePlayWave("chickenEgg.wav");
					eggDrop.start();
				}
			}
		}
	}
	
	@Override
	public void interact(FarmObject fo, int key) {
		if(fo instanceof Farmer) {
			if(getPosition().equals(fo.getPosition().plus(Direction.directionFor(key).asVector()))) {
				if(((Farmer) fo).getInteraction()) {
					Farm.getInstance().addPoints(CATCH_POINTS);
					Farm.getInstance().removeObject(this);
					ImageMatrixGUI.getInstance().removeImage(this);
					AePlayWave chickenGone=new AePlayWave("collectEggChicken.wav");
					chickenGone.start();
				}
			}
		}
	}
}