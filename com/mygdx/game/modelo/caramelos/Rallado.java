package com.mygdx.game.modelo.caramelos;

import com.mygdx.game.controlador.StuffList;
import com.mygdx.game.modelo.tableros.Tablero;

public class Rallado extends Caramelo {
	
	public Rallado(boolean isHorizontal) {
		super();
		this.isHorizontal = isHorizontal;
	}
	
	public Rallado(Color color, boolean isHorizontal) {
		super(color);
		this.isHorizontal = isHorizontal;
	}
	
	@Override
	public boolean destruir(Tablero tablero, int fila, int col) {
		super.destruir(tablero, fila, col);	
		tablero.suprimir(fila, col);
	
		if (isHorizontal) {
			for (int j=0; j<tablero.getColumns(); j++) {
					tablero.destruir(fila, j);
			}
		}
		else {
			for (int i=0; i<tablero.getRows(); i++) {
					tablero.destruir(i, col);
			}
		}
		
		tablero.crear(this, fila, fila, col, col);
		return true;
	}
	
	@Override
	public boolean efectoIntercambio(Tablero tablero, int filaSelf, int colSelf,
			int filaOther, int colOther, boolean iMovedThis) {
		
		Chucheria other = tablero.getElementAt(filaOther, colOther);
		boolean hayCambios = false;
		
		if (other instanceof Rallado) {
			
			for (int i=0; i<tablero.getRows(); i++) {
				if (i != filaSelf && (colSelf != colOther || i != filaOther))
					tablero.destruir(i, colSelf);
			}
			for (int j=0; j<tablero.getColumns(); j++) {
				if (j != colSelf && (filaSelf != filaOther || j != colOther))
					tablero.destruir(filaSelf, j);
			}
			
			tablero.suprimir(filaSelf, colSelf);
			tablero.suprimir(filaOther, colOther);
			hayCambios = true;
		}
		return hayCambios;
	}
	
	@Override
	public StuffList getID() {
		return Color.getInList_Rallado(color, isHorizontal);
	}
	
	protected boolean isHorizontal;
}
