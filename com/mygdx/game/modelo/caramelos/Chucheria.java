package com.mygdx.game.modelo.caramelos;

import com.mygdx.game.controlador.StuffList;
import com.mygdx.game.modelo.tableros.Tablero;

/**
 * Representa cualquier objeto intercambiable que se encuentre en el tablero.
 */
public interface Chucheria {
	
	/**
	 * Activa los diversos efectos asociados a la destruccion como destrucción de caramelos adicionales,
	 * aumento de puntos, actualizacion de estadísticas, etc. <br><br>
	 * <b>NOTA:</b> No destruye la chuchería. Esto debe hacerse en el tablero
	 * usando destruir(fila,col)
	 * @param tablero Tablero en cuestión
	 * @param fila Fila de la chuchería
	 * @param col Columna de la chuchería
	 * @return Devuelve si el caramelo debe ser eliminado del tablero.
	 * Especialmente importante en destrucciones de varias fases (ej: caramelos envueltos)
	 */
	public boolean destruir(Tablero tablero, int fila, int col);
	
	/**
	 * Analiza las chucherías para saber si el mero intercambio de estas produce algún efecto.
	 * Sólo se analizan los efectos en los que la chuchería "Self" (la que se pasa por parámetro 
	 * implícito) es responsable de manera dominante o igualitaria repecto a la chuchería "Other" 
	 * (la cual no se pasa por parámetro implícito). <br><br>
	 * <b>NOTA:</b> Las chucherías implicadas deben destruirse a traves del tablero, 
	 * recomendablemente usando suprimir(int,int)
	 * 
	 * @param tablero Tablero en cuestión
	 * @param filaSelf Fila de la chuchería "Self"
	 * @param colSelf Columna de la chuchería "Self"
	 * @param filaOther Fila de la chuchería "Other"
	 * @param colOther Columna de la chuchería "Other"
	 * @param iMovedThis Indica si es el primer caramelo que se pulsó (true) o el segundo (false),
	 * ya que el primero que se pulsa suele ser el que se quiere intercambiar.
	 * @return True si el intercambio produce algún efecto. False en caso contrario.
	 */
	public boolean efectoIntercambio(Tablero tablero, int filaSelf, int colSelf, 
			int filaOther, int colOther, boolean iMovedThis);
	
	/**
	 * Indica que en la casilla de al lado a la que se encuentra
	 * esta chuchería se ha destruido otra chuchería.
	 * Dependiendo de lo que contenga la casilla, puede tener distintos efectos (o ninguno),
	 * ya que puede implicar la destrucción de esta chuchería. <br><br>
	 * <b>NOTA:</b> No comprueba que los parametros se encuentren dentro de los límites. En
	 * cambio, sí comprueba que el elemento a destruir no sea nulo
	 * @param tablero Tablero en cuestión
	 * @param fila Fila de la chuchería
	 * @param col Columna de la chuchería
	 * @return True si el intercambio produce algún efecto. False en caso contrario.
	 */
	public abstract boolean efectoOndaExpansiva(Tablero tablero, int fila, int col);
	
	/** Método que comprueba si una chuchería (no necesariamente caramelo) en su posoción produce una
	 * combinación. Recibe el tablero y las cordenadas de la chuchería y devuelve un booleano diciendo 
	 * si se produce combinación, destruyendo cuches o creando chuches especiales. <br>
	 * <br>
	 * Nota: el parámetro implicito es la chuchería que se quiere combinar.
	 *
	 * @param tablero Tablero en cuestión
	 * @param fila Fila de la chuchería que se quiere combinar.
	 * @param col Columna de la chuchería que se quiere combinar.
	 * @return True si se produce combinación. False en caso contrario.
	 */
	public boolean combinarDeIntercambio(Tablero tablero, int fila, int col);

	/**
	 * Decide si dos chucherías son equivalentes, es decir, si tienen color y el color de ambas
	 * coincide. <br><br>
	 * <b>NOTA:</b> Una de ellas se pasa por parámetro implícito.
	 * 
	 * @param otro La segunda Chuchería
	 * @return True si son equivalentes. False en caso contrario.
	 */
	public boolean equals(Chucheria otro);

	/**
	 * Devuelve un objeto de StuffList con el ID de la chuchería
	 * @return
	 */
	public StuffList getID();
	
	/**
	 * Devuelve el color de la chuchería
	 * @return Color de la chuchería entre los disponibles. Si no tiene color
	 * devuelve Color.NINGUNO
	 */
	public Color getColor();

}