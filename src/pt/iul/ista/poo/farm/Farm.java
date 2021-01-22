 
package pt.iul.ista.poo.farm;

import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.Vector;
import javax.swing.JOptionPane;
import Audio.AePlayWave;
import Interfaces.Interactable;
import Interfaces.UnMovable;
import Interfaces.Updatable;
import pt.iul.ista.poo.farm.objects.*;
import pt.iul.ista.poo.gui.ImageMatrixGUI;
import pt.iul.ista.poo.gui.ImageTile;
import pt.iul.ista.poo.utils.Direction;
import pt.iul.ista.poo.utils.Point2D;
import pt.iul.ista.poo.utils.Vector2D;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import javax.sound.sampled.*;

/**
 * Data: 27-05-2018
 * @author Rafael Câmara (82036)
 * @version 1.0
 */

public class Farm implements Observer,Serializable {

	private static final int MIN_X = 5;
	private static final int MIN_Y = 5;
	private Farmer farmer;
	private Animal sheep1;
	private Animal sheep2;
	private Animal chicken1;
	private Animal chicken2;
	private int points;
	private int gameLevel;
	private static Farm INSTANCE;
	private int max_x;
	private int max_y;
	private List<FarmObject> mainList=new ArrayList<>();
	private HashMap<Integer,Integer> gameLevels=new HashMap<>();
	
/*----------------------------------------------------------------------------------*/	
	
/*Método construtor da Farm*/
	
	private Farm(String file) {
		
		if(Integer.parseInt(lerDimensao(file).get(1)) <MIN_X || Integer.parseInt(lerDimensao(file).get(0))<MIN_Y) {
			throw new IllegalArgumentException("Dimensões inválidas!");
		}else {
			max_x = Integer.parseInt(lerDimensao(file).get(1));
			max_y = Integer.parseInt(lerDimensao(file).get(0));
		}

		INSTANCE = this;
		
		/*Inicializa o agricultor*/
		farmer=new Farmer(new Point2D(0,0));
		mainList.add(farmer);
		
		/*Inicializa as ovelhas */
		
		//ovelha 1
		sheep1=new Sheep(randomPoint());
		mainList.add(sheep1);
		
		//ovelha 2
		sheep2=new Sheep(randomPoint());
		mainList.add(sheep2);
		
		//galinha 1
		chicken1=new Chicken(randomPoint());
		mainList.add(chicken1);
		
		//galinha 2
		chicken2=new Chicken(randomPoint());
		mainList.add(chicken2);
		
		ImageMatrixGUI.setSize(max_x, max_y);
		addLand();
		loadScenario();
		loadGameLevels();
	}
	
/*-------------------------------------------------------------------------------------------------------------*/	
	
/*Carrega a HashTable com os valores escolhidos para serem os níveis do jogo(relacionados com a pontuação)*/
	
	private void loadGameLevels() {
		gameLevels.put(1, 20);
		gameLevels.put(2, 75);
		gameLevels.put(3, 150);
		gameLevels.put(4, 200);
		gameLevels.put(5, 300);
		gameLevels.put(6, 400);
		gameLevels.put(7, 500);
		gameLevels.put(8, 700);
		gameLevels.put(9, 1000);
		gameLevels.put(10, 1500);
		gameLevels.put(11, 1800);
		gameLevels.put(12, 2100);
		gameLevels.put(13, 2500);
		gameLevels.put(14, 2800);
		gameLevels.put(15, 3000);
	}

/*Indica o nível de jogo atual*/	
	
	public Integer getGameLevel(int points) {
		for(Integer i:gameLevels.keySet()) {
			if(points<gameLevels.get(i)) {
				return i-1;
			}
		}
		return 0;
	}
	
/*Atualiza o nível de jogo*/
	
	public void updateGameLevel() {
		int auxLevel=gameLevel;
		gameLevel=getGameLevel(points);
		if(gameLevel>auxLevel) {
			AePlayWave levelUp=new AePlayWave("levelUp.wav");
			levelUp.start();
		}
	}
	
/*------------------------------------------------------------------------------------------*/	
	
