package com.mygdx.game.modelo.tableros;

import com.mygdx.game.controlador.GameType;
import com.mygdx.game.controlador.Observable;
import com.mygdx.game.controlador.StuffList;
import com.mygdx.game.modelo.caramelos.Chucheria;
/**
 * Represnta el tablero donde se encuentran todos los elementos del juego con los que se 
 * puede interactuar de alguna manera o que pueden afectar a otros elementos interactuables. Se excluyen del 
 * tablero puntuaciones, decoraciones y otros elementos.
 */
public abstract class Tablero extends Observable<Tablero.Observer>{
	
	/** Inerfaz con las acciones relevantes que realiza el tablero.
	 * Cualquier vista que actualice el tablero debe implementarla,
	 * adem�s de registrarse como observador en la clase Tablero.
	 */
	public static interface Observer {
		
		/** Funci�n que debe ser llamada al acabr el intercambio */
		void endOfInteraction();
		
		/**Se han intercambiado dos caramelos expresados por las coordenadas
		 * (fila1, col1) y (fila2, col2). S�lo es necesario llamarlo una vez
		 * por intercambio. No importa en qu� orden se identifiquen los caramelos. */
		void onSwapCandy(int fila1, int col1, int fila2, int col2);
		
		/**Ha caido un caramelo de la posici�n (filaIni, colIni) a
		 * la posici�n (filaFin, colFin) <br> 
		 * <b> NOTA: </b> Permite ca�das hacia izquierda, derecha y, por supuesto, abajo
		 */
		void onFallCandy(int filaIni, int filaFin, int colIni, int colFin);
		
		/**Se ha creado un caramelo en la posici�n (fila, col).
		 * En caso de que caiga desde arriba, se usa el par�metro filaSpawn para indicarlo.
		 * @param candy ID del caramelo creado
		 * @param filaSpawn fila desde donde cae. Puede ser negativa. En caso de que no caiga
		 * desde arriba, tiene el mismo valor que el par�metro <b>fila</b>
		 * @param fila fila donde ha aparecido (posici�n final)
		 * @param colSpawn columna desde donde cae (para cuando la caida es lateral).
		 *  Puede ser negativa o mayor que el n�mero de colmnas. En caso de que no caiga
		 * desde un lado, tiene el mismo valor que el par�metro <b>col</b>
		 * @param col columna donde ha aparecido (posici�n final) */
		void onCreateCandy(StuffList candy, int filaSpawn, int fila, int colSpawn, int col);
		
		/** Se ha destruido el caramelo de la posici�n (fila, col) */
		void onDestroyCandy(int fila, int col);
		
		/** Se ha transformado el caramelo de la posici�n (fila, col) en uno de otro tipo */
		void onTransformCandy(StuffList candy, int fila, int col);
	}
	
	/** Almacena una chucher�a y sus coordenadas en el tablero para poder
	 * destruirla m�s tarde */
	protected interface ChucheYcoord {
		
		/** Destruye todas aquellas chucher�as que quedaron pendientes. Dado que la 
		 * chucher�a puede haber ca�do desde que se inicializ�, antes de llamar a 
		 * la funci�n destruir de la chucher�a se recorre la columna
		 * hacia abajo hasta encontrarla
		 * @param tablero Tablero en el que se encuentra la chucher�a
		 */
		public void destruir(Tablero tablero);

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
	public boolean intercambiar(int fila1, int col1, int fila2, int col2) {
		swap(fila1, col1, fila2, col2);
		
		//Llamar efectos especiales
		boolean hay_especial = this.getElementAt(fila1,col1).efectoIntercambio(
				this, fila1, col1, fila2, col2, true); //Efecto intercambio 1er caramelo
		if (!hay_especial)
			hay_especial = this.getElementAt(fila2,col2).efectoIntercambio(
					this, fila2, col2, fila1, col1, false); //Efecto intercambio 2o caramelo
		
		//Combinar (si no ha habido efecto especial)
		boolean comb1 = false; 
		boolean comb2 = false;
		if (!hay_especial) {
			if(this.getElementAt(fila1,col1) != null) 
				comb1 =  this.getElementAt(fila1,col1).combinarDeIntercambio(this, fila1, col1);
			if(this.getElementAt(fila2,col2) != null) 
				comb2 =  this.getElementAt(fila2,col2).combinarDeIntercambio(this, fila2, col2);
		}
		
		//Rellenar (si ha habido efecto o combinacion) o restablecer (en caso contrario)
		if (hay_especial || comb1 || comb2) {
			boolean inestable = true;
			while (inestable) {
				rellenar();
				inestable = combinarDeBarrido(); //Cierto si sale alguna combinacion
			}
			//Destruir envueltos y rellenar
			inestable = this.quedaPorDestruir();
			while (inestable) {
				this.destruirPendientes();
				while (inestable) {
					rellenar();
					inestable = combinarDeBarrido(); //Cierto si sale alguna combinacion
				}
				inestable = this.quedaPorDestruir();
			}
	
		}
		else {
			swap(fila1, col1, fila2, col2);
			
		}
		
		for (Observer o: obs) o.endOfInteraction();
		return hay_especial || comb1 || comb2;
	}

