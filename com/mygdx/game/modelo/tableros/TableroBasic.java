package com.mygdx.game.modelo.tableros;

import java.util.Vector;

import com.mygdx.game.controlador.GameType;
import com.mygdx.game.modelo.caramelos.BombaColor;
import com.mygdx.game.modelo.caramelos.Caramelo;
import com.mygdx.game.modelo.caramelos.Chucheria;
import com.mygdx.game.modelo.caramelos.Color;
import com.mygdx.game.modelo.caramelos.Envuelto;
import com.mygdx.game.modelo.caramelos.Rallado;


public class TableroBasic extends Tablero {

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
	
	/** Almacena una chuchería y sus coordenadas en el tablero para poder
	 * destruirla más tarde */
	protected class ChucheYcoordBasic implements ChucheYcoord {
		
		public ChucheYcoordBasic(Chucheria candy, int fila, int col){
			this.candy = candy;
			this.fila = fila;
			this.col = col;
		}
		
		@Override
		public void destruir(Tablero tablero) {
			try {
				while (candy != tablero.getElementAt(fila, col))
					fila++;
				candy.destruir(tablero, fila, col);
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
	public TableroBasic() {
		this.destruirMasTarde = new Vector<ChucheYcoord>();
		this.FILAS = 8;
		this.COLS = 8;
		//this.addObserver(this.destruirMasTarde);
		
		this.tablero = new Chucheria[FILAS][COLS];
		for (int i=0; i<FILAS; i++) {
			for (int j=0; j<COLS; j++) {
				do {
					tablero[i][j] = new Caramelo();
				}while (!valido(i,j));
			}
		}
	}
	
	
/*	@Override
	public boolean intercambiar(int fila1, int col1, int fila2, int col2) {
		//System.out.println("--> NEW SWAP <--"); //XXX TEST Mostrar combinaciones
		
		swap(fila1, col1, fila2, col2);
		for (Observer o: obs) o.endOfInteractionGroup();
		
		//Llamar efectos especiales
		boolean hay_especial = tablero[fila1][col1].efectoIntercambio(
				this, fila1, col1, fila2, col2, true); //Efecto intercambio 1er caramelo
		if (!hay_especial)
			hay_especial = tablero[fila2][col2].efectoIntercambio(
					this, fila2, col2, fila1, col1, false); //Efecto intercambio 2o caramelo
		
		//Combinar (si no ha habido efecto especial)
		boolean comb1 = false; 
		boolean comb2 = false;
		if (!hay_especial) {
			if(tablero[fila1][col1] != null) 
				comb1 =  tablero[fila1][col1].combinarDeIntercambio(this, fila1, col1);
			if(tablero[fila2][col2] != null) 
				comb2 =  tablero[fila2][col2].combinarDeIntercambio(this, fila2, col2);
		}
		
		for (Observer o: obs) o.endOfInteractionGroup();
		
		//Rellenar (si ha habido efecto o combinacion) o restablecer (en caso contrario)
		if (hay_especial || comb1 || comb2) {
			boolean inestable = true;
			while (inestable) {
				rellenar();
				for (Observer o: obs) o.endOfInteractionGroup();
				inestable = combinarDeBarrido(); //Cierto si sale alguna combinacion
				for (Observer o: obs) o.endOfInteractionGroup();
			}
			//Destruir envueltos y rellenar
			inestable = !this.destruirMasTarde.isEmpty();
			while (inestable) {
				Vector<ChucheYcoord> destruirAhora = this.destruirMasTarde; //movemos la lista..
				this.destruirMasTarde = new Vector<ChucheYcoord>(); //...para crear una nueva donde se almacenen las nuevas destrucciones aplazadas..
				for (ChucheYcoord ch : destruirAhora)
					ch.destruir(this);  //... y destruimos lo que tengamos pendiente
				for (Observer o: obs) o.endOfInteractionGroup();
				
				while (inestable) {
					rellenar();
					for (Observer o: obs) o.endOfInteractionGroup();
					inestable = combinarDeBarrido(); //Cierto si sale alguna combinacion
					for (Observer o: obs) o.endOfInteractionGroup();
				}
				inestable = !this.destruirMasTarde.isEmpty();
			}
	
		}
		else {
			swap(fila1, col1, fila2, col2);
			for (Observer o: obs) o.endOfInteractionGroup();
		}
			
		return hay_especial || comb1 || comb2;
	}
	*/

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
	public void introducir(Chucheria candy, int fila, int col) {
		tablero[fila][col] = candy;
	}


	@Override
	public void destruir(int fila, int col) { //TODO Posiblemente añadir parámetros caramelo destruido y forma de destruccion (normal, poer rallado, por envuelto, por bomba de color...)
		if (tablero[fila][col] != null) {
			boolean debeDestruirse = tablero[fila][col].destruir(this, fila, col);
			if (debeDestruirse) {
				this.suprimir(fila, col);
				for (Observer o: obs) o.onDestroyCandy(fila, col);
			}
			else
				this.addToDestruirMasTarde(fila, col);
		}
	}
	
	@Override
	public void suprimir(int fila, int col) {
		if (tablero[fila][col] != null) {
			tablero[fila][col] = null;
			
		}
	}

	@Override
	public Chucheria getElementAt(int i, int j) throws ArrayIndexOutOfBoundsException {
		return tablero[i][j];
	}
	
	@Override
	public GameType getGameType() {
		return GameType.BASIC;
	}

	protected void addToDestruirMasTarde(int fila, int col) {
		this.destruirMasTarde.addElement(new ChucheYcoordBasic(tablero[fila][col], fila, col));
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
		for (int j=0; j<COLS; j++) {//De izqda a dcha
			int iExtr = FILAS-1; //Avanzadilla (de donde extrae)
			int iRec = FILAS-1; //Hueco (donde coloca)
			
			while (iExtr>=0) { //De abajo a arriba hacemos caer
				if (tablero[iExtr][j] == null)
					iExtr--;
				else if (iRec != iExtr){
					tablero[iRec][j] = tablero[iExtr][j]; //Colocamos en el hueco
					for (Observer o :obs) o.onFallCandy(iExtr, iRec, j, j); //Avisamos de la caida
					tablero[iExtr][j] = null;
					iExtr--;
					iRec--;
				}
				else {
					iExtr--;
					iRec--;
				}
			}
			
			while (iRec>=0) { //Rellenamos sobrantes
				crear(new Caramelo(), iExtr, iRec, j, j);
				iRec--;
				iExtr--;
			}
			
			
		}	
	}


	@Override
	protected boolean combinarDeBarrido() {
		Vector<SegmentoFila> combinFila = sacarCombinFilas();
		Vector<SegmentoCol> combinCol = sacarCombinCols();
		
		if (combinFila.isEmpty() && combinCol.isEmpty())
			return false;
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
				if (color == tablero[i][j].getColor()) //Si coincide color...
					cont++;								//...hay otro más del mismo color seguido
				
				else {									//Si no coincide color...
					if (cont >= 3 && color != Color.NINGUNO){ //Si ha habido tres o más de un color seguidos...
						combinFila.add(new SegmentoFila(tablero[i][j-1].getColor(), i, j-cont, j-1)); //...marcamos para destruir
					}
					color = tablero[i][j].getColor();	//Ademas, actualizamos el color...
					cont = 1;							//... y el contador
				}
			}
			if (cont >= 3 && color != Color.NINGUNO){ //Si ha habido tres o más de un color seguidos...
				combinFila.add(new SegmentoFila(tablero[i][COLS-1].getColor(), i, COLS-cont, COLS-1)); //...marcamos para destruir
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
				if (color == tablero[i][j].getColor()) //Si coincide color...
					cont++;								//...hay otro más del mismo color seguido
				
				else {									//Si no coincide color...
					if (cont >= 3 && color != Color.NINGUNO) //Si ha habido tres o más de un color seguidos...
						combinCol.add(new SegmentoCol(tablero[i-1][j].getColor(), j, i-cont, i-1)); //...marcamos para destruir
					color = tablero[i][j].getColor();	//Ademas, actualizamos el color...
					cont = 1;							//... y el contador
				}
			}
			if (cont >= 3 && color != Color.NINGUNO) //Si ha habido tres o más de un color seguidos...
				combinCol.add(new SegmentoCol(tablero[FILAS-1][j].getColor(), j, FILAS-cont, FILAS-1)); //...marcamos para destruir
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
		
		//XXX TEST Mostrar combinaciones
/*		System.out.print("F> ");
		for (SegmentoFila seg: combinFila)
			System.out.print("(" + seg.getFila() + "," + seg.getColIni() + "-" + seg.getColFin() + ") ");
		System.out.println();
		System.out.print("C> ");
		for (SegmentoCol seg: combinCol)
			System.out.print("(" + seg.getFilaIni() + "-" + seg.getFilaFin() + "," + seg.getCol() + ") ");
	*/	System.out.println();
		
		//Creación de caramelos envueltos
		for (SegmentoFila segFila : combinFila) 
			for(SegmentoCol segCol : combinCol) {			
				if (	segCol.getFilaIni() <= segFila.getFila() && segFila.getFila() <= segCol.getFilaFin()
					 && segFila.getColIni() <= segCol.getCol() && segCol.getCol() <= segFila.getColFin() ) {
					//Los segmentos se cortan -> Crear envuelto
					boolean creado = this.crear(new Envuelto(segCol.getColor()), segFila.getFila(), segFila.getFila(), segCol.getCol(), segCol.getCol());
					if (!creado) this.crearEnCol(this, new Envuelto(segCol.getColor()), segCol.getCol(), segCol.getFilaFin(), segCol.getFilaFin());
					//System.out.println("Envuelto creado"); //XXX TEST Mostrar combinaciones
				}
			}
		
		//Creación de rallados H y bombas de color (combinaciones en columna)
		for(SegmentoCol segCol : combinCol) {
			if (segCol.getFilaFin() - segCol.getFilaIni() == 3)	{
				//El segmento vertical tiene longitud 4 -> Crear rallado H
				this.crearEnCol(this, new Rallado(segCol.getColor(), true), segCol.getCol(), segCol.getFilaIni(), segCol.getFilaFin());
				//System.out.println("Rallado H creado"); //XXX TEST Mostrar combinaciones
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
				//System.out.println("Rallado V creado"); //XXX TEST Mostrar combinaciones
			}
			
			else if (segFila.getColFin() - segFila.getColIni() >= 4)	{
				int numBombas = segFila.getColFin() - segFila.getColIni() - 3;
				for (int i=0; i<numBombas; i++)
					this.crearEnCol(this, new BombaColor(), segFila.getFila(), segFila.getColIni(), segFila.getColFin());
			}
		}
		
		//System.out.println(); //XXX TEST Mostrar combinaciones
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
		if (( i>=2 && tablero[i][j].equals(tablero[i-1][j]) && tablero[i][j].equals(tablero[i-2][j])) //Si coincide con los dos de arriba...
			|| ( j>=2 && tablero[i][j].equals(tablero[i][j-1]) && tablero[i][j].equals(tablero[i][j-2])))//...o con los dos de la izquierda...
			ok = false; //... no es valido
		return ok;
	}
	
	/**Matriz de chucherías que representa el tablero, representado como (fila,columna)*/
	protected Chucheria[][] tablero;
	
	/** Las chucherías con varias fases de destrucciónse registran aquí
	 *  para volver a destruirse cuando el tablero esté estable*/
	protected Vector<ChucheYcoord> destruirMasTarde;
	
}
