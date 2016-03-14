package com.mygdx.game.controlador;

/** Clase que permite encapsular todos los elementos que van en una casilla
 */
public class StuffPile {
	/**
	 * Construye un conjunto de StuffList, encapsulando todos los elementos
	 * que van en una casilla <br>
	 * <b> NOTA: </b> No se comprueba que los IDs sean del subconjunto correcto
	 * @param candy ID de la chuchería
	 * @param jelly ID de la gelatina
	 */
	public StuffPile(StuffList candy, StuffList jelly) {
		this.candy = candy;
		this.jelly = jelly;
	}
	
	/**
	 * Extrae el ID de la chuchería del conjunto
	 * @return ID de la chuchería
	 */
	public StuffList getCandy() {
		return candy;
	}
	
	/**
	 * Extrae el ID de la gelatina del conjunto
	 * @return ID de la gelatina
	 */
	public StuffList getJelly() {
		return jelly;
	}
	
	private StuffList candy;
	private StuffList jelly;
}
