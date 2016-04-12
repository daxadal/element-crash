package com.mygdx.game.modelo.tableros;

import java.util.Vector;

import com.mygdx.game.controlador.GameType;
import com.mygdx.game.controlador.StuffList;
import com.mygdx.game.controlador.StuffPile;
import com.mygdx.game.modelo.caramelos.Caramelo;
import com.mygdx.game.modelo.caramelos.Chucheria;

/**
 * Clase que implementa la funcionalidad básica del <code>Tablero</code>, 
 * incluyendo la creación y efectos de caramelos especiales
 */
public class TableroBasic extends Tablero {

	/** Almacena una chuchería y sus coordenadas en el tablero para poder
	 * destruirla más tarde */
	protected class ChucheYcoordBasic implements ChucheYcoord {
		
		public ChucheYcoordBasic(Chucheria candy, int fila, int col){
			this.candy = candy;
			this.fila = fila;
			this.col = col;
		}
		
		@Override
		public void destruir(Tablero tablero) {
			try {
				while (candy != tablero.getElementAt(fila, col))
					fila++;
				tablero.destruir(fila, col);
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
	public TableroBasic() {
		this.destruirMasTarde = new Vector<ChucheYcoord>();
		this.FILAS = 8;
		this.COLS = 8;
		//this.addObserver(this.destruirMasTarde);
		
		this.tablero = new Chucheria[FILAS][COLS];
		for (int i=0; i<FILAS; i++) {
			for (int j=0; j<COLS; j++) {
				do {
					tablero[i][j] = new Caramelo();
				}while (!valido(i,j));
			}
		}
	}

	@Override
	public boolean intercambiar(int fila1, int col1, int fila2, int col2, boolean intercambioLibre) {
		boolean intercambioExitoso = false;
		if (	intercambioLibre
				|| (fila1 == fila2 && Math.abs(col1-col2) == 1 )
				|| (col1 == col2 && Math.abs(fila1-fila2) == 1 )	)
			intercambioExitoso = super.intercambioGenerico(fila1, col1, fila2, col2);
		
		for (Observer o: obs) o.endOfInteraction();
		return intercambioExitoso;
	}

	@Override
	public boolean crear(Chucheria candy, int filaSpawn, int fila, int colSpawn, int col, boolean animateTransform) {
		boolean creado = false;
		if (tablero[fila][col] == null) {
			tablero[fila][col] = candy;
			if (animateTransform) {
				for (Observer o: obs) 
					o.onTransformCandy(fila, col, candy.getID()); //Avisamos de la transformación
			}
			else {
				for (Observer o: obs)
					o.onCreateCandy(candy.getID(), filaSpawn, fila, colSpawn, col); //Avisamos de la creación
			}
			creado = true;
		}
		return creado;
	}


	@Override
	public void introducir(Chucheria candy, int fila, int col, boolean animateTransform) {
		tablero[fila][col] = candy;
		if (animateTransform) for (Observer o: obs) o.onTransformCandy(fila, col, candy.getID());
	}


	@Override
	public void efectoOndaExpansiva(int fila, int col) {}

	@Override
	public void destruir(int fila, int col) { 
		if (tablero[fila][col] != null) {
			boolean debeDestruirse = tablero[fila][col].destruir(this, fila, col);
			if (debeDestruirse) {
				this.suprimir(fila, col, true);
			}
			else
				this.addToDestruirMasTarde(fila, col);
		}
	}
	
	@Override
	public void suprimir(int fila, int col, boolean realDestroy) {
		tablero[fila][col] = null;
		if (realDestroy) for (Observer o: obs) o.onDestroyCandy(fila, col);
	}

	@Override
	public Chucheria getElementAt(int i, int j) {
		return tablero[i][j];
	}
	
	@Override
	public StuffPile getPileOfElementsAt(int fila, int col) {
		return new StuffPile(tablero[fila][col].getID(), StuffList.SIN_GELATINA, StuffList.SIN_COBERTURA);
	}

	@Override
	public GameType getGameType() {
		return GameType.BASIC;
	}

	protected void addToDestruirMasTarde(int fila, int col) {
		this.destruirMasTarde.addElement(new ChucheYcoordBasic(tablero[fila][col], fila, col));
	}


	@Override
	protected void swap(int fila1, int col1, int fila2, int col2) {
		Chucheria aux = tablero[fila1][col1];
		tablero[fila1][col1] = tablero[fila2][col2];
		tablero[fila2][col2] = aux;	
		for (Observer o: obs) o.onSwapCandy(fila1, col1, fila2, col2);
	}

	@Override
	protected void rellenar() {
		for (int j=0; j<COLS; j++) {//De izqda a dcha
			int iExtr = FILAS-1; //Avanzadilla (de donde extrae)
			int iRec = FILAS-1; //Hueco (donde coloca)
			
			while (iExtr>=0) { //De abajo a arriba hacemos caer
				if (tablero[iExtr][j] == null)
					iExtr--;
				else if (iRec != iExtr){
					tablero[iRec][j] = tablero[iExtr][j]; //Colocamos en el hueco
					for (Observer o :obs) o.onFallCandy(iExtr, iRec, j, j); //Avisamos de la caida
					tablero[iExtr][j] = null;
					iExtr--;
					iRec--;
				}
				else {
					iExtr--;
					iRec--;
				}
			}
			
			while (iRec>=0) { //Rellenamos sobrantes
				crear(new Caramelo(), iExtr, iRec, j, j, false);
				iRec--;
				iExtr--;
			}
			
			
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

	/**Matriz de chucherías que representa el tablero, representado como (fila,columna)*/
	private Chucheria[][] tablero;
	
	/** Las chucherías con varias fases de destrucciónse registran aquí
	 *  para volver a destruirse cuando el tablero esté estable*/
	private Vector<ChucheYcoord> destruirMasTarde;
	
}
