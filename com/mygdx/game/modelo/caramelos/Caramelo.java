package com.mygdx.game.modelo.caramelos;

import com.mygdx.game.controlador.StuffList;
import com.mygdx.game.modelo.tableros.Tablero;

public class Caramelo implements Chucheria {

	/** Crea un caramelo de color aleatorio */
	public Caramelo() {
		this.color = Color.getRandomColor();
	}
	
	/** Crea un caramelo de un color específico
	 * @param color Color del caramelo a crear */
	public Caramelo(Color color) {
		this.color = color;
	}
	
	@Override
	public Color getColor() {
		return this.color;
	}
	
	@Override
	public boolean destruir(Tablero tablero, int fila, int col) {
		return true;
	}

	
	@Override
	public boolean efectoIntercambio(Tablero tablero, int filaSelf, int colSelf,
			int filaOther, int colOther, boolean iMovedThis) {
		return false;
	}

	@Override
	public boolean efectoOndaExpansiva(Tablero tablero, int fila, int col) {
		return false;
	}

	@Override
	public boolean combinarDeIntercambio(Tablero tablero, int fila, int col) { 
		boolean hayCambios = false;
		
		if (color != Color.NINGUNO) {
			//buscar caramelos de mismo color
			int contIzq = contarIguales(tablero, fila, col, 0, -1);
			int contDch = contarIguales(tablero, fila, col, 0, +1);
			int contAr = contarIguales(tablero, fila, col, -1, 0);
			int contAb = contarIguales(tablero, fila, col, +1, 0);
			
			//Destrucción
			int enFila = contIzq + contDch + 1;
			int enCol = contAr + contAb + 1;
			if (enFila >= 3) {
				for (int i=1; i<=contIzq; i++)
					tablero.destruir(fila, col-i);
				for (int i=1; i<=contDch; i++)
					tablero.destruir(fila, col+i);
			}
			if (enCol >= 3) {
				for (int i=1; i<=contAr; i++)
					tablero.destruir(fila-i, col);
				for (int i=1; i<=contAb; i++)
					tablero.destruir(fila+i, col);
			}
			if (enFila >= 3 || enCol >= 3) {
				tablero.destruir(fila, col);
				hayCambios = true;
			}
			
			//Llamar efecto onda expansiva del tablero,
			if (contAr > 1 || contIzq > 1)
				tablero.efectoOndaExpansiva(fila-1, col-1);
			if (contAr > 1 || contDch > 1)
				tablero.efectoOndaExpansiva(fila-1, col+1);
			if (contAb > 1 || contIzq > 1)
				tablero.efectoOndaExpansiva(fila+1, col-1);
			if (contAb > 1 || contDch > 1)
				tablero.efectoOndaExpansiva(fila+1, col+1);
			
			if (contAr == 1) {
				tablero.efectoOndaExpansiva(fila-2, col-1);
				tablero.efectoOndaExpansiva(fila-2, col);
				tablero.efectoOndaExpansiva(fila-2, col+1);
			}
			else if (contAr == 2) {
				tablero.efectoOndaExpansiva(fila-2, col-1);
				tablero.efectoOndaExpansiva(fila-2, col+1);
				tablero.efectoOndaExpansiva(fila-3, col-1);
				tablero.efectoOndaExpansiva(fila-3, col);
				tablero.efectoOndaExpansiva(fila-3, col+1);
			}
			
			if (contAb == 1) {
				tablero.efectoOndaExpansiva(fila+2, col-1);
				tablero.efectoOndaExpansiva(fila+2, col);
				tablero.efectoOndaExpansiva(fila+2, col+1);
			}
			else if (contAb == 2) {
				tablero.efectoOndaExpansiva(fila+2, col-1);
				tablero.efectoOndaExpansiva(fila+2, col+1);
				tablero.efectoOndaExpansiva(fila+3, col-1);
				tablero.efectoOndaExpansiva(fila+3, col);
				tablero.efectoOndaExpansiva(fila+3, col+1);
			}
			
			if (contIzq == 1) {
				tablero.efectoOndaExpansiva(fila-1, col-2);
				tablero.efectoOndaExpansiva(fila, col-2);
				tablero.efectoOndaExpansiva(fila+1, col-2);
			}
			else if (contIzq == 2) {
				tablero.efectoOndaExpansiva(fila-1, col-2);
				tablero.efectoOndaExpansiva(fila+1, col-2);
				tablero.efectoOndaExpansiva(fila-1, col-3);
				tablero.efectoOndaExpansiva(fila, col-3);
				tablero.efectoOndaExpansiva(fila+1, col-3);
			}
			
			if (contDch == 1) {
				tablero.efectoOndaExpansiva(fila-1, col+2);
				tablero.efectoOndaExpansiva(fila, col+2);
				tablero.efectoOndaExpansiva(fila+1, col+2);
			}
			else if (contDch == 2) {
				tablero.efectoOndaExpansiva(fila-1, col+2);
				tablero.efectoOndaExpansiva(fila+1, col+2);
				tablero.efectoOndaExpansiva(fila-1, col+3);
				tablero.efectoOndaExpansiva(fila, col+3);
				tablero.efectoOndaExpansiva(fila+1, col+3);
			}
			
			
			//Creación de caramelos especiales
			
			//Envuelto
			if (enFila >= 3 && enCol >= 3) {
				this.crearEnCol(tablero, new Envuelto(this.color), fila, col, contAr, contAb);
			}
			
			//Horizontal (rallado V, bomba de color)
			if(enFila == 4) {
				this.crearEnFila(tablero, new Rallado(this.color, false), fila, col, contIzq, contDch);	
			}
			else if (enFila >= 5) {
				int numBombas = enFila - 4;
				for (int i=0; i<numBombas; i++)
					this.crearEnFila(tablero, new BombaColor(), fila, col, contIzq, contDch);	
			}

			//Vertical (rallado H, bomba de color)
			if(enCol == 4) {
				this.crearEnCol(tablero, new Rallado(this.color, true), fila, col, contAr, contAb);
			}
			else if (enCol >= 5) {
				int numBombas = enCol - 4;
				for (int i=0; i<numBombas; i++)
					this.crearEnCol(tablero, new BombaColor(), fila, col, contAr, contAb);
			}
			
		}
		
		return hayCambios;
	}

	
	@Override
	public boolean equals(Chucheria otro) {
		if (otro instanceof Caramelo) {
			Caramelo otro2 = (Caramelo) otro;
			return this.color.equals(otro2.color);
		}
		else
			return false;
	}
	
