package com.mygdx.game.modelo.tableros;

import java.util.Vector;

import com.mygdx.game.controlador.GameType;
import com.mygdx.game.controlador.Observable;
import com.mygdx.game.controlador.StuffList;
import com.mygdx.game.controlador.StuffPile;
import com.mygdx.game.modelo.caramelos.BombaColor;
import com.mygdx.game.modelo.caramelos.Chucheria;
import com.mygdx.game.modelo.caramelos.Color;
import com.mygdx.game.modelo.caramelos.Envuelto;
import com.mygdx.game.modelo.caramelos.Rallado;
/**
 * Represnta el tablero donde se encuentran todos los elementos del juego con los que se 
 * puede interactuar de alguna manera o que pueden afectar a otros elementos interactuables. Se excluyen del 
 * tablero puntuaciones, decoraciones y otros elementos.
 */
public abstract class Tablero extends Observable<Tablero.Observer>{
	
	/** Inerfaz con las acciones relevantes que realiza el tablero.
	 * Cualquier vista que actualice el tablero debe implementarla,
	 * además de registrarse como observador en la clase Tablero.
	 */
	public static interface Observer {
		
		/** Función que debe ser llamada al acabr el intercambio */
		void endOfInteraction();
		
		/**Se han intercambiado dos caramelos expresados por las coordenadas
		 * (fila1, col1) y (fila2, col2). Sólo es necesario llamarlo una vez
		 * por intercambio. No importa en qué orden se identifiquen los caramelos. */
		void onSwapCandy(int fila1, int col1, int fila2, int col2);
		
		/**Ha caido un caramelo de la posición (filaIni, colIni) a
		 * la posición (filaFin, colFin) <br> 
		 * <b> NOTA: </b> Permite caídas hacia izquierda, derecha y, por supuesto, abajo
		 */
		void onFallCandy(int filaIni, int filaFin, int colIni, int colFin);
		
		/**Se ha creado un caramelo en la posición (fila, col).
		 * En caso de que caiga desde arriba, se usa el parámetro filaSpawn para indicarlo.
		 * @param candy ID del caramelo creado
		 * @param filaSpawn fila desde donde cae. Puede ser negativa. En caso de que no caiga
		 * desde arriba, tiene el mismo valor que el parámetro <b>fila</b>
		 * @param fila fila donde ha aparecido (posición final)
		 * @param colSpawn columna desde donde cae (para cuando la caida es lateral).
		 *  Puede ser negativa o mayor que el número de colmnas. En caso de que no caiga
		 * desde un lado, tiene el mismo valor que el parámetro <b>col</b>
		 * @param col columna donde ha aparecido (posición final) */
		void onCreateCandy(StuffList candy, int filaSpawn, int fila, int colSpawn, int col);
		
		/** Se ha destruido el caramelo de la posición (fila, col) */
		void onDestroyCandy(int fila, int col);
		
		/** Se ha destruido el caramelo de la posición (fila, col) 
		 * @param newJelly nueva gelatina en la posición*/
		void onDestroyJelly(int fila, int col, StuffList newJelly);
		
		/** Se ha transformado el caramelo de la posición (fila, col) en uno de otro tipo */
		void onTransformCandy(StuffList candy, int fila, int col);
	}
	
	/** Almacena una chuchería y sus coordenadas en el tablero para poder
	 * destruirla más tarde */
	protected interface ChucheYcoord {
		
		/** Destruye todas aquellas chucherías que quedaron pendientes. Dado que la 
		 * chuchería puede haber caído desde que se inicializó, antes de llamar a 
		 * la función destruir de la chuchería se recorre la columna
		 * hacia abajo hasta encontrarla
		 * @param tablero Tablero en el que se encuentra la chuchería
		 */
		public void destruir(Tablero tablero);

	}
	
	/** Indica la localización de una combinación en fila, 
	 * expresada por la fila en la que se encuentra, ademas
	 * de las columnas de inicio y fin de la combinación.
	 * Dichas columnas se encuentran incluidas por ambos
	 * extremos
	 */
	protected static class SegmentoFila {
		
