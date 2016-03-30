package com.mygdx.game.modelo.tableros;

import java.util.Vector;

import com.mygdx.game.controlador.GameType;
import com.mygdx.game.controlador.StuffList;
import com.mygdx.game.controlador.StuffPile;
import com.mygdx.game.modelo.caramelos.Caramelo;
import com.mygdx.game.modelo.caramelos.Chucheria;
import com.mygdx.game.modelo.caramelos.Color;
import com.mygdx.game.modelo.caramelos.Ingrediente;
import com.mygdx.game.modelo.tableros.Tablero.Observer;
import com.mygdx.game.modelo.tableros.Tablero.SegmentoCol;
import com.mygdx.game.modelo.tableros.Tablero.SegmentoFila;

/**
 * Modalidad de dos jugadores. La mitad izquierda del tablero pertenece inicialmente al jugador rojo,
 * y la mitad derecha al jugador azul
 *
 */
public class TableroJelly2Jug extends Tablero {
	
	/** Almacena una chuchería y sus coordenadas en el tablero para poder
	 * destruirla más tarde */
	protected class ChucheYcoordRobo2Jug implements ChucheYcoord{
		
		public ChucheYcoordRobo2Jug(Chucheria candy, int fila, int col){
			this.candy = candy;
			this.fila = fila;
			this.col = col;
		}
		
		@Override
		public void destruir(Tablero tablero) {
			
			try {
				if (isRedPlayersTurn) {
					while (candy != tablero.getElementAt(fila, col))
						col--;
					candy.destruir(tablero, fila, col);
				}
				else {
					while (candy != tablero.getElementAt(fila, col))
						col++;
					candy.destruir(tablero, fila, col);
				}
			}
			catch (ArrayIndexOutOfBoundsException ex) {}	
		}
		
		protected Chucheria candy;
		protected int fila;
		protected int col;
	}

	/**
	 * Crea un tablero básico con caramelos aleatorios, asegurando
	 * que no aparecen 3 de un mismo color ni en fila ni en columna
	 */
	public TableroJelly2Jug() {
		this.destruirMasTarde = new Vector<ChucheYcoord>();
		this.FILAS = 8;
		this.COLS= 12;
		this.gelAzulesEnTablero = FILAS*COLS/2;
		this.gelRojasEnTablero = FILAS*COLS/2;
		this.isRedPlayersTurn = true;
		
		this.tableroChuches = new Chucheria[FILAS][COLS];
		for (int i=0; i<FILAS; i++) {
			for (int j=0; j<COLS; j++) {
				do {
					tableroChuches[i][j] = new Caramelo();
				}while (!valido(i,j));
			}
		}
		
		this.tableroGelatinas = new int[FILAS][COLS];
		for (int j=0; j<COLS/2; j++) {
			for (int i=0; i<FILAS; i++) {
					tableroGelatinas[i][j] = 1;
			}
		}
		for (int j=COLS/2; j<COLS; j++) {
			for (int i=0; i<FILAS; i++) {
					tableroGelatinas[i][j] = 4;
			}
		}
	}
	
	@Override
	public boolean intercambiar(int fila1, int col1, int fila2, int col2, boolean intercambioLibre) { //TODO Reimplementar. Añadir onda expansiva
		boolean intercambioExitoso = false;
		if (	intercambioLibre
				|| (fila1 == fila2 && Math.abs(col1-col2) == 1 )
				|| (col1 == col2 && Math.abs(fila1-fila2) == 1 )	) {
			
			if (isRedPlayersTurn && col1 < COLS/2 && col2 < COLS/2) {
				intercambioExitoso = super.intercambioGenerico(fila1, col1, fila2, col2);
			}
			else if (col1 >= COLS/2 && col2 >= COLS/2) {
				intercambioExitoso = super.intercambioGenerico(fila1, col1, fila2, col2);
			}
		}
		
		if (intercambioExitoso)
			this.isRedPlayersTurn = !this.isRedPlayersTurn; //Cambio de turno
		
		//XXX TEST Implentar puntos de manera grafica
		System.out.println("Score: R " + this.puntosRojo + ", A " + this.puntosAzul
				+ " OnBoard: R " + this.ingRojosEnTablero + " (" + this.restantesParaIngRojo + " left),"
				+ "A " + this.ingAzulesEnTablero + " (" + this.restantesParaIngAzul + " left),");
		if (isRedPlayersTurn)
			System.out.println("Red's turn!");
		else
			System.out.println("Blue's turn!");
		
		for (Observer o: obs) o.endOfInteraction();
		return intercambioExitoso;
	}