	/**
	 * Crea un caramelo en una casilla. La casilla debe estar vac�a para
	 *  que la creaci�n sea efectiva. Es la funci�n que se usar� por defecto.
	 *  <b>NOTA:</b> No comprueba que los parametros se encuentren dentro de los l�mites. En
	 * cambio, s� comprueba que la casilla est� vac�a.
	 * @param candy ID de la chucher�a
	 * @param filaSpawn Fila donde aparece (en caso de que caiga desde arriba).
	 * En Caso contrario filaSpawn = fila
	 * @param fila Fila de la casilla
	 * @param colSpawn Columna donde aparece (en caso de que caiga desde un lado).
	 * En Caso contrario colSpawn = col
	 * @param col Columna de la casilla
	 * @return Devuelve si se ha podido hacer la creaci�n
	 */
	public abstract boolean crear(Chucheria candy, int filaSpawn, int fila, int colSpawn, int col);
	
	/**
	 * Introduce un caramelo en la casilla descrita. Puede ser de utilidad para los efectos de intercambio. <br><br>
	 * <b>NO</b> llama a la funci�n crear del Obsever. Sin embargo, llama a la funci�n transformar del
	 * observer si se explicita <br>
	 * <b>NOTA:</b> No comprueba que los parametros se encuentren dentro de los l�mites, ni que la casilla
	 * est� vac�a
	 * @param candy
	 * @param fila Fila de la casilla
	 * @param fila
	 * @param col
	 * @param animateTransform Si es cierto, se llama al la funcion {@link Observer#onTransformCandy(StuffList, int, int)}
	 */
	public abstract void introducir(Chucheria candy, int fila, int col, boolean animateTransform);

	/**
	 * Destruye una casilla. Dependiendo de lo que contenga la casilla, puede tener distintos efectos,
	 * ya que puede implicar la destrucci�n de otros caramelos, actualizaci�n de puntos, etc. 
	 * Llama a la funci�n destruir de la chucher�a antes de eliminarla del tablero <br> 
	 * Es la funci�n que se usar� por defecto. <br> <br>
	 * <b>NOTA:</b> No comprueba que los parametros se encuentren dentro de los l�mites. En
	 * cambio, s� comprueba que el elemento a destruir no sea nulo
	 * @param fila Fila de la casilla
	 * @param col Columna de la casilla
	 */
	/*TODO Posiblemente a�adir par�metros caramelo destruido y forma de destruccion
	 *  (normal, poer rallado, por envuelto, por bomba de color...)
	 */
	public abstract void destruir(int fila, int col);

	/**
	 * Destruye una casilla, sin tener encuenta los efectos laterales que pueda tenga la chucher�a. 
	 * <b>NO</b> llama a la funci�n destruir de la chucher�a. Sin embargo, llama a la funci�n destruir del
	 * observer si se explicita <br>
	 * Puede ser de utilidad para los efectos de intercambio. <br><br>
	 * <b>NOTA:</b> No comprueba que los parametros se encuentren dentro de los l�mites. En
	 * cambio, s� comprueba que el elemento a destruir no sea nulo
	 * @param fila Fila de la casilla
	 * @param col Columna de la casilla
	 * @param animateDestroy Si es cierto, se llama al la funcion {@link Observer#onDestroyCandy(int, int)}
	 */
	public abstract void suprimir(int fila, int col, boolean animateDestroy);

	/** @return Numero de filas del tablero */
	public int getRows() {
		return FILAS;
	}

	/** @return Numero de columnas del tablero */
	public int getColumns() {
		return COLS;
	}

	/**
	 * Devuelve la chucher�a en la posicion especificada. <br><br>
	 * <b>NOTA:</b> No comprueba que los parametros se encuentren dentro de los l�mites
	 */
	public abstract Chucheria getElementAt(int fila, int col) throws ArrayIndexOutOfBoundsException;
	
	/**
	 * Devuelve el tipo de juego que representa el tablero
	 * @return tipo de juego
	 */
	public abstract GameType getGameType();
	
	/**
	 * Intercambia dos chucher�as, dadas por sus coordenadas en el tablero.
	 *  Tambi�n llama al m�todo observador onSwap(...)
	 * @param fila1 fila de la primera
	 * @param col1 columna de la primera
	 * @param fila2 fila de la segunda
	 * @param col2 columna de la segunda
	 */
	protected abstract void swap(int fila1, int col1, int fila2, int col2);
	
	/**
	 * Rellena el tablero, haciendo que los caramelos caigan (hacia alguna direcci�n)
	 * y posteriormente se creen caramelos nuevos para rellenar los huecos
	 */
	protected abstract void rellenar();
	
	/**
	 * Reccorre el tablero en busca de combinaciones, destruyendo los caramelos pertinentes y 
	 * generando caramelos especiales si es necesario
	 * @return Booleano que indica si se ha producido alguna combinacion y, por tanto, 
	 * es necesario rellenar el tablero
	 */
	protected abstract boolean combinarDeBarrido();
	
	/**
	 * Permite preguntar si quedan elementos por destruir cuando el tablero est� estable.
	 * �til cuando hay caramelos con varias fases de destrucci�n
	 * @return true si hay elementos que noecesitan otra fase de destrucci�n
	 * (por ejemplo caramelos envueltos */
	protected abstract boolean quedaPorDestruir();
	
	/** Destruye los elementos que queden pendientes por destruir, como aquellos caramelos
	 * que requieren dos fases de destrucci�n (envueltos)*/
	protected abstract void destruirPendientes();
	
	/**N�mero de filas*/
	protected int FILAS;
	/**N�mero de columnas*/
	protected int COLS;
	
}