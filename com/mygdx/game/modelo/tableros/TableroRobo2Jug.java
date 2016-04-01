package com.mygdx.game.modelo.tableros;

import java.util.Vector;

import com.mygdx.game.controlador.GameType;
import com.mygdx.game.controlador.StuffList;
import com.mygdx.game.controlador.StuffPile;
import com.mygdx.game.modelo.caramelos.Caramelo;
import com.mygdx.game.modelo.caramelos.Chucheria;
import com.mygdx.game.modelo.caramelos.Color;
import com.mygdx.game.modelo.caramelos.Ingrediente;

/**
 * Modalidad de dos jugadores. La mitad izquierda del tablero pertenece al jugador rojo,
 * y la mitad derecha al jugador azul
 *
 */
public class TableroRobo2Jug extends Tablero {
	
	/** Almacena una chuchería y sus coordenadas en el tablero para poder
	 * destruirla más tarde */
	protected class ChucheYcoordRobo2Jug implements ChucheYcoord{
		
		public ChucheYcoordRobo2Jug(Chucheria candy, int fila, int col){
			this.candy = candy;
			this.fila = fila;
			this.col = col;
		}
		
		@Override
		public void destruir(Tablero tablero) {
			
			try {
				if (isRedPlayersTurn) {
					while (candy != tablero.getElementAt(fila, col))
						col--;
					candy.destruir(tablero, fila, col);
				}
				else {
					while (candy != tablero.getElementAt(fila, col))
						col++;
					candy.destruir(tablero, fila, col);
				}
			}
			catch (ArrayIndexOutOfBoundsException ex) {}	
		}
		
		protected Chucheria candy;
		protected int fila;
		protected int col;
	}

	/**
	 * Crea un tablero básico con caramelos aleatorios, asegurando
	 * que no aparecen 3 de un mismo color ni en fila ni en columna
	 */
	public TableroRobo2Jug() {
		this.destruirMasTarde = new Vector<ChucheYcoord>();
		this.FILAS = 8;
		this.COLS= 12;
		this.restantesParaIngAzul = RESTANTES_PRIMERO;
		this.restantesParaIngRojo = RESTANTES_PRIMERO;
		this.ingAzulesEnTablero = 0;
		this.ingRojosEnTablero = 0;
		//this.ingEnTablero = new Vector<Chucheria>();
		this.puntosRojo = 0;
		this.puntosAzul = 0;

		this.isRedPlayersTurn = true;
		this.tablero = new Chucheria[FILAS][COLS];
		for (int i=0; i<FILAS; i++) {
			for (int j=0; j<COLS; j++) {
				do {
					tablero[i][j] = new Caramelo();
				}while (!valido(i,j));
			}
		}
	}
	
	@Override
	public boolean intercambiar(int fila1, int col1, int fila2, int col2, boolean intercambioLibre) {
		boolean intercambioExitoso = false;
		if (	intercambioLibre
				|| (fila1 == fila2 && Math.abs(col1-col2) == 1 )
				|| (col1 == col2 && Math.abs(fila1-fila2) == 1 )	) {
			
			if (isRedPlayersTurn && col1 < COLS/2 && col2 < COLS/2) {
				intercambioExitoso = super.intercambioGenerico(fila1, col1, fila2, col2);
			}
			else if (col1 >= COLS/2 && col2 >= COLS/2) {
				intercambioExitoso = super.intercambioGenerico(fila1, col1, fila2, col2);
			}
		}
		
		if (intercambioExitoso)
			this.isRedPlayersTurn = !this.isRedPlayersTurn; //Cambio de turno
		
		//XXX TEST Implentar puntos de manera grafica
		System.out.println("Score: R " + this.puntosRojo + ", A " + this.puntosAzul
				+ " OnBoard: R " + this.ingRojosEnTablero + " (" + this.restantesParaIngRojo + " left),"
				+ "A " + this.ingAzulesEnTablero + " (" + this.restantesParaIngAzul + " left),");
		if (isRedPlayersTurn)
			System.out.println("Red's turn!");
		else
			System.out.println("Blue's turn!");
		
		for (Observer o: obs) o.endOfInteraction();
		return intercambioExitoso;
	}

	@Override
	public boolean crear(Chucheria candy, int filaSpawn, int fila, int colSpawn, int col) {
		boolean creado = false;
		if (tablero[fila][col] == null) {
			tablero[fila][col] = candy;
			for (Observer o: obs) o.onCreateCandy(candy.getID(), filaSpawn, fila, colSpawn, col); //Avisamos de la creación
			creado = true;
		}
		return creado;
	}


	@Override
	public void introducir(Chucheria candy, int fila, int col, boolean animateTransform) {
		tablero[fila][col] = candy;
		if (animateTransform) for (Observer o: obs) o.onTransformCandy(candy.getID(), fila, col);
	}
	
