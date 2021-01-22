package pt.iul.ista.poo.farm.objects;

import pt.iul.ista.poo.gui.ImageMatrixGUI;
import pt.iul.ista.poo.utils.Direction;
import pt.iul.ista.poo.utils.Point2D;

public class Cabbage extends Vegetable {
	/**
	 * inteiro que indica o ciclo m�nimo em que uma couve amadurece
	 */
	private static final int HEALTHY_STATE_MINIMUM=10;
	/**
	 *inteiro que indica o ciclo m�ximo em que uma couve est� madura, antes de apodrecer 
	 */
	private static final int UNHEALTHY_STATE=30;
	/**
	 *inteiro que indica quantos pontos recebemos por coletar uma couve 
	 */
	private static final int COLLECT_POINTS=2;
	/**
	 * inteiro que indica quanto cresce uma couve ao se interagir com a mesma
	 */
	private static final int GROW_INCREMENT=1;
	
	/**
	 * Construtor para uma couve
	 * @param p Ponto para indicar a posi��o em que vai ser criada a couve
	 */
	public Cabbage(Point2D p) {
		super(p, COLLECT_POINTS,HEALTHY_STATE_MINIMUM,UNHEALTHY_STATE,GROW_INCREMENT);
	}
	
	/**
	 * M�todo para fazer a atualiza��o de uma couve
	 * Aqui tratamos de forma especial um caso em espec�fico
	 * Da� se recorrer primeiro ao que foi definido como update na superclasse
	 */
	public void update() {
		super.update();
		if(getCicloPlantado()>= HEALTHY_STATE_MINIMUM && getCicloPlantado()<= UNHEALTHY_STATE-1) {
			changeState(getClass().getSimpleName().toLowerCase());
			setCanBeEaten(true);
			setToRemove(true);
		}
	}

	@Override
	public String toString() {
		return "A couve encontra-se na posi��o ("+getPosition().getX() +","+getPosition().getY()+")";
	}
}