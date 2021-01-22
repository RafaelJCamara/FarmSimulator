package pt.iul.ista.poo.farm.objects;

import pt.iul.ista.poo.gui.ImageTile;
import pt.iul.ista.poo.utils.Point2D;

import java.io.Serializable;

import pt.iul.ista.poo.farm.*;

public abstract class FarmObject implements ImageTile,Serializable {

	private Point2D position;
	private static final int BASE_LAYER=0;
	
	public FarmObject(Point2D p) {
		position = p;
	}

	@Override
	public String getName() {
		return getClass().getSimpleName().toLowerCase();
	}

	@Override
	public Point2D getPosition() {
		return position;
	}

/*Atualiza a posicao*/
	
	public void setPosition(Point2D newPosition) {
		position=newPosition;
	}
	
	
/*Verifica se um ponto tem coordenadas válidas de acordo com a grelha*/
	
	public boolean validPosition(Point2D ponto) {
		if(ponto.getX()>=0&&ponto.getX()<Integer.parseInt(Farm.getInstance().lerDimensao("FarmSize.txt").get(1))) {
			if(ponto.getY()>=0&&ponto.getY()<Integer.parseInt(Farm.getInstance().lerDimensao("FarmSize.txt").get(0))) {
				return true;
			}
		}
		return false;
	}
		
	@Override
	public int getLayer() {
		return BASE_LAYER;
	}
}
