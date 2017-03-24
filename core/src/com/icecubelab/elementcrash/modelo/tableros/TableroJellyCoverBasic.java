package com.icecubelab.elementcrash.modelo.tableros;

import java.util.Vector;

import com.icecubelab.elementcrash.controlador.GameType;
import com.icecubelab.elementcrash.controlador.StuffList;
import com.icecubelab.elementcrash.controlador.StuffPile;
import com.icecubelab.elementcrash.modelo.caramelos.Caramelo;
import com.icecubelab.elementcrash.modelo.caramelos.Chucheria;
import com.icecubelab.elementcrash.modelo.caramelos.Color;

public class TableroJellyCoverBasic extends Tablero {
	
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

	public TableroJellyCoverBasic(int nivelGelatina, int nivelCobertura, int filasCobertura) {
		this.destruirMasTarde = new Vector<ChucheYcoord>();
		this.FILAS = 8;
		this.COLS = 8;
		
		this.tableroGelatinas = new int[FILAS][COLS];
		for (int i=0; i<FILAS; i++) {
			for (int j=0; j<COLS; j++) {
					tableroGelatinas[i][j] = nivelGelatina;
			}
		}
		
		this.tableroCoberturas =new int[FILAS][COLS];
		for (int i=FILAS-filasCobertura; i<FILAS; i++) {
			for (int j=0; j<COLS; j++) {
				tableroCoberturas[i][j] = nivelCobertura;
			}
		}
		
		this.tableroChuches = new Chucheria[FILAS][COLS];
		for (int i=0; i<FILAS-filasCobertura; i++) {
			for (int j=0; j<COLS; j++) {
				do {
					tableroChuches[i][j] = new Caramelo();
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
			if (this.tableroCoberturas[fila1][col1] == 0 && this.tableroCoberturas[fila2][col2] == 0)
				intercambioExitoso = super.intercambioGenerico(fila1, col1, fila2, col2);
		
		for (Observer o: obs) o.endOfInteraction();
		return intercambioExitoso;
	}
	
	@Override
	public boolean crear(Chucheria candy, int filaSpawn, int fila, int colSpawn, int col, boolean animateTransform) {
		boolean creado = false;
		if (tableroCoberturas[fila][col] == 0 && tableroChuches[fila][col] == null) {
			tableroChuches[fila][col] = candy;
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
	
	protected static StuffList intToCover(int jellyInt) {
		StuffList jelly;
		switch (jellyInt) {
			case 5: jelly = StuffList.COBERTURA_5; break;
			case 4: jelly = StuffList.COBERTURA_4; break;
			case 3: jelly = StuffList.COBERTURA_3; break;
			case 2: jelly = StuffList.COBERTURA_2; break;
			case 1: jelly = StuffList.COBERTURA_1; break;
			case 0: jelly = StuffList.SIN_COBERTURA; break;
			default: jelly = null;
		}
		return jelly;
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
	public void efectoOndaExpansiva(int fila, int col) {
		if(fila>=0 && fila < FILAS && col>=0 && col<COLS && tableroCoberturas[fila][col]>0) {
			tableroCoberturas[fila][col]--;
			for (Observer o: obs) o.onDestroyCover(fila, col, intToCover(tableroCoberturas[fila][col]));
		}
	}
	
	@Override
	public Chucheria getElementAt(int i, int j) {
		if (tableroCoberturas[i][j] == 0)
			return tableroChuches[i][j];
		else
			return null;
	}
	
	
	@Override
	public StuffPile getPileOfElementsAt(int fila, int col) {
		StuffList candy = StuffList.SIN_CARAMELO;
		if (tableroCoberturas[fila][col] == 0)
			candy = tableroChuches[fila][col].getID();
		
		return new StuffPile(
				candy, 
				intToWhiteJelly(tableroGelatinas[fila][col]),
				intToCover(tableroCoberturas[fila][col])
				);
	}
	
	@Override
	public GameType getGameType() {
		return GameType.JELLY_COVER_BASIC;
	}
	
	@Override
	public void introducir(Chucheria candy, int fila, int col, boolean animateTransform) {
		if (this.tableroCoberturas[fila][col] == 0) {
			tableroChuches[fila][col] = candy;
			if (animateTransform) for (Observer o: obs) o.onTransformCandy(fila, col, candy.getID());
		}
		
	}

	@Override
	public void destruir(int fila, int col) { 
		if(tableroCoberturas[fila][col]>0) {
			tableroCoberturas[fila][col]--;
			for (Observer o: obs) o.onDestroyCover(fila, col, intToCover(tableroGelatinas[fila][col]));
		}
		else  {
			if (tableroChuches[fila][col] != null) {
				boolean debeDestruirse = tableroChuches[fila][col].destruir(this, fila, col);
				if (debeDestruirse) {
					this.suprimir(fila, col, true);
				}
				else
					this.addToDestruirMasTarde(fila, col);	
			}
			else if(tableroGelatinas[fila][col]>0) {
				tableroGelatinas[fila][col]--;
				for (Observer o: obs) o.onDestroyJelly(fila, col, intToWhiteJelly(tableroGelatinas[fila][col]));
			}
		}
			
	}
	
	@Override
	public void suprimir(int fila, int col, boolean realDestroy) {
		tableroChuches[fila][col] = null;
		if (realDestroy) {
			for (Observer o: obs) o.onDestroyCandy(fila, col);
			if(tableroCoberturas[fila][col]>0) {
				tableroCoberturas[fila][col]--;
				for (Observer o: obs) o.onDestroyCover(fila, col, intToCover(tableroGelatinas[fila][col]));
			}
			else if(tableroGelatinas[fila][col]>0) {
				tableroGelatinas[fila][col]--;
				for (Observer o: obs) o.onDestroyJelly(fila, col, intToWhiteJelly(tableroGelatinas[fila][col]));
			}
		}
	}

	@Override
	protected void rellenar() {
		for (int j=0; j<COLS; j++) {//De izqda a dcha
			int iExtr = FILAS-1; //Avanzadilla (de donde extrae)
			int iRec = FILAS-1; //Hueco (donde coloca)
			
			while (iExtr>=0) { //De abajo a arriba hacemos caer
				if(tableroCoberturas[iExtr][j] > 0) {
					iExtr--;
					iRec = iExtr;
				}
				else if (tableroChuches[iExtr][j] == null)
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
				crear(new Caramelo(), iExtr, iRec, j, j, false);
				iRec--;
				iExtr--;
			}
			
			
		}	
	}

	
	@Override
	protected void swap(int fila1, int col1, int fila2, int col2) {
		Chucheria aux = tableroChuches[fila1][col1];
		tableroChuches[fila1][col1] = tableroChuches[fila2][col2];
		tableroChuches[fila2][col2] = aux;	
		for (Observer o: obs) o.onSwapCandy(fila1, col1, fila2, col2);
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
	
	protected void addToDestruirMasTarde(int fila, int col) {
		this.destruirMasTarde.addElement(new ChucheYcoordBasic(tableroChuches[fila][col], fila, col));
	}
	
	/**
	 * Recorre el tablero en busca de combinaciones EN HORIZONTAL
	 * @return Vector de combinaciones de filas, cada una recogida por la clase SegmentoFila
	 * @see SegmentoFila
	 */
	protected Vector<SegmentoFila> sacarCombinFilas() {
		int cont;
		Color color;
		Vector<SegmentoFila> combinFila = new Vector<SegmentoFila>();
		for (int i=0; i<FILAS; i++) {
			color = Color.NINGUNO;	//Preparamos la fila
			cont = 0;
			for (int j=0; j<COLS; j++) {	//Recorremos la fila
				if (this.tableroCoberturas[i][j] == 0) {
					if (color == tableroChuches[i][j].getColor()) //Si coincide color...
						cont++;								//...hay otro más del mismo color seguido
					
					else {									//Si no coincide color...
						if (cont >= 3 && color != Color.NINGUNO){ //Si ha habido tres o más de un color seguidos...
							combinFila.add(new SegmentoFila(this.getElementAt(i,j-1).getColor(), i, j-cont, j-1)); //...marcamos para destruir
						}
						color = tableroChuches[i][j].getColor();	//Ademas, actualizamos el color...
						cont = 1;	//... y el contador
					}
				}
			}
			if (cont >= 3 && color != Color.NINGUNO){ //Si ha habido tres o más de un color seguidos...
				combinFila.add(new SegmentoFila(this.getElementAt(i,COLS-1).getColor(), i, COLS-cont, COLS-1)); //...marcamos para destruir
			}
		}
		
		return combinFila;
	}


	/**
	 * Recorre el tablero en busca de combinaciones EN VERTICAL
	 * @return Vector de combinaciones de columnas, cada una recogida por la clase SegmentoCol
	 * @see SegmentoCol
	 */
	protected Vector<SegmentoCol> sacarCombinCols() {
		int cont;
		Color color;
		Vector<SegmentoCol> combinCol = new Vector<SegmentoCol>();
		for (int j=0; j<COLS; j++){
			color = Color.NINGUNO;	//Preparamos la columna
			cont = 0;
			for (int i=0; i<FILAS; i++) {	//Recorremos la columa
				if (this.tableroCoberturas[i][j] == 0) {
					if (color == tableroChuches[i][j].getColor()) //Si coincide color...
						cont++;								//...hay otro más del mismo color seguido
					
					else {									//Si no coincide color...
						if (cont >= 3 && color != Color.NINGUNO) //Si ha habido tres o más de un color seguidos...
							combinCol.add(new SegmentoCol(this.getElementAt(i-1,j).getColor(), j, i-cont, i-1)); //...marcamos para destruir
						color = tableroChuches[i][j].getColor();	//Ademas, actualizamos el color...
						cont = 1;							//... y el contador
					}
				}
			}
			if (cont >= 3 && color != Color.NINGUNO) //Si ha habido tres o más de un color seguidos...
				combinCol.add(new SegmentoCol(this.getElementAt(FILAS-1,j).getColor(), j, FILAS-cont, FILAS-1)); //...marcamos para destruir
		}
		
		return combinCol;
	}


	/**Matriz de coberturas del tablero, representado como (fila,columna)*/
	protected int[][] tableroCoberturas;

	/**Matriz de chucherías que representa el tablero, representado como (fila,columna)*/
	protected Chucheria[][] tableroChuches;
	
	/**Matriz de gelatinas del tablero, representado como (fila,columna)*/
	protected int[][] tableroGelatinas;
	
	/** Las chucherías con varias fases de destrucciónse registran aquí
	 *  para volver a destruirse cuando el tablero esté estable*/
	protected Vector<ChucheYcoord> destruirMasTarde;
}