	@Override
	public boolean crear(Chucheria candy, int filaSpawn, int fila, int colSpawn, int col) {
		boolean creado = false;
		if (tableroChuches[fila][col] == null) {
			tableroChuches[fila][col] = candy;
			for (Observer o: obs) o.onCreateCandy(candy.getID(), filaSpawn, fila, colSpawn, col); //Avisamos de la creación
			creado = true;
		}
		return creado;
	}


	@Override
	public void introducir(Chucheria candy, int fila, int col, boolean animateTransform) {
		tableroChuches[fila][col] = candy;
		if (animateTransform) for (Observer o: obs) o.onTransformCandy(candy.getID(), fila, col);
	}
	
	@Override
	public void destruir(int fila, int col) { //TODO destruir
		if (tableroChuches[fila][col] != null) {
			boolean debeDestruirse = tableroChuches[fila][col].destruir(this, fila, col);
			if (debeDestruirse) {
			
				if (tableroChuches[fila][col].getID() == StuffList.CEREZA_AZUL) {
					this.puntosAzul++;
					this.gelAzulesEnTablero--;
					//this.ingEnTablero.remove(tablero[fila][col]);
					if (this.gelAzulesEnTablero == 0 && this.restantesParaIngAzul > RESTANTES_PRIMERO)
						this.restantesParaIngAzul = RESTANTES_PRIMERO;
				}
				else if (tableroChuches[fila][col].getID() == StuffList.CEREZA_ROJA) {
					this.puntosRojo++;
					this.gelRojasEnTablero--;
					//this.ingEnTablero.remove(tablero[fila][col]);
					if (this.gelRojasEnTablero == 0 && this.restantesParaIngRojo > RESTANTES_PRIMERO)
						this.restantesParaIngRojo = RESTANTES_PRIMERO;
				}
				this.suprimir(fila, col, false);
				for (Observer o: obs) o.onDestroyCandy(fila, col);
			}
			else
				this.addToDestruirMasTarde(fila, col);
		}
	}

	@Override
	public void suprimir(int fila, int col, boolean animateDestroy) { //TODO suprimir
		tableroChuches[fila][col] = null;
		if (animateDestroy) for (Observer o: obs) o.onDestroyCandy(fila, col);
	}

	@Override
	public Chucheria getElementAt(int i, int j) throws ArrayIndexOutOfBoundsException {
		return tableroChuches[i][j];
	}
	
	protected static StuffList intToColorJelly(int jellyInt) {
		StuffList jelly;
		switch (jellyInt) {
			case 1: jelly = StuffList.GELATINA_ROJA_2; break;
			case 2: jelly = StuffList.GELATINA_ROJA_1; break;
			case 3: jelly = StuffList.GELATINA_AZUL_1; break;
			case 4: jelly = StuffList.GELATINA_AZUL_2; break;
			default: jelly = null;
		}
		return jelly;
	}
	
	@Override
	public StuffPile getPileOfElementsAt(int fila, int col)
			throws ArrayIndexOutOfBoundsException {
		return new StuffPile(
				tableroChuches[fila][col].getID(), 
				intToColorJelly(tableroGelatinas[fila][col])
				);
	}

	@Override
	public GameType getGameType() {
		return GameType.JELLY_2P;
	}
		
	@Override
	protected void swap(int fila1, int col1, int fila2, int col2) {
		Chucheria aux = tableroChuches[fila1][col1];
		tableroChuches[fila1][col1] = tableroChuches[fila2][col2];
		tableroChuches[fila2][col2] = aux;	
		for (Observer o: obs) o.onSwapCandy(fila1, col1, fila2, col2);
	}

	@Override
	protected void rellenar() {
		if (this.isRedPlayersTurn) 
			rellenarCaidaHaciaIzda();
		else
			rellenarCaidaHaciaDcha();
	}
	