		public SegmentoFila(Color color, int fila, int colIni, int colFin) {
			this.color = color;
			this.fila = fila;
			this.colIni = colIni;
			this.colFin = colFin;
		}
		
		public Color getColor() {return color;}	
		public int getFila() {return fila;}	
		public int getColIni() {return colIni;}	
		public int getColFin() {return colFin;}
		
		protected Color color;
		protected int fila;
		protected int colIni;
		protected int colFin;
	}
	
	/** Indica la localización de una combinación en columna, 
	 * expresada por la columna en la que se encuentra, ademas
	 * de las filas de inicio y fin de la combinación.
	 * Dichas filas se encuentran incluidas por ambos
	 * extremos
	 */
	protected static class SegmentoCol {
		
		public SegmentoCol(Color color, int col, int filaIni, int filaFin) {
			this.color = color;
			this.col = col;
			this.filaIni = filaIni;
			this.filaFin = filaFin;
		}
		
		public Color getColor() {return color;}	
		public int getCol() {return col;}	
		public int getFilaIni() {return filaIni;}	
		public int getFilaFin() {return filaFin;}
		
		protected Color color;
		protected int col;
		protected int filaIni;
		protected int filaFin;
	}
	
	/**
	 * Intercambia dos casillas cualesquiera contiguas. Puede hacer uso del algoritmo de
	 * intercambio generico implementado en esta clase. Al final debe realizarse un
	 * "<code>for (Observer o: obs) o.endOfInteraction();</code>" <br><br>
	 * 
	 * <b>NOTA:</b> En caso de que el intercambio sea fallido, se restablecen las posiciones 
	 * de los caramelos intercambiados. <br>
	 * <b>NOTA:</b> No comprueba que los parametros se encuentren dentro de los límites.<br>
	 * 
	 * @param fila1 Fila del primer caramelo a intercambiar
	 * @param col1 Columna del primer caramelo a intercambiar
	 * @param fila2 Fila del segundo caramelo a intercambiar
	 * @param col2 Columna del segundo caramelo a intercambiar
	 * @param intercambioLibre si es cierto, permite intercambiar casillas cualesquiera
	 * @return True si el intecambio tiene éxito. False si es fallido.
	 * @see Tablero#intercambioGenerico(int, int, int, int)
	 */
	public abstract boolean intercambiar(int fila1, int col1, int fila2, int col2, boolean intercambioLibre);

	/**
	 * Crea un caramelo en una casilla. La casilla debe estar vacía para
	 *  que la creación sea efectiva. Es la función que se usará por defecto.
	 *  <b>NOTA:</b> No comprueba que los parametros se encuentren dentro de los límites. En
	 * cambio, sí comprueba que la casilla esté vacía.
	 * @param candy ID de la chuchería
	 * @param filaSpawn Fila donde aparece (en caso de que caiga desde arriba).
	 * En Caso contrario filaSpawn = fila
	 * @param fila Fila de la casilla
	 * @param colSpawn Columna donde aparece (en caso de que caiga desde un lado).
	 * En Caso contrario colSpawn = col
	 * @param col Columna de la casilla
	 * @return Devuelve si se ha podido hacer la creación
	 */
	public abstract boolean crear(Chucheria candy, int filaSpawn, int fila, int colSpawn, int col);
	
	/**
	 * Introduce un caramelo en la casilla descrita. Puede ser de utilidad para los efectos de intercambio. <br><br>
	 * <b>NO</b> llama a la función crear del Obsever. Sin embargo, llama a la función transformar del
	 * observer si se explicita <br>
	 * <b>NOTA:</b> No comprueba que los parametros se encuentren dentro de los límites, ni que la casilla
	 * esté vacía
	 * @param candy
	 * @param fila Fila de la casilla
	 * @param fila
	 * @param col
	 * @param animateTransform Si es cierto, se llama al la funcion {@link Observer#onTransformCandy(StuffList, int, int)}
	 */
	public abstract void introducir(Chucheria candy, int fila, int col, boolean animateTransform);