	@Override
	public StuffList getID() {
		return Color.getInList_Normal(color);
	}
	
	/** Cuenta cuantos caramelos hay del mismo color a uno dado (sin contar éste) en una dirección dada,
	 * codificada de la siguente manera por los parámetros (i,j) :
	 * <ul>
	 * <li> Izquierda: (0,-1)
	 * <li> Derecha: (0,+1)
	 * <li> Arriba: (-1,0)
	 * <li> Abajo: (+1,0)
	 * </ul>
	 * <b>NOTA:</b> Dada la implementación, el máximo resultado de esta función es 2, pero es suficiente
	 * dadas las normas del juego. En caso de cambio de normas, la función debe ser reimplementada. <br><br>
	 * @param tablero Tablero en cuestión
	 * @param fila Fila de la chuchería que se quiere comparar.
	 * @param col Columna de la chuchería que se quiere comparar.
	 * @param i Parámetro para indicar la dirección.
	 * @param j Parámetro para indicar la dirección.
	 * @return Número de caramelos iguales en la dirección dada, sin contar el caramelo que se pasa
	 * por parámetro. Valor entre 0 y 2.
	 */
	protected int contarIguales(Tablero tablero, int fila, int col, int i, int j) {
		int cont = 0; //Nº de caramelos iguales en la dirección dada
		try {
			if ( tablero.getElementAt(fila+i, col+j) != null 
					&& color == tablero.getElementAt(fila+i, col+j).getColor()) {
				cont++;
				if (tablero.getElementAt(fila+2*i, col+2*j) != null 
						&& color == tablero.getElementAt(fila+2*i, col+2*j).getColor())
					cont++;
			}
		} catch (ArrayIndexOutOfBoundsException ex) {}
		
		return cont;
	}

	/**
	 * Crea un caramelo del tipo especificado en el tablero lo más cerca
	 *  posible del lugar donde se ha producido el intercambio, dentro del hueco
	 *  que ha dejado la combinación.
	 * @param tablero Tablero en cuestión
	 * @param candy Caramelo a crear
	 * @param fila Fila donde se debe crear
	 * @param col Columna óptima para crearlo
	 * @param contIzq Margen libre a la izquierda (entre 0 y 2)
	 * @param contDch Margen libre a la derecha (entre 0 y 2)
	 */
	protected void crearEnFila(Tablero tablero, Chucheria candy, int fila, int col, int contIzq, int contDch) {	
		boolean creado = tablero.crear(candy, fila, fila, col, col, true);
		if (!creado && contIzq >=1) {
			creado = tablero.crear(candy, fila, fila, col-1, col-1, true);
			if (!creado && contDch >=1) {
				creado = tablero.crear(candy, fila, fila, col+1, col+1, true);
				if (!creado && contIzq >=2) {
					creado = tablero.crear(candy, fila, fila, col-2, col-2, true);
					if (!creado && contDch >=2) {
						creado = tablero.crear(candy, fila, fila, col+2, col+2, true);
					}
				}
			}
		}
	}

	/**
	 * Crea un caramelo del tipo especificado en el tablero lo más cerca
	 *  posible del lugar donde se ha producido el intercambio, dentro del hueco
	 *  que ha dejado la combinación.
		 * @param tablero Tablero en cuestión
	 * @param candy Caramelo a crear
	 * @param fila Fila óptima para crearlo
	 * @param col Columna donde se debe crear
	 * @param contAr Margen libre hacia arriba (entre 0 y 2)
	 * @param contAb Margen libre hacia abajo (entre 0 y 2)
	 */
	protected void crearEnCol(Tablero tablero, Chucheria candy, int fila, int col, int contAr, int contAb) {
		boolean creado = tablero.crear(candy, fila, fila, col, col, true);
		if (!creado && contAr >=1) {
			creado = tablero.crear(candy, fila-1, fila-1, col, col, true);
			if (!creado && contAb >=1) {
				creado = tablero.crear(candy, fila+1, fila+1, col, col, true);
				if (!creado && contAr >=2) {
					creado = tablero.crear(candy, fila-2, fila-2, col, col, true);
					if (!creado && contAb >=2) {
						creado = tablero.crear(candy, fila+2, fila+2, col, col, true);
					}
				}
			}
		}
	}

	/**Color de la chuchería*/
	protected Color color;
}