	@Override
	public void efectoOndaExpansiva(int fila, int col) {}

	@Override
	public void destruir(int fila, int col) {
		if (tablero[fila][col] != null) {
			boolean debeDestruirse = tablero[fila][col].destruir(this, fila, col);
			if (debeDestruirse) {
				if (tablero[fila][col].getID() == StuffList.CEREZA_AZUL) {
					this.puntosAzul++;
					this.ingAzulesEnTablero--;
					//this.ingEnTablero.remove(tablero[fila][col]);
					if (this.ingAzulesEnTablero == 0 && this.restantesParaIngAzul > RESTANTES_PRIMERO)
						this.restantesParaIngAzul = RESTANTES_PRIMERO;
				}
				else if (tablero[fila][col].getID() == StuffList.CEREZA_ROJA) {
					this.puntosRojo++;
					this.ingRojosEnTablero--;
					//this.ingEnTablero.remove(tablero[fila][col]);
					if (this.ingRojosEnTablero == 0 && this.restantesParaIngRojo > RESTANTES_PRIMERO)
						this.restantesParaIngRojo = RESTANTES_PRIMERO;
				}
				this.suprimir(fila, col, false);
			}
			else
				this.addToDestruirMasTarde(fila, col);
		}
	}

	@Override
	public void suprimir(int fila, int col, boolean animateDestroy) {
		tablero[fila][col] = null;
		if (animateDestroy) for (Observer o: obs) o.onDestroyCandy(fila, col);
	}

	@Override
	public Chucheria getElementAt(int i, int j) throws ArrayIndexOutOfBoundsException {
		return tablero[i][j];
	}
	
	@Override
	public StuffPile getPileOfElementsAt(int fila, int col)
			throws ArrayIndexOutOfBoundsException {
		StuffList jelly;
		if (col<COLS/2) 
			jelly = StuffList.GELATINA_ROJA_2;
		else 
			jelly = StuffList.GELATINA_AZUL_2;
		return new StuffPile(tablero[fila][col].getID(), jelly);
	}
	
	@Override
	public GameType getGameType() {
		return GameType.STEAL_2P;
	}
		
	@Override
	protected void swap(int fila1, int col1, int fila2, int col2) {
		Chucheria aux = tablero[fila1][col1];
		tablero[fila1][col1] = tablero[fila2][col2];
		tablero[fila2][col2] = aux;	
		for (Observer o: obs) o.onSwapCandy(fila1, col1, fila2, col2);
	}

	@Override
	protected void rellenar() {
		if (this.isRedPlayersTurn) 
			rellenarCaidaHaciaIzda();
		else
			rellenarCaidaHaciaDcha();
	}
	

	@Override
	protected boolean combinarDeBarrido() {
		Vector<SegmentoFila> combinFila = sacarCombinFilas();
		Vector<SegmentoCol> combinCol = sacarCombinCols();
		
		boolean ingrDestruidos = buscarIngredientesParaDestruir();
		
		if (combinFila.isEmpty() && combinCol.isEmpty())
			return ingrDestruidos;
		else {
			destruir(combinFila, combinCol);
			crearEspecialesBarrido(combinFila, combinCol);
			return true;
		}
		
	}
	@Override
	protected boolean quedaPorDestruir() {
		return !this.destruirMasTarde.isEmpty();
	}

	@Override
	protected void destruirPendientes() {
		Vector<ChucheYcoord> destruirAhora = this.destruirMasTarde; //movemos la lista..
		this.destruirMasTarde = new Vector<ChucheYcoord>(); //...para crear una nueva donde se almacenen las nuevas destrucciones aplazadas..
		for (ChucheYcoord ch : destruirAhora)
			ch.destruir(this);  //... y destruimos lo que tengamos pendiente
	}

	/**
	 * Recorre el tablero destruyendo los ingredientes que estén en la
	 * zona de su mismo color
	 */
	private boolean buscarIngredientesParaDestruir() {
		boolean modificado = false;
		System.out.print("Iniciando busqueda de ingredientes... ");
		for (int i=0; i<FILAS; i++) { //Para cada fila
			for (int j=0; j<COLS/2; j++) {//para cada casilla de cada fila
				if (tablero[i][j].getID() == StuffList.CEREZA_ROJA) {
					modificado = true;
					destruir(i, j);
				}
			}
			for (int j=COLS/2; j<COLS; j++) {//para cada casilla de cada fila
				if (tablero[i][j].getID() == StuffList.CEREZA_AZUL) {
					modificado = true;
					destruir(i, j);
				}
			}
		}
		System.out.println("Busqueda finalizada!");
		return modificado;
	}

