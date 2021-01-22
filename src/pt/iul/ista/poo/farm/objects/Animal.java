package pt.iul.ista.poo.farm.objects;

import java.io.Serializable;

import Interfaces.AnimalMovable;
import Interfaces.Interactable;
import Interfaces.Movable;
import Interfaces.UnMovable;
import Interfaces.Updatable;
import pt.iul.ista.poo.farm.Farm;
import pt.iul.ista.poo.utils.Direction;
import pt.iul.ista.poo.utils.Point2D;
import pt.iul.ista.poo.utils.Vector2D;

public class Animal extends FarmObject implements Updatable,Interactable,AnimalMovable,UnMovable{
	
	private int ciclos;
	private String currentState;
	private Direction random;
	private boolean interacted=true;
	private static final int ANIMAL_PRIORITY=3;
	private static final int ANIMAL_LAYER=2;
	
	public void setInteracted(boolean b) {
		interacted=b;
	}
	
	public boolean getInteracted() {
		return interacted;
	}
	
	public Animal(Point2D p) {
		super(p);
		currentState=getClass().getSimpleName().toLowerCase();
	}

	public Direction getRandom() {
		return random;
	}
	
	public void setRadom(Direction d) {
		random=d;
	}
	
	@Override
	public void interact(FarmObject fo, int key) {
	}

	@Override
	public void update() {
		ciclos++;
		random=Direction.random();
		if(this!=null) {
			Farm.getInstance().animalInteraction(this,random);
		}
	}
	
	public int getCiclos() {
		return ciclos;
	}
	
	public void resetCiclos() {
		ciclos=0;
	}
	
	public void changeState(String newState) {
		currentState=newState;
	}
	
/*Movimento aleatório das instâncias Animal (assumindo que apresentam o mesmo esquema
 * de movimento do que a ovelha para as mesmas circuntâncias)
 * */	
	
	@Override
	public void move(int key) {
	}

	@Override
	public void move() {
		if(validPosition(getPosition().plus(random.asVector()))) {
			setPosition(getPosition().plus(random.asVector()));
		}
	}
	
	@Override
	public int getLayer() {
		return ANIMAL_LAYER;
	}
	
	@Override
	public String getName() {
		return currentState;
	}

	@Override
	public int getImportancia() {
		return ANIMAL_PRIORITY;
	}
}
