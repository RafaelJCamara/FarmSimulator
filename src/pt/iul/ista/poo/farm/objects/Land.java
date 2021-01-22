package pt.iul.ista.poo.farm.objects;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import Audio.AePlayWave;
import Interfaces.Interactable;
import Interfaces.Updatable;
import pt.iul.ista.poo.farm.Farm;
import pt.iul.ista.poo.gui.ImageMatrixGUI;
import pt.iul.ista.poo.utils.Direction;
import pt.iul.ista.poo.utils.Point2D;


public class Land extends FarmObject implements Interactable,Updatable{

	private String currentState;
	private Vegetable plantedVegetable;
	private static final int LAND_PRIORITY=0;
	private int landAge;
	private static final int HEALTHY_LAND=75;
	private static final int LAND_LAYER=0;
	
	public Land(Point2D p, String state) {
		super(p);
		currentState=state;
	}
	
/*Expressa a atualizaão da land*/

	@Override
	public void update() {
		if(currentState.equals(getClass().getSimpleName().toLowerCase())) {
			landAge++;
			if(landAge>HEALTHY_LAND) {
				currentState="grass";
			}
		}
	}
	
	
/*Expressa a interação com a terra*/
	
	@Override
	public void interact(FarmObject fo,int key) {
		willFarmerInteract(fo,key);
		willAnimalInteract(fo,key);
	}
	

/*Verificar se o Farmer vai interagir com a terra*/

	public void willFarmerInteract(FarmObject fo,int key) {
		if(fo instanceof Farmer) {
			if(Direction.isDirection(key)&&((Farmer)fo).getInteraction()) {
				if(getPosition().equals(fo.getPosition().plus(Direction.directionFor(key).asVector()))) {
					if(currentState.equals(getClass().getSimpleName().toLowerCase())) {
						currentState="plowed";
						
						//faz som quando mete a terra a plowed

						AePlayWave plowLand=new AePlayWave("plantSound.wav");
						plowLand.start();
						
					}else {
						if(currentState.equals("plowed")) {
							if(plantedVegetable==null) {
								toPlant();
								
								
								//faz som quando se interage para plantar pela 1ª vez
								
								AePlayWave plant=new AePlayWave("plantSound.wav");
								plant.start();
								
							}
							if(plantedVegetable.getToRemove()) {
								Farm.getInstance().removeObject(plantedVegetable);
								plantedVegetable=null;
								currentState=getClass().getSimpleName().toLowerCase();
							}
						}
					}
					
					/*falta fazer o que é preciso aconter para que a relva cresca num local de terra*/
					if(currentState.equals("grass")) {
						currentState=getClass().getSimpleName().toLowerCase();
						landAge=0;
					}
				}
			}
		}
	}
	

/*Planta aleatoriamente um vegetal na terra, entre a couve e o tomate*/
	
	public void toPlant() {
		if(plantedVegetable==null) {
			if(Math.random()<0.5) {
				plantedVegetable=new Tomato(getPosition());
				ImageMatrixGUI.getInstance().addImage(plantedVegetable);
				Farm.getInstance().addObject(plantedVegetable);
			}else {
				plantedVegetable=new Cabbage(getPosition());
				ImageMatrixGUI.getInstance().addImage(plantedVegetable);
				Farm.getInstance().addObject(plantedVegetable);
			}
		}
	}	
	
	
/*Verficar se uma instância do Animal vai interagir com a terra*/	
	
	public void willAnimalInteract(FarmObject fo,int key) {
		if(fo instanceof Animal) {
			if(fo instanceof Sheep) {
				if(fo.getPosition().plus(((Animal) fo).getRandom().asVector()).equals(getPosition())) {
					if(plantedVegetable!=null && plantedVegetable.getCanBeEaten() && ((Sheep) fo).getAlimentada()==false) {
						if(((Sheep) fo).getInteracted()) {
							plantedVegetable=null;
							currentState=getClass().getSimpleName().toLowerCase();
						}
					}
				}
			}
		}
		
		if(fo instanceof Chicken) {
			if(fo.getPosition().plus(((Chicken) fo).getRandom().asVector()).equals(getPosition())) {
				if(plantedVegetable!=null && plantedVegetable.getCanBeEaten()) {
					if(plantedVegetable instanceof Tomato) {
						plantedVegetable=null;
						currentState=getClass().getSimpleName().toLowerCase();
					}
				}
			}
		}
	}
	

/*Operações sobre o aributo currentState*/
	
	@Override
	public String getName() {
		return currentState;
	}

	public void resetState() {
		currentState=getClass().getSimpleName().toLowerCase();
		plantedVegetable=null;
	}
	
/*Operações sobre o atributo plantedVegetable*/	
	
	public Vegetable getPlantedVegetable() {
		return plantedVegetable;
	}
	
	public void setPlantedVegetable(Vegetable v) {
		plantedVegetable=v;
	}
		
	
/*Colocar a terra como camada base*/
	
	@Override
	public int getLayer() {
		return LAND_LAYER;
	}

	@Override
	public int getImportancia() {
		return LAND_PRIORITY;
	}
}