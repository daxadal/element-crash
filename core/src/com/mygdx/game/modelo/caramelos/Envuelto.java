package com.mygdx.game.modelo.caramelos;

import com.mygdx.game.controlador.StuffList;
import com.mygdx.game.modelo.tableros.Tablero;

public class Envuelto extends Caramelo {

	public Envuelto() {
		super();
		this.isExploding = false;
		this.radio = 1;
	}
	
	public Envuelto(Color color) {
		super(color);
		this.isExploding = false;
		this.radio = 1;
	}
	
	@Override
	public Color getColor() {
		if (isExploding)
			return Color.NINGUNO;
		else
			return this.color;
	}
	
	/**
	 * Destruye las casillas que se encuentran a 2 de distancia
	 * (para el efecto de intercambio de Envueltos)
	 * @param tablero
	 * @param fila
	 * @param col
	 */
	private void destruirCapa2(Tablero tablero, int fila, int col) {
		boolean nFila0 = !(fila == 0);
		boolean nCol0 = !(col == 0);
		boolean nFilaUlt = !(fila == tablero.getRows()-1);
		boolean nColUlt = !(col == tablero.getColumns()-1);
		boolean nFila1 = !(fila <= 1);
		boolean nCol1 = !(col <= 1);
		boolean nFilaPenult = !(fila >= tablero.getRows()-2);
		boolean nColPenult = !(col >= tablero.getColumns()-2);
		
		if (nFila1)	{
			tablero.destruir(fila-2, col);
			if (nCol0)		tablero.destruir(fila-2, col-1);
			if (nColUlt)	tablero.destruir(fila-2, col+1);
		}
		if (nFilaPenult) {
			tablero.destruir(fila+2, col);
			if (nCol0)		tablero.destruir(fila+2, col-1);
			if (nColUlt)	tablero.destruir(fila+2, col+1);
		}
		if (nCol1)	{
			tablero.destruir(fila, col-2);
			if (nFila0)		tablero.destruir(fila-1, col-2);
			if (nFilaUlt)	tablero.destruir(fila+1, col-2);
		}
		if (nColPenult)	{
			tablero.destruir(fila, col+2);
			if (nFila0)		tablero.destruir(fila-1, col+2);
			if (nFilaUlt)	tablero.destruir(fila+1, col+2);
		}
		if (nFila1 && nCol1)		tablero.destruir(fila-2, col-2);
		if (nFilaPenult && nCol1)	tablero.destruir(fila+2, col-2);
		if (nFila1 && nColPenult)	tablero.destruir(fila-2, col+2);
		if(nFilaPenult && nColPenult)	tablero.destruir(fila+2, col+2);
	}
	
	@Override
	public boolean destruir(Tablero tablero, int fila, int col) {
		boolean nFila0 = !(fila == 0);
		boolean nCol0 = !(col == 0);
		boolean nFilaUlt = !(fila == tablero.getRows()-1);
		boolean nColUlt = !(col == tablero.getColumns()-1);
		boolean debeDestruirse = false;
		
		tablero.suprimir(fila, col, false);
		
		if (nFila0)		tablero.destruir(fila-1, col);
		if (nFilaUlt)	tablero.destruir(fila+1, col);
		if (nCol0)		tablero.destruir(fila, col-1);
		if (nColUlt)	tablero.destruir(fila, col+1);
		if (nFila0 && nCol0)	tablero.destruir(fila-1, col-1);
		if (nFilaUlt && nCol0)	tablero.destruir(fila+1, col-1);
		if (nFila0 && nColUlt)	tablero.destruir(fila-1, col+1);
		if(nFilaUlt && nColUlt)	tablero.destruir(fila+1, col+1);
		
		if (radio >= 2) destruirCapa2(tablero, fila, col);
		
		if (this.isExploding) { //Está en la segunda fase
			debeDestruirse = true;
			tablero.introducir(this, fila, col, false);
		}
		else { //Está en la primera fase
			this.isExploding = true;
			debeDestruirse = false;
			tablero.introducir(this, fila, col, true);
		}
		
		return debeDestruirse;
	}
	
	private void destruirFila(Tablero tablero, int fila) {
		for (int j=0; j<tablero.getColumns(); j++) 
				tablero.destruir(fila, j);
		
	}
	
	private void destruirCol(Tablero tablero, int col) {
		for (int i=0; i<tablero.getRows(); i++) 
				tablero.destruir(i, col);
		
	}
	
	@Override
	public boolean efectoIntercambio(Tablero tablero, int filaSelf, int colSelf,
			int filaOther, int colOther, boolean iMovedThis) {
		
		Chucheria other = tablero.getElementAt(filaOther, colOther);	
		boolean hayCambios = false;
		
		if (other instanceof Rallado) {
			if (iMovedThis) {
				boolean nFila0 = !(filaSelf == 0);
				boolean nCol0 = !(colSelf == 0);
				boolean nFilaUlt = !(filaSelf == tablero.getRows()-1);
				boolean nColUlt = !(colSelf == tablero.getColumns()-1);
				
				tablero.suprimir(filaSelf, colSelf, true);
				tablero.suprimir(filaOther, colOther, true);
				
				this.destruirFila(tablero, filaSelf);
				this.destruirCol(tablero, colSelf);
				if (nFila0)		this.destruirFila(tablero, filaSelf-1);
				if (nFilaUlt)	this.destruirFila(tablero, filaSelf+1);
				if (nCol0)		this.destruirCol(tablero, colSelf-1);
				if (nColUlt)	this.destruirCol(tablero, colSelf+1);
			}
			else {
				boolean nFila0 = !(filaOther == 0);
				boolean nCol0 = !(colOther == 0);
				boolean nFilaUlt = !(filaOther == tablero.getRows()-1);
				boolean nColUlt = !(colOther == tablero.getColumns()-1);
				
				tablero.suprimir(filaOther, colOther, true);
				tablero.suprimir(filaSelf, colSelf, true);
				
				this.destruirFila(tablero, filaOther);
				this.destruirCol(tablero, colOther);
				if (nFila0)		this.destruirFila(tablero, filaOther-1);
				if (nFilaUlt)	this.destruirFila(tablero, filaOther+1);
				if (nCol0)		this.destruirCol(tablero, colOther-1);
				if (nColUlt)	this.destruirCol(tablero, colOther+1);
			}
			
			hayCambios = true;
		}
		else if (other instanceof Envuelto) {
			this.radio = 2;
			//((Envuelto) other).radio = 2;
			tablero.suprimir(filaOther, colOther, true);
			tablero.destruir(filaSelf, colSelf);
			
			
			hayCambios = true;
		}
		return hayCambios;
	}
	
	@Override
	public StuffList getID() {
		return Color.getInList_Envuelto(color, isExploding);
	}
	
	protected boolean isExploding;
	protected int radio;
}
