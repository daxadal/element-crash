package com.mygdx.game.modelo.caramelos;

import com.mygdx.game.controlador.StuffList;
import com.mygdx.game.modelo.tableros.Tablero;

public class BombaColor implements Chucheria {

	public BombaColor() {}
	
	private void destruirColor(Tablero tablero, Color color) {
		for(int i=0; i<tablero.getRows(); i++)
			for (int j=0; j<tablero.getColumns(); j++) {
				if (tablero.getElementAt(i, j) != null && tablero.getElementAt(i, j).getColor() == color)
					tablero.destruir(i, j);
			}
	}
	
	@Override
	public boolean destruir(Tablero tablero, int fila, int col) {
		destruirColor(tablero, Color.getRandomColor());
		return true;
	}

	@Override
	public boolean efectoIntercambio(Tablero tablero, int filaSelf,
			int colSelf, int filaOther, int colOther, boolean iMovedThis) {
		
		Chucheria other = tablero.getElementAt(filaOther, colOther);	
		boolean hayCambios = false;
		
		if (other instanceof Rallado) {
			tablero.suprimir(filaSelf, colSelf);
			tablero.suprimir(filaOther, colOther);
			for(int i=0; i<tablero.getRows(); i++)
				for (int j=0; j<tablero.getColumns(); j++) {
					if (tablero.getElementAt(i, j) != null && tablero.getElementAt(i, j).getColor() == other.getColor()) {
						tablero.suprimir(i, j);
						double rand = Math.random();
						if (rand < 0.5)
							tablero.crear(new Rallado(other.getColor(), true), i, i, j, j);
						else
							tablero.crear(new Rallado(other.getColor(), false), i, i, j, j);
					}
						
				}
			
			destruirColor(tablero, other.getColor());
			
			hayCambios = true;
		}
		else if (other instanceof Envuelto) {
			tablero.suprimir(filaSelf, colSelf);
			tablero.suprimir(filaOther, colOther);
			for(int i=0; i<tablero.getRows(); i++)
				for (int j=0; j<tablero.getColumns(); j++) {
					if (tablero.getElementAt(i, j) != null && tablero.getElementAt(i, j).getColor() == other.getColor()) {
						tablero.suprimir(i, j);
						tablero.crear(new Envuelto(other.getColor()), i, i, j, j);
					}
						
				}
			
			destruirColor(tablero, other.getColor());
			
			hayCambios = true;
		}
		else if (other instanceof Caramelo) {
			tablero.suprimir(filaSelf, colSelf);
			tablero.suprimir(filaOther, colOther);
			destruirColor(tablero, other.getColor());
			
			hayCambios = true;
		}
		else if (other instanceof BombaColor) {
			tablero.suprimir(filaSelf, colSelf);
			tablero.suprimir(filaOther, colOther);
			for(int i=0; i<tablero.getRows(); i++)
				for (int j=0; j<tablero.getColumns(); j++) {
						tablero.destruir(i, j);
				}
			
			hayCambios = true;
		}
		return hayCambios;
	}

	@Override
	public boolean combinarDeIntercambio(Tablero tablero, int fila, int col) {
		return false;
	}

	@Override
	public boolean equals(Chucheria otro) {
		return false;
	}

	@Override
	public StuffList getID() {
		return StuffList.BOMBA_COLOR;
	}

	@Override
	public Color getColor() {
		return Color.NINGUNO;
	}

}
