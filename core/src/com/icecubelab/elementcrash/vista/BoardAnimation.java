package com.icecubelab.elementcrash.vista;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.icecubelab.elementcrash.controlador.Controlador;
import com.icecubelab.elementcrash.controlador.GameType;
import com.icecubelab.elementcrash.controlador.StuffList;
import com.icecubelab.elementcrash.controlador.StuffPile;
import com.icecubelab.elementcrash.modelo.tableros.Tablero;

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
			Texture aux;
			switch (movement) {
			case NONE: break;
			
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
				aux = animCell[filaFin][colFin].getIcon();
				
				animCell[filaFin][colFin].newFreeSwap(filaIni, colIni, filaFin, colFin, 
						animCell[filaIni][colIni].getIcon()
					);
				actualList.add(animCell[filaFin][colFin]);
				
				animCell[filaIni][colIni].newFreeSwap(filaFin, colFin, filaIni, colIni, aux);
				actualList.add(animCell[filaIni][colIni]);
				
				break;
			
			case SWAP_H:
				aux = animCell[filaFin][colFin].getIcon();
				
				animCell[filaFin][colFin].newHorizontalSwap(filaIni, colIni, colFin, 
						animCell[filaIni][colIni].getIcon()
					);
				actualList.add(animCell[filaFin][colFin]);
				
				animCell[filaIni][colIni].newHorizontalSwap(filaFin, colFin, colIni, aux);
				actualList.add(animCell[filaIni][colIni]);
				break;
				
			case SWAP_V:
				aux = animCell[filaFin][colFin].getIcon();
				
				animCell[filaFin][colFin].newVerticalSwap(filaIni, filaFin, colFin, 
						animCell[filaIni][colIni].getIcon()
					);
				actualList.add(animCell[filaFin][colFin]);
				
				animCell[filaIni][colIni].newVerticalSwap(filaFin, filaIni, colIni, aux);
				actualList.add(animCell[filaIni][colIni]);
				break;
				
			case JELLY_DESTROY:
				jellyAnimCell[filaFin][colFin].newJellyDestroy(icon);
				break;
			case COVER_DESTROY:
				coverAnimCell[filaFin][colFin].newCoverDestroy(icon);
				break;			
			}
		}
	}
	/**
	 * Crea el control de la animación. Es necesario que la clase MD haya sido inicializada con 
	 * un resize(), ya que los AnimationCell usan valores de MD en su constructor.
	 * @param controlTablero
	 * @see AnimationCell
	 */
	public BoardAnimation(Controlador controlTablero) {
		interactionQueue = new ConcurrentLinkedQueue<LinkedList<AnimationTask>>();
		actualList = new LinkedList<AnimationCell>();
		nextToQueueList = new LinkedList<AnimationTask>();
		lastAnimation = AnimationType.NONE;
		
		gameType = controlTablero.getGameType();
		FILAS = controlTablero.getRows();
		COLS = controlTablero.getColumns();
		
		animCell = new AnimationCell[FILAS][COLS];
		
		if (gameType == GameType.JELLY_BASIC
				|| gameType == GameType.JELLY_2P
				|| gameType == GameType.JELLY_COVER_BASIC)
			jellyAnimCell = new AnimationCell[FILAS][COLS];
		
		if (gameType == GameType.JELLY_COVER_BASIC)
			coverAnimCell = new AnimationCell[FILAS][COLS];
		
		StuffPile pileOfThings;
		Texture icon;
		
		
		for (int j=0; j<FILAS; j++) //Para cada fila
			for (int i=0; i<COLS; i++) {//para cada casilla de cada fila
				pileOfThings = controlTablero.getPileOfElementsAt(j, i);
				icon = Assets.getIcon(pileOfThings.getCandy());
				animCell[j][i] = new AnimationCell(j, i, icon);
				if (gameType == GameType.JELLY_BASIC
						|| gameType == GameType.JELLY_2P
						|| gameType == GameType.JELLY_COVER_BASIC) {
					icon = Assets.getIcon(pileOfThings.getJelly());
					jellyAnimCell[j][i] = new AnimationCell(j,i,icon);
				}
				if (gameType == GameType.JELLY_COVER_BASIC) {
					icon = Assets.getIcon(pileOfThings.getCover());
					coverAnimCell[j][i] = new AnimationCell(j,i,icon);
				}
			}
	}
	
	/**Separador que divide las acciones en grupos, donde los grupos se ejecutan 
	 * uno tras el otro, mientras que las acciones en un mismo grupo se ejecutan
	 * a la vez. No es necesario ejecutarlo al principio del todo, pero sí al final.*/
	private void endOfInteractionGroup() {
		interactionQueue.add(nextToQueueList);
		nextToQueueList = new LinkedList<AnimationTask>();
		
		//XXX TEST System.out.println(" <----- endOfInteractionGroup ----> ");
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
		else if (fila1 != fila2 || col1 != col2){ //Evito intercambiar una casilla con sigo misma
			this.nextToQueueList.add(new AnimationTask(
					AnimationType.SWAP_FREE, fila1, col1, fila2, col2, 
					animCell[fila1][col1].getIcon()
				));	
		}
		
		this.lastAnimation = AnimationType.SWAP_FREE;
		
		
		//XXX TEST System.out.println("Swap: (" + fila1 + "," + col1 + ") <-> (" + fila2 + "," + col2 +")");
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
		
		
		//XXX TEST System.out.println("Fall: (" + filaIni + "," + colIni + ") -> (" + filaFin + "," + colFin +")");
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
		
		
		//XXX TEST System.out.println("Create: (" + filaSpawn + "," + colSpawn + ") -> (" + fila + "," + col +")");
	}

	@Override
	public void onDestroyCandy(int fila, int col) {
		
		if (this.lastAnimation != AnimationType.DESTROY)
			this.endOfInteractionGroup();
		
		this.nextToQueueList.add(new AnimationTask(
				AnimationType.DESTROY, fila, col, fila, col, null
			));
				
		this.lastAnimation = AnimationType.DESTROY;
		
		
		//XXX TEST System.out.println("Destroy candy: (" + fila + "," + col + ")");
	}
	
	@Override
	public void onTransformCandy(int fila, int col, StuffList newCandy) {
		// TODO Implementacion provisional
		if (this.lastAnimation != AnimationType.FALL)
			this.endOfInteractionGroup();

		this.nextToQueueList.add(new AnimationTask(
				AnimationType.FALL, fila, col, fila, col, Assets.getIcon(newCandy)
			));

		this.lastAnimation = AnimationType.FALL;
		
		
		//XXX TEST System.out.println("Transform candy: (" + fila + "," + col + ") -> " + newCandy );
		
	}

	@Override
	public void onDestroyJelly(int fila, int col, StuffList newJelly) {	
		if (this.lastAnimation != AnimationType.DESTROY)
			this.endOfInteractionGroup();
		
		this.nextToQueueList.add(new AnimationTask(
				AnimationType.JELLY_DESTROY, fila, col, fila, col, Assets.getIcon(newJelly)
			));
		
		this.lastAnimation = AnimationType.DESTROY;
		
		
		//XXX TEST System.out.println("Destroy Jelly: (" + fila + "," + col + ") -> " + newJelly );
	}

	@Override
	public void onDestroyCover(int fila, int col, StuffList newCover) {
		if (this.lastAnimation != AnimationType.DESTROY)
			this.endOfInteractionGroup();
		
		this.nextToQueueList.add(new AnimationTask(
				AnimationType.COVER_DESTROY, fila, col, fila, col, Assets.getIcon(newCover)
			));
		
		this.lastAnimation = AnimationType.DESTROY;
		
		
		//XXX TEST System.out.println("Destroy Cover: (" + fila + "," + col + ") -> " + newCover );
		
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
		else if (gameType == GameType.JELLY_BASIC || gameType == GameType.JELLY_2P )
			this.pintarGelatina(batch);
		else if (gameType == GameType.JELLY_COVER_BASIC) {
			this.pintarGelatina(batch);
			this.pintarCobertura(batch);
		}
		
		//Pintar chucherias
		for (int j=0; j<FILAS; j++) //Para cada fila
			for (int i=0; i<COLS; i++) {//para cada casilla de cada fila
				if (animCell[j][i].getIcon() != null )
					batch.draw(animCell[j][i].getIcon(), animCell[j][i].getX(),
							animCell[j][i].getY(), MD.dim(), MD.dim()
						);
				else if (animCell[j][i].getSprite() != null )
					batch.draw(animCell[j][i].getSprite(), animCell[j][i].getX(),
							animCell[j][i].getY(), MD.dim(), MD.dim()
						);
				//XXX TEST else System.err.println("Sin sprite o icono: chuche("+ j+","+i+")");
			}

	}
	
	protected void pintarCobertura(SpriteBatch batch) {
		//Pintar cobertura
		for (int j=0; j<FILAS; j++) //Para cada fila
			for (int i=0; i<COLS; i++) {//para cada casilla de cada fila
				if (coverAnimCell[j][i].getIcon() != null )
					batch.draw(coverAnimCell[j][i].getIcon(), coverAnimCell[j][i].getX(),
							coverAnimCell[j][i].getY(), MD.dim(), MD.dim()
						);
				//XXX TEST else System.err.println("Sin sprite o icono: cobertura("+ j+","+i+")");
			}
		
	}

	/**
	 * Dibuja el tablero, tanto los elementos jugables como las decoraciones del propio tablero,
	 * tales como el fondo de gelatina
	 * @param batch Herramienta de dibujo
	 */
	protected void pintarGelatina(SpriteBatch batch) {		
		//Pintar gelatina
		for (int j=0; j<FILAS; j++) //Para cada fila
			for (int i=0; i<COLS; i++) {//para cada casilla de cada fila
				if (jellyAnimCell[j][i].getIcon() != null )
					batch.draw(jellyAnimCell[j][i].getIcon(), jellyAnimCell[j][i].getX(),
							jellyAnimCell[j][i].getY(), MD.dim(), MD.dim()
						);
				else if (animCell[j][i].getSprite() != null )
					batch.draw(Assets.gelatinaFondo, jellyAnimCell[j][i].getX(),
							jellyAnimCell[j][i].getY(), MD.dim(), MD.dim()
						);
				//XXX TEST else System.err.println("Sin sprite o icono: gelatina("+ j+","+i+")");
			}

	}
	
	/**
	 * Pinta la gelatina para el Robo de ingredientes de 2 jugadores
	 * @param batch Herramienta de dibujo
	 */
	protected void pintarGelatina2Jug(SpriteBatch batch) {
		//Pintar fondo de gelatina
		for (int j=0; j<FILAS; j++) { //Para cada fila
			for (int i=0; i<COLS/2; i++) {//para cada casilla de cada fila
				batch.draw(Assets.gelatinaR2, 
						MD.originX() + i*MD.dim(),
						MD.originY() - (j+1)*MD.dim(),
						MD.dim(), MD.dim()
					);
			}
			for (int i=COLS/2; i<COLS; i++) {//para cada casilla de cada fila
				batch.draw(Assets.gelatinaAz2, 
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
	/** Matriz donde cada elemento guarda el texture, la posición y el tamaño de
	 * la gelatina
	 * @see AnimationCell
	 */
	private AnimationCell[][] jellyAnimCell;
	/** Matriz donde cada elemento guarda el texture, la posición y el tamaño de
	 * la cobertura
	 * @see AnimationCell
	 */
	private AnimationCell[][] coverAnimCell;
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
	
	private int FILAS;
	private int COLS;
}
