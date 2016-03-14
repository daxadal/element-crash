
package com.mygdx.game.controlador;

import com.mygdx.game.modelo.tableros.Tablero;
import com.mygdx.game.vista.MyGdxGame;

public class Controlador {
	
	public Controlador(Tablero tablero, MyGdxGame vista) {
		this.tablero = tablero;
		this.vista = vista;
	}
	
	/**
	 * Intercambia dos casillas cualesquiera (no necesariamente contiguas) y:
	 * <ol>
	 * <li> En caso de que sean caramelos especiales y el intercambio produzca algun efecto, 
	 * se ejecuta y se destruyen los caramelos pertinentes.
	 * <li> En caso contrario, se intenta buscar combinaciones de 3 o m�s. Si la b�squeda 
	 * tiene �xito, se destruyen los caramelos pertinentes y se crean caramelos especiales
	 * en caso necesario
	 * <li> En caso de que haya ocurrido algo de lo anterior (y por tanto, se hayan destruido 
	 * caramelos) se rellena el tablero de forma pertinente. En caso de que se generen m�s 
	 * combinaciones, se repiten los pasos 2 y 3.
	 * </ol>
	 * 
	 * <b>NOTA:</b> Si el usuario de esta clase quiere que las casillas que se intercambian sean 
	 * contiguas, debe comprobarlo antes de realizar la llamada a esta funci�n. <br>
	 * <b>NOTA:</b> En caso de que el intercambio sea fallido, se restablecen las posiciones 
	 * de los caramelos intercambiados. <br>
	 * <b>NOTA:</b> No comprueba que los parametros se encuentren dentro de los l�mites.<br>
	 * 
	 * @param fila1 Fila del primer caramelo a intercambiar
	 * @param col1 Columna del primer caramelo a intercambiar
	 * @param fila2 Fila del segundo caramelo a intercambiar
	 * @param col2 Columna del segundo caramelo a intercambiar
	 * @return True si el intecambio tiene �xito. False si es fallido.
	 */
	public void intercambiar(int fila1, int col1, int fila2, int col2) {
		boolean hay_cambios =  tablero.intercambiar(fila1, col1, fila2, col2);
		if (hay_cambios)
			vista.updateData(tablero);
	}
	
	/**
	 * Devuelve el ID la chucher�a en la posicion especificada. <br><br>
	 * <b>NOTA:</b> No comprueba que los parametros se encuentren dentro de los l�mites
	 */
	public StuffList getElementAt(int fila, int col) throws ArrayIndexOutOfBoundsException {
		return tablero.getElementAt(fila, col).getID();
	}
	
	/**
	 * Devuelve el ID la chucher�a en la posicion especificada. <br><br>
	 * <b>NOTA:</b> No comprueba que los parametros se encuentren dentro de los l�mites
	 */
	public StuffPile getPileOfElementsAt(int fila, int col) throws ArrayIndexOutOfBoundsException {
		return tablero.getPileOfElementsAt(fila, col);
	}
	
	/**
	 * Devuelve el tipo de juego que representa el tablero
	 * @return tipo de juego
	 */
	public GameType getGameType() {
		return tablero.getGameType();
	}
	
	private Tablero tablero;
	private MyGdxGame vista;
}