	private void registerAll() {
		for(FarmObject f:mainList) {
			ImageMatrixGUI.getInstance().addImage(f);
		}
		ImageMatrixGUI.getInstance().update();
	}

/*---------------------------------------------------------------------------------*/	
	
	private void loadScenario() {
		registerAll();
	}

/*---------------------------------------------------------------------------------*/	
	
	@Override
	public void update(Observable gui, Object a) {
		System.out.println("Update sent " + a);
		// TODO
		
		/*Verifica se existe interação no próximo ciclo*/
		if((Integer)a==KeyEvent.VK_SPACE) {
			farmer.checkIntereaction();
		}
		
		
		/*Grava o jogo quando se pressiona a tecla S*/
		if((Integer)a ==KeyEvent.VK_S) {
			saveGame("jogo.bin");
			System.out.println("Jogo gravado com sucesso.");
		}
		
		/*Carrega o jogo quando se pressiona a tecla L*/
		if((Integer)a ==KeyEvent.VK_L) {
			loadGame("jogo.bin");
			System.out.println("Jogo carregado com sucesso.");
		}
		
		
		/*Tecla pressionada válida para movimento*/
		if (a instanceof Integer) {
			int key = (Integer) a;
			if (Direction.isDirection(key)) {				
				/*Atualização do movimento do agricultor*/
				if(!farmer.getInteraction()) {
					if(canMove(farmer,Direction.directionFor(key).asVector())) {
						farmer.move(key);
					}
				}
				
				/*Faz as ações essenciais do jogo*/
				updateAll();
				farmerInteracts(key);
				farmer.defaultInteraction();
				updateGameLevel();
				
				/*meter o farmer a dizer coisas aleatoriamente*/
				if(Math.random()<0.015) {
					farmer.randomQuotes();
				}
				
				/*reproduzir o som de uma quinta*/
				if(Math.random()<0.02) {
					AePlayWave farmSounds=new AePlayWave("farmSounds.wav");
					farmSounds.start();
				}
			}
		}
		
		ImageMatrixGUI.getInstance().setStatusMessage("Points: " + points + "    Game Level : " +gameLevel);
		ImageMatrixGUI.getInstance().update();
	}
		
/*---------------------------------------------------------------------------------------*/	

/*Descreve o update das instâncias Updatable*/
	
	public void updateAll() {
		
		List<FarmObject> aux=new ArrayList<>();
		
		for(FarmObject f:mainList) {
			aux.add(f);
		}
		
		for(FarmObject f:aux) {
			if(f instanceof Updatable) {
				((Updatable) f).update();
			}
		}
	}
	
/*-----------------------------------------------------------------------------------------*/	
	
/*Descreve a interação que ocorre entre os animais e os objetos da quinta (com os quais pode interagir)*/	
	
	public void animalInteraction(Animal animal,Direction direction) {
		List<FarmObject> aux=new ArrayList<>();
		for(FarmObject f:mainList) {
			aux.add(f);
		}
		for(FarmObject fo:aux) {
			if(fo instanceof Interactable) {
				if(fo!=null) {
					if(fo.getPosition().equals(animal.getPosition().plus(direction.asVector()))) {
						((Interactable) fo).interact(animal, 0);
					}
				}
			}
		}
	}
	
/*-------------------------------------------------------------------------------------------*/	
	
//Tudo o abaixo expresso serve para fazer com que o farmer apenas interaga com um objeto numa determinada posição
	
	
/*O farmer interage com o objeto de maior importancia
 * Caso seja um vegetal, também interage com a terra correspondente
 * */	
	
	public void farmerInteracts(int key) {
		List<FarmObject> aux= new ArrayList<>();
		for(FarmObject fo:mainList) {
			aux.add(fo);
		}
		Interactable i=maisImportancia(farmer.getPosition().plus(Direction.directionFor(key).asVector()));
		if(i!=null) {
			i.interact(farmer, key);
			if(i instanceof Vegetable) {
				for(FarmObject fo:aux) {
					if(fo instanceof Land) {
						if(fo.getPosition().equals(((Vegetable) i).getPosition())) {
							((Land) fo).interact(farmer, key);
						}
					}
				}
			}
		}
	}
	
/*Retorna o interactable com maior importancia, para uma determinada posição*/
	
