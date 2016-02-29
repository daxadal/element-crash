package com.mygdx.game.modelo.caramelos;

import com.mygdx.game.controlador.StuffList;
import com.mygdx.game.modelo.tableros.Tablero;

/**
 * Representa cualquier objeto intercambiable que se encuentre en el tablero.
 */
public interface Chucheria {
	
	/**
	 * Activa los diversos efectos asociados a la destruccion como destrucci�n de caramelos adicionales,
	 * aumento de puntos, actualizacion de estad�sticas, etc. <br><br>
	 * <b>NOTA:</b> No destruye la chucher�a. Esto debe hacerse en el tablero
	 * usando destruir(fila,col)
	 * @param tablero Tablero en cuesti�n
	 * @param fila Fila de la chucher�a
	 * @param col Columna de la chucher�a
	 * @return Devuelve si el caramelo debe ser eliminado del tablero.
	 * Especialmente importante en destrucciones de varias fases (ej: caramelos envueltos)
	 */
	public boolean destruir(Tablero tablero, int fila, int col);
	
	/**
	 * Analiza las chucher�as para saber si el mero intercambio de estas produce alg�n efecto.
	 * S�lo se analizan los efectos en los que la chucher�a "Self" (la que se pasa por par�metro 
	 * impl�cito) es responsable de manera dominante o igualitaria repecto a la chucher�a "Other" 
	 * (la cual no se pasa por par�metro impl�cito). <br><br>
	 * <b>NOTA:</b> Las chucher�as implicadas deben destruirse a traves del tablero, 
	 * recomendablemente usando suprimir(int,int)
	 * 
	 * @param tablero Tablero en cuesti�n
	 * @param filaSelf Fila de la chucher�a "Self"
	 * @param colSelf Columna de la chucher�a "Self"
	 * @param filaOther Fila de la chucher�a "Other"
	 * @param colOther Columna de la chucher�a "Other"
	 * @param iMovedThis Indica si es el primer caramelo que se puls� (true) o el segundo (false),
	 * ya que el primero que se pulsa suele ser el que se quiere intercambiar.
	 * @return True si el intercambio produce alg�n efecto. False en caso contrario.
	 */
	public boolean efectoIntercambio(Tablero tablero, int filaSelf, int colSelf, 
			int filaOther, int colOther, boolean iMovedThis);
	
	/** M�todo que comprueba si una chucher�a (no necesariamente caramelo) en su posoci�n produce una
	 * combinaci�n. Recibe el tablero y las cordenadas de la chucher�a y devuelve un booleano diciendo 
	 * si se produce combinaci�n, destruyendo cuches o creando chuches especiales. <br>
	 * <br>
	 * Nota: el par�metro implicito es la chucher�a que se quiere combinar.
	 *
	 * @param tablero Tablero en cuesti�n
	 * @param fila Fila de la chucher�a que se quiere combinar.
	 * @param col Columna de la chucher�a que se quiere combinar.
	 * @return True si se produce combinaci�n. False en caso contrario.
	 */
	public boolean combinarDeIntercambio(Tablero tablero, int fila, int col);

	/**
	 * Decide si dos chucher�as son equivalentes, es decir, si tienen color y el color de ambas
	 * coincide. <br><br>
	 * <b>NOTA:</b> Una de ellas se pasa por par�metro impl�cito.
	 * 
	 * @param otro La segunda Chucher�a
	 * @return True si son equivalentes. False en caso contrario.
	 */
	public boolean equals(Chucheria otro);

	/**
	 * Devuelve un objeto de StuffList con el ID de la chucher�a
	 * @return
	 */
	public StuffList getID();
	
	/**
	 * Devuelve el color de la chucher�a
	 * @return Color de la chucher�a entre los disponibles. Si no tiene color
	 * devuelve Color.NINGUNO
	 */
	public Color getColor();

}