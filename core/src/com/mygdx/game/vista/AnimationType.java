package com.mygdx.game.vista;

/**
 * Enumera los distintos tipos de animaciones posibles, ya sean movimientos del sprite o sprites animados
 */
public enum AnimationType {
		//Chucher�as
		NONE, DESTROY,
		SWAP_H, SWAP_V, SWAP_FREE,
		FALL, FALL_L, FALL_R,
		
		//Gelatina
		JELLY_DESTROY,
		
		//cobertura
		COVER_DESTROY
}
