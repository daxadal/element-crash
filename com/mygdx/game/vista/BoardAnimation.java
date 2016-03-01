package com.mygdx.game.vista;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.controlador.GameType;
import com.mygdx.game.controlador.StuffList;
import com.mygdx.game.modelo.tableros.Tablero;

/**
 * Implementa la animación de los caramelos del tablero. Contiene un {@link AnimationCell} por casilla,
 * y activa las animaciones de estos.
 */
public class BoardAnimation implements Tablero.Observer{
	
	/**
	 * Guarda los parámetros para crear activar más tarde la animación en el
	 *  {@link AnimationCell} correspondiente 
	 */
	private class AnimationTask {
		private AnimationType movement;
		private int filaIni;
		private int filaFin;
		private int colIni;
		private int colFin;
		private Texture icon;
		
		/**
		 * Guarda los parámetros para crear un {@link AnimationCell} más tarde.
		 * En caso de SWAP, sólo es necesario crear uno de los dos. <br>
		 * La función activate crea el más apropiado según los parámetros
		 * @param movement tipo de movimento
		 * @param filaIni fila origen
		 * @param colIni columna origen
		 * @param filaFin fila destino (si no hay movimento vertical su valor el de filaIni)
		 * @param colFin columna destino (si no hay movimento horizontal su valor es el de colIni)
		 * @param icon caramelo (sólo relevante en CREATE. Si tiene valor null, se coge el sprite
		 * de la posicion de origen)
		 */
		public AnimationTask(AnimationType movement, int filaIni, int colIni, int filaFin, int colFin, Texture icon) {
			this.movement = movement;
			this.filaIni = filaIni;
			this.filaFin = filaFin;
			this.colIni = colIni;
			this.colFin = colFin;
			this.icon = icon;
		}

		/**
		 * Modifica el {@link AnimationCell} corerspondiente según los parámetros guardados
		 * y lo añade a la lista {@link BoardAnimation#actualList} de animaciones en curso
		 */
		public void activate() {
			switch (movement) {
			case DESTROY: 
				animCell[filaFin][colFin].newDestroy();
				actualList.add(animCell[filaFin][colFin]);
				break;
				
			case FALL:
				if (icon == null)
					icon = animCell[filaIni][colIni].getIcon();
				
				animCell[filaFin][colFin].newFall(filaIni, colIni, filaFin, icon);
				actualList.add(animCell[filaFin][colFin]);
				break;
				
			case FALL_L:
				if (icon == null)
					icon = animCell[filaIni][colIni].getIcon();
				
				animCell[filaFin][colFin].newFallToLeft(filaIni, colIni, colFin, icon);
				actualList.add(animCell[filaFin][colFin]);
				break;
				
			case FALL_R:
				if (icon == null)
					icon = animCell[filaIni][colIni].getIcon();
				
				animCell[filaFin][colFin].newFallToRight(filaIni, colIni, colFin, icon);
				actualList.add(animCell[filaFin][colFin]);
				break;
				
			case SWAP_FREE:
				Texture aux = animCell[filaFin][colFin].getIcon();
				
				animCell[filaFin][colFin].newFreeSwap(filaIni, colIni, filaFin, colFin, 
						animCell[filaIni][colIni].getIcon()
					);
				actualList.add(animCell[filaFin][colFin]);
				
				animCell[filaIni][colIni].newFreeSwap(filaFin, colFin, filaIni, colIni, aux);
				actualList.add(animCell[filaIni][colIni]);
				
				break;
			
			case SWAP_H:
				Texture aux2 = animCell[filaFin][colFin].getIcon();
				
				animCell[filaFin][colFin].newHorizontalSwap(filaIni, colIni, colFin, 
						animCell[filaIni][colIni].getIcon()
					);
				actualList.add(animCell[filaFin][colFin]);
				
				animCell[filaIni][colIni].newHorizontalSwap(filaFin, colFin, colIni, aux2);
				actualList.add(animCell[filaIni][colIni]);
				break;
				
			case SWAP_V:
				Texture aux3 = animCell[filaFin][colFin].getIcon();
				
				animCell[filaFin][colFin].newVerticalSwap(filaIni, filaFin, colFin, 
						animCell[filaIni][colIni].getIcon()
					);
				actualList.add(animCell[filaFin][colFin]);
				
				animCell[filaIni][colIni].newVerticalSwap(filaFin, filaIni, colIni, aux3);
				actualList.add(animCell[filaIni][colIni]);
				break;
				
			default:
				break;
			
			}
		}
	}
	
