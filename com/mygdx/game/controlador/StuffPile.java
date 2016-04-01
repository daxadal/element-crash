package com.mygdx.game.controlador;

/** Clase que permite encapsular todos los elementos que van en una casilla
 */
public class StuffPile {
	/**
	 * Construye un conjunto de StuffList, encapsulando todos los elementos
	 * que van en una casilla <br>
	 * <b> NOTA: </b> No se comprueba que los IDs sean del subconjunto correcto
	 * @param candy ID de la chucher�a
	 * @param jelly ID de la gelatina
	 */
	public StuffPile(StuffList candy, StuffList jelly, StuffList cover) {
		this.candy = candy;
		this.jelly = jelly;
		this.cover = cover;
	}
	
	/**
	 * Extrae el ID de la chucher�a del conjunto
	 * @return ID de la chucher�a
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
	
	/**
	 * Extrae el ID de la cobertura del conjunto
	 * @return ID de la cobertura
	 */
	public StuffList getCover() {
		return cover;
	}
	
	private StuffList candy;
	private StuffList jelly;
	private StuffList cover;
}