	/**
	 * Destruye una casilla. Dependiendo de lo que contenga la casilla, puede tener distintos efectos,
	 * ya que puede implicar la destrucción de otros caramelos, actualización de puntos, etc. 
	 * Llama a la función destruir de la chuchería antes de eliminarla del tablero <br> 
	 * Es la función que se usará por defecto. <br> <br>
	 * <b>NOTA:</b> No comprueba que los parametros se encuentren dentro de los límites. En
	 * cambio, sí comprueba que el elemento a destruir no sea nulo
	 * @param fila Fila de la casilla
	 * @param col Columna de la casilla
	 */
	/*TODO Posiblemente añadir parámetros caramelo destruido y forma de destruccion
	 *  (normal, poer rallado, por envuelto, por bomba de color...)
	 */
	public abstract void destruir(int fila, int col);
	
	/**
	 * Indica que al lado de la casilla se ha destruido una chuchería.
	 * Dependiendo de lo que contenga la casilla, puede tener distintos efectos (o ninguno),
	 * ya que puede implicar la destrucción de otros caramelos, gelatinas o coberturas. 
	 * Llama a la función efectoIntercambio() de la chuchería <br> <br>
	 * <b>NOTA:</b> No comprueba que los parametros se encuentren dentro de los límites. En
	 * cambio, sí comprueba que el elemento a destruir no sea nulo
	 * @param fila Fila de la casilla
	 * @param col Columna de la casilla
	 */
	public abstract void efectoOndaExpansiva(int fila, int col);

	/**
	 * Destruye una casilla, sin tener encuenta los efectos laterales que pueda tenga la chuchería. 
	 * <b>NO</b> llama a la función destruir de la chuchería. Sin embargo, llama a la función destruir del
	 * observer si se explicita <br>
	 * Puede ser de utilidad para los efectos de intercambio. <br><br>
	 * <b>NOTA:</b> No comprueba que los parametros se encuentren dentro de los límites. En
	 * cambio, sí comprueba que el elemento a destruir no sea nulo
	 * @param fila Fila de la casilla
	 * @param col Columna de la casilla
	 * @param realDestroy Si es cierto, indica que la destrucción es real, no es una extracción temporal
	 * 		necesario en un algoritmo. Por tanto, si <code>realDestroy == true</code> se llama 
	 * 		al la funcion {@link Observer#onDestroyCandy(int, int)}, y
	 * 		también se producen otros efectos como destrucción de gelatina.
	 */
	public abstract void suprimir(int fila, int col, boolean realDestroy);

	/** @return Numero de filas del tablero */
	public int getRows() {
		return FILAS;
	}

	/** @return Numero de columnas del tablero */
	public int getColumns() {
		return COLS;
	}

	/**
	 * Devuelve la chuchería en la posicion especificada. <br><br>
	 * <b>NOTA:</b> No comprueba que los parametros se encuentren dentro de los límites
	 */
	public abstract Chucheria getElementAt(int fila, int col) throws ArrayIndexOutOfBoundsException;
	
	/**
	 * Devuelve los IDs de todos los elementos en la posicion del tablero especificada. <br><br>
	 * <b>NOTA:</b> No comprueba que los parametros se encuentren dentro de los límites
	 */
	public abstract StuffPile getPileOfElementsAt(int fila, int col) throws ArrayIndexOutOfBoundsException;
	
	/**
	 * Devuelve el tipo de juego que representa el tablero
	 * @return tipo de juego
	 */
	public abstract GameType getGameType();
	
	/**
	 * Intercambia dos chucherías, dadas por sus coordenadas en el tablero.
	 *  También llama al método observador onSwap(...)
	 * @param fila1 fila de la primera
	 * @param col1 columna de la primera
	 * @param fila2 fila de la segunda
	 * @param col2 columna de la segunda
	 */
	protected abstract void swap(int fila1, int col1, int fila2, int col2);
	
