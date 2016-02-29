
package com.mygdx.game.controlador;

import com.mygdx.game.modelo.tableros.Tablero;
import com.mygdx.game.vista.MyGdxGame;

public class Controlador {
	
	public Controlador(Tablero tablero, MyGdxGame vista) {
		this.tablero = tablero;
		this.vista = vista;
	}

	public void intercambiar(int fila1, int col1, int fila2, int col2) {
		boolean hay_cambios =  tablero.intercambiar(fila1, col1, fila2, col2);
		if (hay_cambios)
			vista.updateData(tablero);
	}
	
	private Tablero tablero;
	private MyGdxGame vista;
}
