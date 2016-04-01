package com.mygdx.game.modelo.tableros;

import java.util.Vector;

import com.mygdx.game.controlador.GameType;
import com.mygdx.game.controlador.StuffList;
import com.mygdx.game.controlador.StuffPile;
import com.mygdx.game.modelo.caramelos.Caramelo;
import com.mygdx.game.modelo.caramelos.Chucheria;

/**
 * Extensión de <code>TableroBasic</code> que incluye la funcionalidad de la gelatina
 * @see TableroBasic
 */
public class TableroJellyBasic extends Tablero {

	
	
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
				candy.destruir(tablero, fila, col);
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
	public TableroJellyBasic(int nivelGelatina) {
		this.destruirMasTarde = new Vector<ChucheYcoord>();
		this.FILAS = 8;
		this.COLS = 8;
		
		this.tableroChuches = new Chucheria[FILAS][COLS];
		for (int i=0; i<FILAS; i++) {
			for (int j=0; j<COLS; j++) {
				do {
					tableroChuches[i][j] = new Caramelo();
				}while (!valido(i,j));
			}
		}
		
		this.tableroGelatinas = new int[FILAS][COLS];
		for (int i=0; i<FILAS; i++) {
			for (int j=0; j<COLS; j++) {
					tableroGelatinas[i][j] = nivelGelatina;
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
	public void efectoOndaExpansiva(int fila, int col) {}

	@Override
	public void destruir(int fila, int col) { 
		if (tableroChuches[fila][col] != null) {
			boolean debeDestruirse = tableroChuches[fila][col].destruir(this, fila, col);
			if (debeDestruirse) {
				this.suprimir(fila, col, true);
			}
			else
				this.addToDestruirMasTarde(fila, col);	
		}
	}
	
	@Override
	public void suprimir(int fila, int col, boolean animateDestroy) {
		tableroChuches[fila][col] = null;
		if (animateDestroy) {
			for (Observer o: obs) o.onDestroyCandy(fila, col);
			if(tableroGelatinas[fila][col]>0) {
				tableroGelatinas[fila][col]--;
				for (Observer o: obs) o.onDestroyJelly(fila, col, intToWhiteJelly(tableroGelatinas[fila][col]));
			}
		}
	}

	@Override
	public Chucheria getElementAt(int i, int j) throws ArrayIndexOutOfBoundsException {
		return tableroChuches[i][j];
	}
	
	protected static StuffList intToWhiteJelly(int jellyInt) {
		StuffList jelly;
		switch (jellyInt) {
			case 4: jelly = StuffList.GELATINA_NORMAL_4; break;
			case 3: jelly = StuffList.GELATINA_NORMAL_3; break;
			case 2: jelly = StuffList.GELATINA_NORMAL_2; break;
			case 1: jelly = StuffList.GELATINA_NORMAL_1; break;
			case 0: jelly = StuffList.SIN_GELATINA;		 break;
			default: jelly = null;
		}
		return jelly;
	}
	
	@Override
	public StuffPile getPileOfElementsAt(int fila, int col)
			throws ArrayIndexOutOfBoundsException {
		
		return new StuffPile(
				tableroChuches[fila][col].getID(), 
				intToWhiteJelly(tableroGelatinas[fila][col])
				);
	}

	@Override
	public GameType getGameType() {
		return GameType.JELLY_BASIC;
	}

	protected void addToDestruirMasTarde(int fila, int col) {
		this.destruirMasTarde.addElement(new ChucheYcoordBasic(tableroChuches[fila][col], fila, col));
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
		for (int j=0; j<COLS; j++) {//De izqda a dcha
			int iExtr = FILAS-1; //Avanzadilla (de donde extrae)
			int iRec = FILAS-1; //Hueco (donde coloca)
			
			while (iExtr>=0) { //De abajo a arriba hacemos caer
				if (tableroChuches[iExtr][j] == null)
					iExtr--;
				else if (iRec != iExtr){
					tableroChuches[iRec][j] = tableroChuches[iExtr][j]; //Colocamos en el hueco
					for (Observer o :obs) o.onFallCandy(iExtr, iRec, j, j); //Avisamos de la caida
					tableroChuches[iExtr][j] = null;
					iExtr--;
					iRec--;
				}
				else {
					iExtr--;
					iRec--;
				}
			}
			
			while (iRec>=0) { //Rellenamos sobrantes
				crear(new Caramelo(), iExtr, iRec, j, j);
				iRec--;
				iExtr--;
			}
			
			
		}	
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

	/**Matriz de chucherías que representa el tablero, representado como (fila,columna)*/
	protected Chucheria[][] tableroChuches;
	
	/**Matriz de gelatinas del tablero, representado como (fila,columna)*/
	protected int[][] tableroGelatinas;
	
	/** Las chucherías con varias fases de destrucciónse registran aquí
	 *  para volver a destruirse cuando el tablero esté estable*/
	protected Vector<ChucheYcoord> destruirMasTarde;
	
}
