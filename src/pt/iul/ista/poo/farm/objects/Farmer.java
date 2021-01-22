package pt.iul.ista.poo.farm.objects;

import Audio.AePlayWave;
import Interfaces.Movable;
import Interfaces.UnMovable;
import pt.iul.ista.poo.farm.Farm;
import pt.iul.ista.poo.utils.Direction;
import pt.iul.ista.poo.utils.Point2D;
import pt.iul.ista.poo.utils.Vector2D;

public class Farmer extends FarmObject implements Movable,UnMovable{

	private boolean isThereInteraction;
	private static final int FARMER_LAYER=3; 
	
/*Contrutor da classe Farmer*/	
	
	public Farmer(Point2D p) {
		super(p);
	}

	
/*Faz com que o farmer diga "catchphrases" relacionados com a sua personagem*/
	
	public void randomQuotes() {
		if(Math.random()<0.25) {
			AePlayWave rq1=new AePlayWave("maniacGun.wav");
			rq1.start();
		}else {
			if(Math.random()>=0.25 && Math.random()<0.5){
				AePlayWave rq2=new AePlayWave("criminal.wav");
				rq2.start();
			}else {
				if(Math.random()>=0.5 && Math.random()<0.75) {
					AePlayWave rq3=new AePlayWave("doMyJob.wav");
					rq3.start();
				}else {
					if(Math.random()>=0.75){
						AePlayWave rq4=new AePlayWave("goAway.wav");
						rq4.start();
					}
				}
			}
		}
	}
	
	
/*Movimento do agricultor*/
	
	public void move(int key) {
		Vector2D movimento=Direction.directionFor(key).asVector();
		if(validPosition(getPosition().plus(movimento))) {
			setPosition(getPosition().plus(movimento));
		}
	}
	
/*Coloca o agricultor na camada superior à da terra(camada base)*/
	
	@Override
	public int getLayer() {
		return FARMER_LAYER;
	}
	
//Operações sobre o atributo isThereInteraction
	
/*verifica se no próximo ciclo irá existir interação*/
	
	public void checkIntereaction() {
		isThereInteraction=true;
	}
	
/*Retorna o valor do atributo isThereInteraction*/
	
	public boolean getInteraction(){
		return isThereInteraction;
	}

/*Reinicia o valor do atributo isThereInteraction (metendo a false)*/	
	public void defaultInteraction(){
		isThereInteraction=false;
	}
}