	/**
	 * Rellena el tablero, haciendo que los caramelos caigan (hacia la derecha)
	 * y posteriormente se creen caramelos nuevos para rellenar los huecos
	 */
	protected void rellenarCaidaHaciaDcha() {
		for (int i=0; i<FILAS; i++) {//De arriba a abajo
			int jExtr = COLS-1; //Avanzadilla (de donde extrae)
			int jRec = COLS-1; //Hueco (donde coloca)
			
			while (jExtr>=0) { //De dcha a izda hacemos caer
				if (tablero[i][jExtr] == null)
					jExtr--;
				else if (jRec != jExtr){
					tablero[i][jRec] = tablero[i][jExtr]; //Colocamos en el hueco
					for (Observer o :obs) o.onFallCandy(i, i, jExtr, jRec); //Avisamos de la caida
					tablero[i][jExtr] = null;
					jExtr--;
					jRec--;
				}
				else {
					jExtr--;
					jRec--;
				}
			}
			
			while (jRec>=0) { //Rellenamos sobrantes
				if (this.restantesParaIngAzul == 0) {
					crear(new Ingrediente(Color.AZUL), i, i, jExtr, jRec);
					//this.ingEnTablero.add(tablero[i][jRec]);
					this.ingAzulesEnTablero++;
					this.restantesParaIngAzul = RESTANTES_SIGUIENTES;
				}
				else {
					this.restantesParaIngAzul--;
					crear(new Caramelo(), i, i, jExtr, jRec);
				}
				jRec--;
				jExtr--;
			}
			
			
		}	
	}
	
	/**
	 * Rellena el tablero, haciendo que los caramelos caigan (hacia la izquierda)
	 * y posteriormente se creen caramelos nuevos para rellenar los huecos
	 */
	protected void rellenarCaidaHaciaIzda() {
		for (int i=0; i<FILAS; i++) {//De arriba a abajo
			int jExtr = 0; //Avanzadilla (de donde extrae)
			int jRec = 0; //Hueco (donde coloca)
			
			while (jExtr<COLS) { //De dcha a izda hacemos caer
				if (tablero[i][jExtr] == null)
					jExtr++;
				else if (jRec != jExtr){
					tablero[i][jRec] = tablero[i][jExtr]; //Colocamos en el hueco
					for (Observer o :obs) o.onFallCandy(i, i, jExtr, jRec); //Avisamos de la caida
					tablero[i][jExtr] = null;
					jExtr++;
					jRec++;
				}
				else {
					jExtr++;
					jRec++;
				}
			}
			
			while (jRec<COLS) { //Rellenamos sobrantes
				if (this.restantesParaIngRojo == 0) {
					crear(new Ingrediente(Color.ROJO), i, i, jExtr, jRec);
					//this.ingEnTablero.add(tablero[i][jRec]);
					this.ingRojosEnTablero++;
					this.restantesParaIngRojo = RESTANTES_SIGUIENTES;
				}
				else {
					this.restantesParaIngRojo--;
					crear(new Caramelo(), i, i, jExtr, jRec);
				}
				jRec++;
				jExtr++;
			}
			
			
		}	
	}
	
	protected void addToDestruirMasTarde(int fila, int col) {
		this.destruirMasTarde.addElement(new ChucheYcoordRobo2Jug(tablero[fila][col], fila, col));
	}
	
	/**Matriz de chucherías que representa el tablero, representado como (fila,columna)*/
	private Chucheria[][] tablero;
	
	/** Las chucherías con varias fases de destrucciónse registran aquí
	 *  para volver a destruirse cuando el tablero esté estable*/
	private Vector<ChucheYcoord> destruirMasTarde;
	
	/** Indica si el jugador rojo (izquierdo) está jugando */
	private boolean isRedPlayersTurn;
	/** Número de caramelos por destruir para que salga un ingrediente rojo */
	private int restantesParaIngRojo;
	/** Número de caramelos por destruir para que salga un ingrediente azul */
	private int restantesParaIngAzul;
	/** Número de ingredientes rojos en el tablero */
	private int ingRojosEnTablero;
	/** Número de ingredientes azules en el tablero */
	private int ingAzulesEnTablero;
	/** Puntos del jugador rojo */
	private int puntosRojo;
	/** Puntos del jugador azul */
	private int puntosAzul;
	/** Caramelos que es necesario destruir para que aparezca un
	 * ingrediente en el tablero cuando <b>NO</b> hay otro de ese color*/
	private static final int RESTANTES_PRIMERO = 6;
	/** Caramelos que son necesarios destruir para que aparezca un
	 * ingrediente en el tablero cuando <b>YA</b> hay otro de ese color*/
	private static final int RESTANTES_SIGUIENTES = 60;
	

}