	public BoardAnimation(Tablero tablero) {
		interactionQueue = new ConcurrentLinkedQueue<LinkedList<AnimationTask>>();
		actualList = new LinkedList<AnimationCell>();
		nextToQueueList = new LinkedList<AnimationTask>();
		animCell = new AnimationCell[MD.filas()][MD.cols()];
		lastAnimation = AnimationType.NONE;
		gameType = tablero.getGameType();
		
		StuffList thing;
		Texture icon;
		
		
		for (int j=0; j<MD.filas(); j++) //Para cada fila
			for (int i=0; i<MD.cols(); i++) {//para cada casilla de cada fila
				thing = tablero.getElementAt(j, i).getID();
				icon = Assets.getIcon(thing);
				animCell[j][i] = new AnimationCell(j, i, icon);	
			}
	}
	
	/**Separador que divide las acciones en grupos, donde los grupos se ejecutan 
	 * uno tras el otro, mientras que las acciones en un mismo grupo se ejecutan
	 * a la vez. No es necesario ejecutarlo al principio del todo, pero sí al final.*/
	private void endOfInteractionGroup() {
		interactionQueue.add(nextToQueueList);
		nextToQueueList = new LinkedList<AnimationTask>();
	}

	@Override
	public void endOfInteraction() {
		this.endOfInteractionGroup();		
	}

	@Override
	public void onSwapCandy(int fila1, int col1, int fila2, int col2) {
		
		if (lastAnimation != AnimationType.SWAP_FREE)
			this.endOfInteractionGroup();
		
		
		if ( fila1 == fila2 && Math.abs(col1-col2) == 1 ) {
			this.nextToQueueList.add(new AnimationTask(
					AnimationType.SWAP_H, fila1, col1, fila2, col2, 
					animCell[fila1][col1].getIcon()
				));	
		}
		else if ( col1 == col2 && Math.abs(fila1-fila2) == 1 ) {
			this.nextToQueueList.add(new AnimationTask(
					AnimationType.SWAP_V, fila1, col1, fila2, col2, 
					animCell[fila1][col1].getIcon()
				));	
		}
		else {
			this.nextToQueueList.add(new AnimationTask(
					AnimationType.SWAP_FREE, fila1, col1, fila2, col2, 
					animCell[fila1][col1].getIcon()
				));	
		}
		
		this.lastAnimation = AnimationType.SWAP_FREE;
	}

	@Override
	public void onFallCandy(int filaIni, int filaFin, int colIni, int colFin) {
		
		if (this.lastAnimation != AnimationType.FALL)
			this.endOfInteractionGroup();
		
		if (colIni == colFin) {
			if (filaIni <= filaFin) 
				this.nextToQueueList.add(new AnimationTask(
						AnimationType.FALL, filaIni, colIni, filaFin, colFin, null
					));
			}	
		else if (filaIni == filaFin) {
			if (colIni <= colFin)
				this.nextToQueueList.add(new AnimationTask(
						AnimationType.FALL_R, filaIni, colIni, filaFin, colFin, null
					));
			else
				this.nextToQueueList.add(new AnimationTask(
						AnimationType.FALL_L, filaIni, colIni, filaFin, colFin, null
					));
		}
		
		this.lastAnimation = AnimationType.FALL;
	}

	@Override
	public void onCreateCandy(StuffList candy, int filaSpawn, int fila, int colSpawn, int col) {
		if (this.lastAnimation != AnimationType.FALL)
			this.endOfInteractionGroup();
		
		if (colSpawn == col) {
			if (filaSpawn <= fila) 
				this.nextToQueueList.add(new AnimationTask(
						AnimationType.FALL, filaSpawn, colSpawn, fila, col, Assets.getIcon(candy)
					));
			}	
		else if (filaSpawn == fila) {
			if (colSpawn <= col)
				this.nextToQueueList.add(new AnimationTask(
						AnimationType.FALL_R, filaSpawn, colSpawn, fila, col, Assets.getIcon(candy)
					));
			else
				this.nextToQueueList.add(new AnimationTask(
						AnimationType.FALL_L, filaSpawn, colSpawn, fila, col, Assets.getIcon(candy)
					));
		}
		
		this.lastAnimation = AnimationType.FALL;
	}

	@Override
	public void onDestroyCandy(int fila, int col) {
		
		if (this.lastAnimation != AnimationType.DESTROY)
			this.endOfInteractionGroup();
		
		this.nextToQueueList.add(new AnimationTask(
				AnimationType.DESTROY, fila, col, fila, col, null
			));
		
		this.lastAnimation = AnimationType.DESTROY;
	}
	
	@Override
	public void onTransformCandy(StuffList candy, int fila, int col) {
		// TODO Implementar transformacion de sprite
		
	}

