package com.mygdx.game.vista;

import com.mygdx.game.controlador.GameType;

/**
 * Clase "MarginsAndDimensions" que implementa las medidas y posiciones de los elementos del juego. Parámetros:
 * <ul>
 * <li> width/height: ancho/alto de la pantalla
 * <li> (originX, originY) : Coordenadas de punto superior izquierdo del tablero.
 * <li> dim : ancho/alto de las casillas
 * <li> filas/cols : número de filas/columnas del tablero
 * </ul>
 * El eje horizontal crece hacia la izquierda, el vertical DECRECE hacia abajo
 */
public class MD {
	/**
	 * A partir de los nuevos datos, reestructura la pantalla, actualizando sus atributos
	 * @param width	ancho de la pantalla
	 * @param height altura de la pantalla
	 * @param filas número de filas del tablero
	 * @param cols número de columnas del tablero
	 * @param gameType Tipo de juego al quye se está jugando
	 */
	public static void rearrange(int width, int height, int filas, int cols, GameType gameType) {
		//Parameters
		MD.width = width;
		MD.height = height;
		MD.filas = filas;
		MD.cols = cols;
		
		//if(gameType == GameType.STEAL_2P)
		//	cols = cols+2;
		
		//Dim
		if( width/cols < height/filas)
			MD.dim = width / cols;
		else
			MD.dim = height / filas;
		
		//Origin
		switch (gameType) {
			case BASIC:	case JELLY_BASIC: case STEAL_2P:	
				MD.originX = 0;
				MD.originY = height;
				break;
		}
		
	}
	
	//Getters
	/**@return ancho de la pantalla*/
	public static int width() 	{return width;}
	/**@return alto de la pantalla*/
	public static int height() 	{return height;}
	/**@return número de filas del tablero*/
	public static int filas() 	{return filas;}
	/**@return número de columnas del tablero */
	public static int cols() 	{return cols;}
	/**@return ancho y alto de las casillas del tablero*/
	public static int dim()		{return dim;}
	/**@return coordenada X de la esquina superior izquierda del tablero
	 *  El eje X crece hacia la derecha. Usado para dibujar Texture*/
	public static int originX() {return originX;}
	/**@return coordenada Y de la esquina superior izquierda del tablero
	 *  El eje Y crece hacia arriba. Usado para dibujar Texture*/
	public static int originY() {return originY;}
	/**@return coordenada X de la esquina superior izquierda del tablero
	 *  El eje X crece hacia la derecha. Usado para dibujar BoundingBox*/
	public static int originX_BB() {return originX;}
	/**@return coordenada Y de la esquina superior izquierda del tablero
	 *  El eje Y crece hacia abajo. Usado para dibujar BoundingBox*/
	public static int originY_BB() {return height - originY;}


	//Attributes
	private static int width; 
	private static int height;
	private static int filas;
	private static int cols;
	private static int dim;
	private static int originX;
	private static int originY;

}