	/**
	 * Rellena el tablero, haciendo que los caramelos caigan (hacia alguna dirección)
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
	 * Permite preguntar si quedan elementos por destruir cuando el tablero está estable.
	 * útil cuando hay caramelos con varias fases de destrucción
	 * @return true si hay elementos que noecesitan otra fase de destrucción
	 * (por ejemplo caramelos envueltos */
	protected abstract boolean quedaPorDestruir();
	
	/** Destruye los elementos que queden pendientes por destruir, como aquellos caramelos
	 * que requieren dos fases de destrucción (envueltos)*/
	protected abstract void destruirPendientes();
	
	/**
	 * Intercambia dos casillas cualesquiera (no necesariamente contiguas) y:
	 * <ol>
	 * <li> En caso de que sean caramelos especiales y el intercambio produzca algun efecto, 
	 * se ejecuta y se destruyen los caramelos pertinentes.
	 * <li> En caso contrario, se intenta buscar combinaciones de 3 o más. Si la búsqueda 
	 * tiene éxito, se destruyen los caramelos pertinentes y se crean caramelos especiales
	 * en caso necesario
	 * <li> En caso de que haya ocurrido algo de lo anterior (y por tanto, se hayan destruido 
	 * caramelos) se rellena el tablero de forma pertinente. En caso de que se generen más 
	 * combinaciones, se repiten los pasos 2 y 3.
	 * </ol>
	 * 
	 * <b>NOTA:</b> Si el usuario de esta clase quiere que las casillas que se intercambian sean 
	 * contiguas, debe comprobarlo antes de realizar la llamada a esta función. <br>
	 * <b>NOTA:</b> En caso de que el intercambio sea fallido, se restablecen las posiciones 
	 * de los caramelos intercambiados. <br>
	 * <b>NOTA:</b> No comprueba que los parametros se encuentren dentro de los límites.<br>
	 * 
	 * @param fila1 Fila del primer caramelo a intercambiar
	 * @param col1 Columna del primer caramelo a intercambiar
	 * @param fila2 Fila del segundo caramelo a intercambiar
	 * @param col2 Columna del segundo caramelo a intercambiar
	 * @return True si el intecambio tiene éxito. False si es fallido.
	 */
	protected boolean intercambioGenerico(int fila1, int col1, int fila2, int col2) { //Modificar intercambiar para comprobar casillas contiguas
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
		
		return hay_especial || comb1 || comb2;
	}

	/**
	 * Condición usada en el constructor. Determina, al generar el tablero,
	 * si al añadir un caramelo se crea combinación con alguno ya existente
	 * (arriba y a la izquierda)
	 * @param i fila del caramelo
	 * @param j columna del caramelo
	 * @return <b>false</b> si tiene el mismo color que los dos inmediatamente a su izquierda
	 * o que las dos inmediatamente encima. <br>
	 * <b>true</b> en caso contrario
	 */
	protected boolean valido(int i,int j) {
		boolean ok = true;
		if (( i>=2 && this.getElementAt(i,j).equals(this.getElementAt(i-1,j)) && this.getElementAt(i,j).equals(this.getElementAt(i-2,j))) //Si coincide con los dos de arriba...
			|| ( j>=2 && this.getElementAt(i,j).equals(this.getElementAt(i,j-1)) && this.getElementAt(i,j).equals(this.getElementAt(i,j-2))))//...o con los dos de la izquierda...
			ok = false; //... no es valido
		return ok;
	}
	
