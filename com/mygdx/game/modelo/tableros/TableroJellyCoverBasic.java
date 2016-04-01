package com.mygdx.game.modelo.tableros;

import com.mygdx.game.controlador.StuffList;
import com.mygdx.game.controlador.StuffPile;
import com.mygdx.game.modelo.caramelos.Chucheria;
import com.mygdx.game.modelo.tableros.Tablero.Observer;

public class TableroJellyCoverBasic extends TableroJellyBasic {
	
	public TableroJellyCoverBasic(int nivelGelatina, int nivelCobertura, int filasCobertura) {
		super(nivelGelatina);
		
		this.tableroCoberturas =new int[FILAS][COLS];
		for (int i=FILAS-filasCobertura; i<FILAS; i++) {
			for (int j=0; j<COLS; j++) {
					tableroCoberturas[i][j] = filasCobertura;
			}
		}
	}
	
	@Override
	public boolean intercambiar(int fila1, int col1, int fila2, int col2, boolean intercambioLibre) {
		boolean intercambioExitoso = false;
		if (	intercambioLibre //TODO comprobar que se puede mover (sin cobertura). Posiblemente nueva funcion en interfaz Tablero
				|| (fila1 == fila2 && Math.abs(col1-col2) == 1 )
				|| (col1 == col2 && Math.abs(fila1-fila2) == 1 )	)
			intercambioExitoso = super.intercambioGenerico(fila1, col1, fila2, col2);
		
		for (Observer o: obs) o.endOfInteraction();
		return intercambioExitoso;
	}
	
	@Override
	public boolean crear(Chucheria candy, int filaSpawn, int fila, int colSpawn, int col) {
		if (tableroCoberturas[fila][col] > 0)
			return super.crear(candy, filaSpawn, fila, colSpawn, col);
		else
			return false;
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
	
	@Override
	public void efectoOndaExpansiva(int fila, int col) {
		if(tableroCoberturas[fila][col]>0) {
			tableroCoberturas[fila][col]--;
			for (Observer o: obs) o.onDestroyCover(fila, col, intToCover(tableroCoberturas[fila][col]));
		}
	}
	
	@Override
	public StuffPile getPileOfElementsAt(int fila, int col)
			throws ArrayIndexOutOfBoundsException {
		
		return new StuffPile(
				tableroChuches[fila][col].getID(), 
				intToWhiteJelly(tableroGelatinas[fila][col]),
				intToCover(tableroCoberturas[fila][col])
				);
	}


	/**Matriz de coberturas del tablero, representado como (fila,columna)*/
	protected int[][] tableroCoberturas;
}
