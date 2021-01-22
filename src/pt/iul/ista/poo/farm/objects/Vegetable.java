
package pt.iul.ista.poo.farm.objects;

import java.io.Serializable;
import java.util.List;

import Audio.AePlayWave;
import Interfaces.Interactable;
import Interfaces.Updatable;
import pt.iul.ista.poo.farm.Farm;
import pt.iul.ista.poo.gui.ImageMatrixGUI;
import pt.iul.ista.poo.utils.Direction;
import pt.iul.ista.poo.utils.Point2D;

public class Vegetable extends FarmObject implements Updatable,Interactable{

	private boolean toRemove;	//para se poder meter a null na terra
	private String currentState;               
	private final int collect_points;	//pontos por colher o vegetal
	private int cicloPlantado;                
	private boolean vegetableInteracted;  //verificar se se interagiu com o vegetal
	private final int HEALTHY_STATE_MINIMUM;  
	private final int UNHEALTHY_STATE;		  
	private final int GROW_INCREMENT;         
	private boolean canBeEaten;  //para os animais poderem comer 
	private int numTimesInteracted; //enquanto o vegetal for verde 
	private static final int VEGETABLE_PRIORITY=1;
	private static final int VEGETABLE_LAYER=1;
	
	
/*Construtor da classe Vegetable*/
	
	public Vegetable(Point2D p,int collect_point,int hsmin,int uhs,int gi) {
		super(p);
		collect_points=collect_point;
		HEALTHY_STATE_MINIMUM=hsmin;
		UNHEALTHY_STATE=uhs;
		GROW_INCREMENT=gi;
		changeState("small_".concat(getClass().getSimpleName().toLowerCase()));
	}

/*Operações principais sobre as instâncias de Vegetable*/
	
	@Override
	public void update() {
		cicloPlantado++;
		if(vegetableInteracted) {
			if(cicloPlantado+GROW_INCREMENT*numTimesInteracted>= HEALTHY_STATE_MINIMUM && cicloPlantado+GROW_INCREMENT*numTimesInteracted<= UNHEALTHY_STATE-1) {
				changeState(getClass().getSimpleName().toLowerCase());
				canBeEaten=true;
				toRemove=true;
			}
			if(cicloPlantado+GROW_INCREMENT*numTimesInteracted>= UNHEALTHY_STATE) {
				changeState("bad_".concat(getClass().getSimpleName().toLowerCase()));
				canBeEaten=false;
				toRemove=true;
			}
		}else {
			
			if(cicloPlantado<HEALTHY_STATE_MINIMUM) {
				canBeEaten=true;
				toRemove=false;
			}
			
			if(cicloPlantado>= UNHEALTHY_STATE) {
				changeState("bad_".concat(getClass().getSimpleName().toLowerCase()));
				canBeEaten=false;
				toRemove=true;
			}
		}
	}

	@Override
	public void interact(FarmObject fo,int key) {
		willFarmerInteract(fo,key);
		willAnimalInteract(fo);
	}

/*Verifica se será o Farmer a interagir com o vegetal*/	
	
	public void willFarmerInteract(FarmObject fo, int key) {
		if(fo instanceof Farmer) {
			if(Direction.isDirection(key) && ((Farmer)fo).getInteraction()) {
				if(getPosition().equals(((Farmer)fo).getPosition().plus(Direction.directionFor(key).asVector()))) {
					
					if(currentState.equals("small_".concat(getClass().getSimpleName().toLowerCase()))) {
						vegetableInteracted=true;
						numTimesInteracted++;
					}
					
					if(currentState.equals(getClass().getSimpleName().toLowerCase())) {
						ImageMatrixGUI.getInstance().removeImage(this);
						if(canBeEaten) {
							Farm.getInstance().addPoints(collect_points);
							
							//faz som quando se colecta o vegetal (som dos pontos)
							AePlayWave collectVegetable=new AePlayWave("collectVegetable.wav");
							collectVegetable.start();
						}
						Farm.getInstance().removeObject(this);
					}
					
					if(currentState.equals("bad_".concat(getClass().getSimpleName().toLowerCase()))) {
						ImageMatrixGUI.getInstance().removeImage(this);
						Farm.getInstance().removeObject(this);
					}
				}
			}
		}
	}
	
/*Verifica se uma instância do Animal interage com o vegetal*/	
	
	public void willAnimalInteract(FarmObject fo) {
		if(fo instanceof Animal) {
			if(fo instanceof Sheep) {
				if(fo.getPosition().plus(((Sheep) fo).getRandom().asVector()).equals(getPosition())) {   
					if(((Sheep) fo).getAlimentada()==false && canBeEaten) {
						if(((Sheep) fo).getInteracted()) {
							ImageMatrixGUI.getInstance().removeImage(this);
							((Sheep) fo).changeAlimentada(true);
							((Sheep) fo).changeState(fo.getClass().getSimpleName().toLowerCase());
							((Sheep) fo).resetCiclos();
							Farm.getInstance().removeObject(this);
						}
					}
				}
			}
		}
	}
		
/*Operações sobre o atributo canBeEaten*/	
	
	public void setCanBeEaten(boolean b) {
		canBeEaten=b;
	}
	
	public boolean getCanBeEaten() {
		return canBeEaten;
	}	
	
/*Operações sobre o atributo cicloPlantado*/	
	
	public int getCicloPlantado() {
		return cicloPlantado;
	}
	
/*Operações sobre o atributo toRemove*/	
	
	public boolean getToRemove() {
		return toRemove;
	}
	
	public void setToRemove(boolean b) {
		toRemove=b;
	}

/*Operação sobre o atributo currentState*/
	
	public void changeState(String newState) {
		currentState=newState;
	}
	
	@Override
	public String getName() {
		return currentState;
	}
	
/*Colocar os vegetais numa layer superior à da base(terra)*/
	
	@Override
	public int getLayer() {
		return VEGETABLE_LAYER;
	}

	@Override
	public int getImportancia() {
		return VEGETABLE_PRIORITY;
	}
}