	/**
	 * Recorre el tablero en busca de combinaciones EN HORIZONTAL
	 * @return Vector de combinaciones de filas, cada una recogida por la clase SegmentoFila
	 * @see SegmentoFila
	 */
	protected Vector<SegmentoFila> sacarCombinFilas() {
		int cont;
		Color color;
		Vector<SegmentoFila> combinFila = new Vector<SegmentoFila>();
		for (int i=0; i<FILAS; i++) {
			color = Color.NINGUNO;	//Preparamos la fila
			cont = 0;
			for (int j=0; j<COLS; j++) {	//Recorremos la fila
				if (color == this.getElementAt(i,j).getColor()) //Si coincide color...
					cont++;								//...hay otro más del mismo color seguido
				
				else {									//Si no coincide color...
					if (cont >= 3 && color != Color.NINGUNO){ //Si ha habido tres o más de un color seguidos...
						combinFila.add(new SegmentoFila(this.getElementAt(i,j-1).getColor(), i, j-cont, j-1)); //...marcamos para destruir
					}
					color = this.getElementAt(i,j).getColor();	//Ademas, actualizamos el color...
					cont = 1;							//... y el contador
				}
			}
			if (cont >= 3 && color != Color.NINGUNO){ //Si ha habido tres o más de un color seguidos...
				combinFila.add(new SegmentoFila(this.getElementAt(i,COLS-1).getColor(), i, COLS-cont, COLS-1)); //...marcamos para destruir
			}
		}
		
		return combinFila;
	}


	/**
	 * Recorre el tablero en busca de combinaciones EN VERTICAL
	 * @return Vector de combinaciones de columnas, cada una recogida por la clase SegmentoCol
	 * @see SegmentoCol
	 */
	protected Vector<SegmentoCol> sacarCombinCols() {
		int cont;
		Color color;
		Vector<SegmentoCol> combinCol = new Vector<SegmentoCol>();
		for (int j=0; j<COLS; j++){
			color = Color.NINGUNO;	//Preparamos la columna
			cont = 0;
			for (int i=0; i<FILAS; i++) {	//Recorremos la columa
				if (color == this.getElementAt(i,j).getColor()) //Si coincide color...
					cont++;								//...hay otro más del mismo color seguido
				
				else {									//Si no coincide color...
					if (cont >= 3 && color != Color.NINGUNO) //Si ha habido tres o más de un color seguidos...
						combinCol.add(new SegmentoCol(this.getElementAt(i-1,j).getColor(), j, i-cont, i-1)); //...marcamos para destruir
					color = this.getElementAt(i,j).getColor();	//Ademas, actualizamos el color...
					cont = 1;							//... y el contador
				}
			}
			if (cont >= 3 && color != Color.NINGUNO) //Si ha habido tres o más de un color seguidos...
				combinCol.add(new SegmentoCol(this.getElementAt(FILAS-1,j).getColor(), j, FILAS-cont, FILAS-1)); //...marcamos para destruir
		}
		
		return combinCol;
	}


	/**
	 * Tras haber encontrado todas las combinaciones de filas y columnas en el tablero,
	 * las destruye. Tambien tienen efecto los efectos laterales de la destrucción de
	 * las chucherías implicadas
	 * @param combinFila Vector de combinaciones de filas, cada una recogida por la clase SegmentoFila
	 * @param combinCol Vector de combinaciones de columnas, cada una recogida por la clase SegmentoCol
	 * @see SegmentoCol
	 * @see SegmentoFila
	 * @see sacarCombinFilas()
	 * @see sacarCombinCols()
	 */
	protected void destruir(Vector<SegmentoFila> combinFila, Vector<SegmentoCol> combinCol) {
		//Destrucciones horizontales
		for (SegmentoFila seg: combinFila)
			for (int i=seg.getColIni(); i<= seg.getColFin(); i++)
				destruir(seg.getFila(), i);
		
		//Destrucciones verticalse
		for (SegmentoCol seg: combinCol)
			for (int i=seg.getFilaIni(); i<= seg.getFilaFin(); i++)
				destruir(i, seg.getCol());
	}

	/**
	 * Crea un caramelo del tipo especificado en el tablero dentro del hueco
	 *  que ha dejado la combinación.
	 * @param tablero Tablero en cuestión
	 * @param candy Caramelo a crear
	 * @param fila Fila donde se debe crear
	 * @param colIni Límite izquierdo para crearlo
	 * @param colFin Límite derecho para crearlo
	 */
	protected void crearEnFila(Tablero tablero, Chucheria candy, int fila, int colIni, int colFin) {	
		boolean creado = false;
		int col=colIni;
		while (!creado && col<=colFin) {
			creado = tablero.crear(candy, fila, fila, col, col);
			col++;
		}
	}
	