	public Interactable maisImportancia(Point2D ponto) {
		int max=-1;
		Interactable i=null;
		for(FarmObject fo:mainList) {
			if(fo instanceof Interactable) {
				if(fo.getPosition().equals(ponto)) {
					if(((Interactable) fo).getImportancia()>max) {
						max=((Interactable) fo).getImportancia();
						i=(Interactable)fo;
					}
				}
			}
		}	
		return i;
	}
	
/*---------------------------------------------------------------------------------------*/	

/*Adiciona uma instância à lista principal de objectos*/	
	
	public void addObject(FarmObject fo) {
		mainList.add(fo);
	}
	
/*Remover uma instância da lista principal de objectos*/
	
	public void removeObject(FarmObject fo) {
		mainList.remove(fo);
	}
	
/*---------------------------------------------------------------------------------------*/	
	
/*Adiciona pontos ao jogo*/	
	
	public void addPoints(int pontos) {
		points+=pontos;
	}
	
/*---------------------------------------------------------------------------------------*/	

/*Adicionar terra ao ecrã (usado no registerAll())*/

	public void addLand() {
		for(int w=0;w!=max_x;w++) {
			for(int h=0;h!=max_y;h++) {
				if(Math.random()<=0.9) {
					Land land=new Land(new Point2D(w,h),"land");
					mainList.add(land);
				}else {
					Land rocky=new Land(new Point2D(w,h),"rock");
					mainList.add(rocky);
				}
			}
		}
	}

/*----------------------------------------------------------------------------------------*/	
	
/*Verifica se um objecto se pode mover de modo a não colidir com outros*/
	
	public boolean canMove(FarmObject fo, Vector2D v) {
		for(FarmObject f:mainList) {
			if(f instanceof UnMovable) {
				if(f.getPosition().equals(fo.getPosition().plus(v))) {
					return false;
				}
			}	
		}
		return true;
	}
	
/*----------------------------------------------------------------------------------------------------*/	
	
//Posições ocupadas e posições desocupadas	
	
	
/*Retorna uma lista com as posições que estão ocupadas pelos objetos UnMovable*/
	
	public List<Point2D> unAvailablePositions(){
		List<Point2D> lista=new ArrayList<>();
		for(FarmObject fo:mainList) {
			if(fo instanceof UnMovable) {
				lista.add(fo.getPosition());
			}
		}
		return lista;
	}
	
/*Retorna uma lista com todas as posições disponíveis (exceto as dos UnMovable)*/
	
	public List<Point2D> availablePositions(){
		List<Point2D> lista=new ArrayList<>();
		for(FarmObject fo:mainList) {
			if((fo instanceof UnMovable)==false) {
				lista.add(fo.getPosition());
			}
		}
		return lista;
	}
	
/*-------------------------------------------------------------------------------------------------------------------------------*/	
	
/*Gerar posições no ecrã aleatórias (excluir das opções a posição (0,0) do farmer)*/
	
	public Point2D randomPoint() {
		Point2D pontoFinal=new Point2D((int)(Math.random()*Integer.parseInt(lerDimensao("FarmSize.txt").get(1))),(int)(Math.random()*Integer.parseInt(lerDimensao("FarmSize.txt").get(0))));
		List<Point2D> lista=unAvailablePositions();
		if(lista.contains(pontoFinal)==false && pontoFinal.equals(new Point2D(0,0))==false) {
			return pontoFinal;
		}else {
			return randomPoint();
		}
	}
	
/*----------------------------------------------------------------------------------------------------*/	

/*Dada uma referencia, calcula-se a menor distância, tendo por base apenas posições livres*/
	