	@Override
	protected boolean combinarDeBarrido() {
		Vector<SegmentoFila> combinFila = sacarCombinFilas();
		Vector<SegmentoCol> combinCol = sacarCombinCols();
		
		if (combinFila.isEmpty() && combinCol.isEmpty())
			return false;
		else {
			destruir(combinFila, combinCol);
			crearEspecialesBarrido(combinFila, combinCol);
			return true;
		}
		
	}
	@Override
	protected boolean quedaPorDestruir() {
		return !this.destruirMasTarde.isEmpty();
	}

	@Override
	protected void destruirPendientes() {
		Vector<ChucheYcoord> destruirAhora = this.destruirMasTarde; //movemos la lista..
		this.destruirMasTarde = new Vector<ChucheYcoord>(); //...para crear una nueva donde se almacenen las nuevas destrucciones aplazadas..
		for (ChucheYcoord ch : destruirAhora)
			ch.destruir(this);  //... y destruimos lo que tengamos pendiente
	}

	
	/**
	 * Rellena el tablero, haciendo que los caramelos caigan (hacia la derecha)
	 * y posteriormente se creen caramelos nuevos para rellenar los huecos
	 */
	protected void rellenarCaidaHaciaDcha() {
		for (int i=0; i<FILAS; i++) {//De arriba a abajo
			int jExtr = COLS-1; //Avanzadilla (de donde extrae)
			int jRec = COLS-1; //Hueco (donde coloca)
			
			while (jExtr>=0) { //De dcha a izda hacemos caer
				if (tableroChuches[i][jExtr] == null)
					jExtr--;
				else if (jRec != jExtr){
					tableroChuches[i][jRec] = tableroChuches[i][jExtr]; //Colocamos en el hueco
					for (Observer o :obs) o.onFallCandy(i, i, jExtr, jRec); //Avisamos de la caida
					tableroChuches[i][jExtr] = null;
					jExtr--;
					jRec--;
				}
				else {
					jExtr--;
					jRec--;
				}
			}
			
			while (jRec>=0) { //Rellenamos sobrantes
				crear(new Caramelo(), i, i, jExtr, jRec);
				jRec--;
				jExtr--;
			}
			
			
		}	
	}
	
	/**
	 * Rellena el tablero, haciendo que los caramelos caigan (hacia la izquierda)
	 * y posteriormente se creen caramelos nuevos para rellenar los huecos
	 */
	protected void rellenarCaidaHaciaIzda() {
		for (int i=0; i<FILAS; i++) {//De arriba a abajo
			int jExtr = 0; //Avanzadilla (de donde extrae)
			int jRec = 0; //Hueco (donde coloca)
			
			while (jExtr<COLS) { //De dcha a izda hacemos caer
				if (tableroChuches[i][jExtr] == null)
					jExtr++;
				else if (jRec != jExtr){
					tableroChuches[i][jRec] = tableroChuches[i][jExtr]; //Colocamos en el hueco
					for (Observer o :obs) o.onFallCandy(i, i, jExtr, jRec); //Avisamos de la caida
					tableroChuches[i][jExtr] = null;
					jExtr++;
					jRec++;
				}
				else {
					jExtr++;
					jRec++;
				}
			}
			
			while (jRec<COLS) { //Rellenamos sobrantes
				crear(new Caramelo(), i, i, jExtr, jRec);
				jRec++;
				jExtr++;
			}
			
			
		}	
	}
	
	protected void addToDestruirMasTarde(int fila, int col) {
		this.destruirMasTarde.addElement(new ChucheYcoordRobo2Jug(tableroChuches[fila][col], fila, col));
	}
	
	/**Matriz de chucherías que representa el tablero, representado como (fila,columna)*/
	private Chucheria[][] tableroChuches;
	/**Matriz de gelatinas del tablero, representado como (fila,columna)*/
	private int[][] tableroGelatinas;
	
	/** Las chucherías con varias fases de destrucciónse registran aquí
	 *  para volver a destruirse cuando el tablero esté estable*/
	private Vector<ChucheYcoord> destruirMasTarde;
	
	/** Indica si el jugador rojo (izquierdo) está jugando */
	private boolean isRedPlayersTurn;
	/** Número de gelatinas rojas en el tablero */
	private int gelRojasEnTablero;
	/** Número de gelatinas azules en el tablero */
	private int gelAzulesEnTablero;

	

}