	/**
	 * Crea un caramelo del tipo especificado en el tablero dentro del hueco
	 *  que ha dejado la combinación.
	 * @param tablero Tablero en cuestión
	 * @param candy Caramelo a crear
	 * @param col Columna donde se debe crear
	 * @param filaIni Límite izquierdo para crearlo
	 * @param filaFin Límite derecho para crearlo
	 */
	protected void crearEnCol(Tablero tablero, Chucheria candy, int col, int filaIni, int filaFin) {
		boolean creado = false;
		int fila=filaIni;
		while (!creado && fila<=filaFin) {
			creado = tablero.crear(candy, fila, fila, col, col);
			fila++;
		}
	}
	
	/**
	 * Tras haber encontrado todas las combinaciones de filas y columnas en el tablero,
	 * y tras haberlas destruido, se crean los caramelos especiales.
	 * @param combinFila Vector de combinaciones de filas, cada una recogida por la clase SegmentoFila
	 * @param combinCol Vector de combinaciones de columnas, cada una recogida por la clase SegmentoCol
	 * @see SegmentoCol
	 * @see SegmentoFila
	 * @see sacarCombinFilas()
	 * @see sacarCombinCols()
	 */
	protected void crearEspecialesBarrido(Vector<SegmentoFila> combinFila, Vector<SegmentoCol> combinCol) {
				
		//Creación de caramelos envueltos
		for (SegmentoFila segFila : combinFila) 
			for(SegmentoCol segCol : combinCol) {			
				if (	segCol.getFilaIni() <= segFila.getFila() && segFila.getFila() <= segCol.getFilaFin()
					 && segFila.getColIni() <= segCol.getCol() && segCol.getCol() <= segFila.getColFin() ) {
					//Los segmentos se cortan -> Crear envuelto
					boolean creado = this.crear(new Envuelto(segCol.getColor()), segFila.getFila(), segFila.getFila(), segCol.getCol(), segCol.getCol());
					if (!creado) this.crearEnCol(this, new Envuelto(segCol.getColor()), segCol.getCol(), segCol.getFilaFin(), segCol.getFilaFin());
				}
			}
		
		//Creación de rallados H y bombas de color (combinaciones en columna)
		for(SegmentoCol segCol : combinCol) {
			if (segCol.getFilaFin() - segCol.getFilaIni() == 3)	{
				//El segmento vertical tiene longitud 4 -> Crear rallado H
				this.crearEnCol(this, new Rallado(segCol.getColor(), true), segCol.getCol(), segCol.getFilaIni(), segCol.getFilaFin());
			}
			else if (segCol.getFilaFin() - segCol.getFilaIni() >= 4)	{
				int numBombas = segCol.getFilaFin() - segCol.getFilaIni() - 3;
				for (int i=0; i<numBombas; i++)
					this.crearEnCol(this, new BombaColor(), segCol.getCol(), segCol.getFilaIni(), segCol.getFilaFin());
			}
		}
		
		//Creación de rallados V y bombas de color (combinaciones en fila)
		for (SegmentoFila segFila : combinFila) {
			if (segFila.getColFin() - segFila.getColIni() == 3)	{
				//El segmento horizontal tiene longitud 4 -> Crear rallado V
				this.crearEnFila(this, new Rallado(segFila.getColor(), false), segFila.getFila(), segFila.getColIni(), segFila.getColFin());
			}
			
			else if (segFila.getColFin() - segFila.getColIni() >= 4)	{
				int numBombas = segFila.getColFin() - segFila.getColIni() - 3;
				for (int i=0; i<numBombas; i++)
					this.crearEnCol(this, new BombaColor(), segFila.getFila(), segFila.getColIni(), segFila.getColFin());
			}
		}
	}


	/**Número de filas*/
	protected int FILAS;
	/**Número de columnas*/
	protected int COLS;
	
}