	public Point2D menorDistancia(Point2D referencia) {
		List<Point2D> listaCandidatos=availablePositions();
		List<Point2D> listaOcupados=unAvailablePositions();
		double distancia=1000;
		Point2D pontoFinal=null;
		for(Point2D ponto:listaCandidatos) {
			double novaDistancia=Math.abs(Math.sqrt((referencia.getX()-ponto.getX())*(referencia.getX()-ponto.getX())+(referencia.getY()-ponto.getY())*(referencia.getY()-ponto.getY())));
			if(novaDistancia!=0 && novaDistancia<distancia) {
				if(listaOcupados.contains(ponto)==false) {
					pontoFinal=ponto;
					distancia=novaDistancia;
				}
			}
		}
		return pontoFinal;
	}
	
/*----------------------------------------------------------------------------------------------------------------------------*/	
	
/*Leitura da dimensão da quinta*/
	
	public static List<String> lerDimensao(String nomeFicheiro){
		ArrayList<String> dimensao=new ArrayList<>();
		try {
			Scanner fs=new Scanner(new File(nomeFicheiro));
			while(fs.hasNextLine()) {
				String line=fs.nextLine();
				String info[]=line.split(" ");
				
				//linhas (equivalente ao y)
				dimensao.add(info[0]);
				
				//colunas (equivalente ao x)
				dimensao.add(info[1]);
			}
			fs.close();
		}catch(FileNotFoundException e){
			System.out.println("Erro na abertura do ficheiro!");
		}
		return dimensao;
	}
	
/*---------------------------------------------------------------------------------------------------------------------------*/	
	
/*Faz a gravacao/save do jogo*/
	
	public void saveGame(String nomeFicheiro) {
		try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(nomeFicheiro))){			
			output.writeObject(max_x);
			output.writeObject(max_y);
			output.writeObject(points);
			output.writeObject(gameLevel);
			output.writeObject(mainList);
		} catch (IOException e) {
			System.out.println("Problemas com a escrita do ficheiro.");
		}
	}
	
/*---------------------------------------------------------------------------------------------------------------------------*/	
	
//Carregamento do jogo
	
/*Carregamento principal do jogo*/	
	
	public void loadGame(String nomeFicheiro) {

		try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(nomeFicheiro))){
			int auxX=(int)input.readObject();
			int auxY=(int)input.readObject();
			
			if(auxX!=max_x || auxY!=max_y) {
				return;
			}else {
				points=(int) input.readObject();
				gameLevel=(int) input.readObject();
				@SuppressWarnings("unchecked")
				List<FarmObject> objetos = (List<FarmObject>) input.readObject();
				loadGameAux(objetos);
			}
		} catch (IOException e) {
			System.out.println("Problemas com a leitura do ficheiro.");
			
		} catch (ClassNotFoundException e) {
			System.out.println("Problemas com o formato dos dados - classe de objectos não encontrada.");
		}
	}
	
/*Auxílio para o carregamento do jogo*/	
	
	public void loadGameAux(List<FarmObject> lista) {
		List<FarmObject> listaCarregada=lista;
		List<FarmObject> aux=new ArrayList<>();
		for(FarmObject fo:mainList) {
			aux.add(fo);
		}
		
		for(FarmObject fo:aux) {
			mainList.remove(fo);
			ImageMatrixGUI.getInstance().removeImage(fo);
		}

		for(FarmObject fo:listaCarregada) {
			mainList.add(fo);
			if(fo instanceof Farmer) {
				farmer=(Farmer) fo;
			}
			ImageMatrixGUI.getInstance().addImage(fo);
		}
	}
	
/*---------------------------------------------------------------------------------------------------------------------------*/
	
	private void play() {
		ImageMatrixGUI.getInstance().addObserver(this);
		ImageMatrixGUI.getInstance().go();
	}
	public static Farm getInstance() {
		assert (INSTANCE != null);
		return INSTANCE;
	}
	public static void main(String[] args) {
		Farm f = new Farm("FarmSize.txt");
		f.play();
		AePlayWave inicio=new AePlayWave("introSong.wav");
		inicio.start();
	}
}