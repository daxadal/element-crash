package com.mygdx.game.modelo.caramelos;

import com.mygdx.game.controlador.StuffList;
import com.mygdx.game.modelo.tableros.Tablero;


public class Ingrediente implements Chucheria{
	
	/**
	 * Crea una cereza azul, roja o sin color
	 * @param color Color de la cereza. Cualquier valor distinto de rojo o azul
	 * se entenderá como ninguno
	 */
	public Ingrediente(Color color) {
		this.color = color;
	}

	@Override
	public boolean destruir(Tablero tablero, int fila, int col) {
		if (color == Color.ROJO && col < tablero.getColumns()/2)
			return true;
		else if (color == Color.AZUL && col >= tablero.getColumns()/2)
			return true;
		else
			return false;
	}

	@Override
	public boolean efectoIntercambio(Tablero tablero, int filaSelf,
			int colSelf, int filaOther, int colOther, boolean iMovedThis) {
		return false;
	}

	@Override
	public boolean efectoOndaExpansiva(Tablero tablero, int fila, int col) {
		return false;
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
		switch (color) {
			case AZUL:	return StuffList.CEREZA_AZUL;
			case ROJO:	return StuffList.CEREZA_ROJA;
			default:	return StuffList.CEREZA;
		}
	}

	@Override
	public Color getColor() {
		return Color.NINGUNO;
	}
	
	private Color color;

}
