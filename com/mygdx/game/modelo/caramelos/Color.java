package com.mygdx.game.modelo.caramelos;

import com.mygdx.game.controlador.StuffList;

public enum Color {

	AMARILLO,
	AZUL,
	ROJO,
	NARANJA,
	VERDE,
	MORADO, 
	NINGUNO;
	
	public static Color getRandomColor() {
		double color1 = Math.random()*Color.getNumColors(); //Doble en [0 , numColors)
		int color2 = (int) (Math.floor(color1) +1); //Int de 1 a numColors
		return Color.toColor(color2);
	}
	
	private static int getNumColors() {
		return 6;
	}
	
	/**
	 * A partir de un número entre 1 y numColors incluidos, devuelve un color
	 * @param x
	 * @return
	 */
	private static Color toColor(int x) {
		Color color = null;
		switch (x) {
			case 1: color = AMARILLO;	break;
			case 2: color = AZUL;		break;
			case 3: color = ROJO;		break;
			case 4: color = NARANJA;	break;
			case 5: color = VERDE;		break;
			case 6: color = MORADO;		break;		
		}
		return color;
	}
	/*
	private static int toInt(Color x) {
		int i = 0;
		switch (x) {
			case AMARILLO:	i = 1;	break;
			case AZUL:		i = 2;	break;
			case ROJO:		i = 3;	break;
			case NARANJA:	i = 4;	break;
			case VERDE:		i = 5;	break;
			case MORADO:	i = 6;	break;		
			default: break;
		}
		return i;
	}
	*/
	public static StuffList getInList_Normal(Color color) {
		StuffList id = null;
		switch (color) {
			case AMARILLO:	id = StuffList.CARAMELO_NORMAL_AMARILLO;	break;
			case AZUL:		id = StuffList.CARAMELO_NORMAL_AZUL;	break;
			case ROJO:		id = StuffList.CARAMELO_NORMAL_ROJO;	break;
			case NARANJA:	id = StuffList.CARAMELO_NORMAL_NARANJA;	break;
			case VERDE:		id = StuffList.CARAMELO_NORMAL_VERDE;	break;
			case MORADO:	id = StuffList.CARAMELO_NORMAL_MORADO;	break;
			default: break;
		}
		return id;
	}
	
	public static StuffList getInList_Rallado(Color color, boolean isHorizontal) {
		StuffList id = null;
		if (isHorizontal) {
			switch (color) {
				case AMARILLO:	id = StuffList.CARAMELO_RALLADO_H_AMARILLO;	break;
				case AZUL:		id = StuffList.CARAMELO_RALLADO_H_AZUL;	break;
				case ROJO:		id = StuffList.CARAMELO_RALLADO_H_ROJO;	break;
				case NARANJA:	id = StuffList.CARAMELO_RALLADO_H_NARANJA;	break;
				case VERDE:		id = StuffList.CARAMELO_RALLADO_H_VERDE;	break;
				case MORADO:	id = StuffList.CARAMELO_RALLADO_H_MORADO;	break;
				default: break;
			}
		}
		else {
			switch (color) {
				case AMARILLO:	id = StuffList.CARAMELO_RALLADO_V_AMARILLO;	break;
				case AZUL:		id = StuffList.CARAMELO_RALLADO_V_AZUL;	break;
				case ROJO:		id = StuffList.CARAMELO_RALLADO_V_ROJO;	break;
				case NARANJA:	id = StuffList.CARAMELO_RALLADO_V_NARANJA;	break;
				case VERDE:		id = StuffList.CARAMELO_RALLADO_V_VERDE;	break;
				case MORADO:	id = StuffList.CARAMELO_RALLADO_V_MORADO;	break;
				default: break;
			}
		}
		return id;
	}
	
	public static StuffList getInList_Envuelto(Color color, boolean isExploding) {
		StuffList id = null;
		if (isExploding) {
			switch (color) {
				case AMARILLO:	id = StuffList.CARAMELO_EXPLOTANDO_AMARILLO;	break;
				case AZUL:		id = StuffList.CARAMELO_EXPLOTANDO_AZUL;	break;
				case ROJO:		id = StuffList.CARAMELO_EXPLOTANDO_ROJO;	break;
				case NARANJA:	id = StuffList.CARAMELO_EXPLOTANDO_NARANJA;	break;
				case VERDE:		id = StuffList.CARAMELO_EXPLOTANDO_VERDE;	break;
				case MORADO:	id = StuffList.CARAMELO_EXPLOTANDO_MORADO;	break;
				default: break;
			}
		}
		else {
			switch (color) {
				case AMARILLO:	id = StuffList.CARAMELO_ENVUELTO_AMARILLO;	break;
				case AZUL:		id = StuffList.CARAMELO_ENVUELTO_AZUL;	break;
				case ROJO:		id = StuffList.CARAMELO_ENVUELTO_ROJO;	break;
				case NARANJA:	id = StuffList.CARAMELO_ENVUELTO_NARANJA;	break;
				case VERDE:		id = StuffList.CARAMELO_ENVUELTO_VERDE;	break;
				case MORADO:	id = StuffList.CARAMELO_ENVUELTO_MORADO;	break;
				default: break;
			}
		}
		return id;
	}
}