	/**
	 * Ejecuta un paso de las aniaciones en curso. Si todas las animaciones en curso han terminado,
	 * extrae la siguiente lista de tareas {@link AnimationTask} de la cola {@link BoardAnimation#interactionQueue }
	 * y activa las animaciones con {@link AnimationTask#activate() }
	 */
	public void step() {
		boolean shouldBeRemoved;
		LinkedList<AnimationCell> toRemoveList = new LinkedList<AnimationCell>();
		
		//Carga del siguiente bloque de interacción
		while (actualList.isEmpty() && !interactionQueue.isEmpty()) {
			LinkedList<AnimationTask> nextToProcess = interactionQueue.remove();
			for (AnimationTask o: nextToProcess) o.activate();
		}
		
		//Movimientos de los caramelos
		for (AnimationCell o: actualList) {
			shouldBeRemoved = o.step();
			if (shouldBeRemoved) toRemoveList.add(o);
		}
		//Borrado de los caramelos que han alcanzado la posición final
		for (AnimationCell o: toRemoveList) {
			actualList.remove(o);
		}
	}
	
	/**
	 * Dibuja el tablero, tanto los elementos jugables como las decoraciones del propio tablero,
	 * tales como el fondo de gelatina
	 * @param batch Herramienta de dibujo
	 */
	public void drawTablero(SpriteBatch batch) {
		if (gameType == GameType.STEAL_2P)
			this.pintarGelatina2Jug(batch);
		
		//Pintar chucherias
		for (int j=0; j<MD.filas(); j++) //Para cada fila
			for (int i=0; i<MD.cols(); i++) {//para cada casilla de cada fila
				if (animCell[j][i].getIcon() != null )
					batch.draw(animCell[j][i].getIcon(), animCell[j][i].getX(),
							animCell[j][i].getY(), MD.dim(), MD.dim()
						);
				else
					batch.draw(animCell[j][i].getSprite(), animCell[j][i].getX(),
							animCell[j][i].getY(), MD.dim(), MD.dim()
						);
			}

	}
	
	/**
	 * Pinta la gelatina para el Robo de ingredientes de 2 jugadores
	 * @param batch Herramienta de dibujo
	 */
	protected void pintarGelatina2Jug(SpriteBatch batch) {
		//Pintar fondo de gelatina
		for (int j=0; j<MD.filas(); j++) { //Para cada fila
			for (int i=0; i<MD.cols()/2; i++) {//para cada casilla de cada fila
				batch.draw(Assets.gelatinaR, 
						MD.originX() + i*MD.dim(),
						MD.originY() - (j+1)*MD.dim(),
						MD.dim(), MD.dim()
					);
			}
			for (int i=MD.cols()/2; i<MD.cols(); i++) {//para cada casilla de cada fila
				batch.draw(Assets.gelatinaAz, 
						MD.originX() + i*MD.dim(),
						MD.originY() - (j+1)*MD.dim(),
						MD.dim(), MD.dim()
					);
			}
		}
	}
	/**
	 * Lista de animaciones en curso. Son elementos de la matriz animCell */
	private LinkedList<AnimationCell> actualList;
	/** Tareas de animación pendientes, estructurada en una sucesion de grupos.
	 * Los grupos se ejecutan uno tras otro, pero los elementos dentro de un
	 * grupo se ejecutan a la vez */
	private ConcurrentLinkedQueue<LinkedList<AnimationTask>> interactionQueue;
	/** Grupo de animaciones pendientes actualmente en creación. Se añadirá
	 *  al final de la cola cuando entre una animación que pertenezca
	 *   a otro grupo o cuando se llame a {@link BoardAnimation#endOfInteractionGroup()}
	 *    @see BoardAnimation#interactionQueue
	 *    @see BoardAnimation#endOfInteraction()
	 *    @see BoardAnimation#endOfInteractionGroup()
	 */
	private LinkedList<AnimationTask> nextToQueueList;
	/** Matriz donde cada elemento guarda el texture, la posición y el tamaño de
	 * la chuchería
	 * @see AnimationCell
	 */
	private AnimationCell[][] animCell;
	/**
	 * Guarda la última animación que se realizó, para poder distinguir cuáles
	 * se pueden realizar a la vez. No distingue entre distintos tipos de SWAP o de FALL,
	 * ya que sólo se ejecutará un tipo a la vez. La creación se considera un tipo de FALL
	 * @see AnimationType
	 */
	private AnimationType lastAnimation;
	
	/**
	 * Tipo de juego al que se está jugando
	 * @see GameType
	 */
	private GameType gameType;
}
