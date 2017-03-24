package com.icecubelab.elementcrash.modelo.caramelos;

import com.icecubelab.elementcrash.controlador.StuffList;
import com.icecubelab.elementcrash.modelo.tableros.Tablero;

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
		tablero.suprimir(fila, col, false);
	
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
		
		tablero.introducir(this, fila, col, false);
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
			
			tablero.suprimir(filaSelf, colSelf, true);
			tablero.suprimir(filaOther, colOther, true);
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
