package com.icecubelab.elementcrash.vista;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.icecubelab.elementcrash.controlador.Controlador;
import com.icecubelab.elementcrash.modelo.tableros.*;

public class MyGdxGame implements ApplicationListener {
	
	
	@Override
	public void create () {
		Assets.loadCandies();
		
		batch = new SpriteBatch();
		//this.tablero = new TableroJellyCoverBasic(4, 5, 3);
		this.tablero = new TableroJelly2Jug();
		//this.tablero = new TableroRobo2Jug();

		this.filaSelec = -1;
		this.colSelec = -1;
		this.controlador = new Controlador(tablero, this);
					
	}

	@Override
	public void resize(int width, int height) {
		MD.rearrange(width, height, tablero.getRows(), tablero.getColumns(), tablero.getGameType());
		if (this.boardAnim != null)
			this.tablero.removeObserver(this.boardAnim);
		this.boardAnim = new BoardAnimation(this.controlador);
		this.tablero.addObserver(this.boardAnim);
		createTouchPoints();
		/* TODO pausa del renderizado
		Gdx.graphics.setContinuousRendering(false);
		Gdx.graphics.requestRendering();
		*/
	}

	/** Crea las zonas clicables de la pantalla */
	private void createTouchPoints() {
			
		this.cuadrTablero = new BoundingBox[controlador.getRows()][controlador.getColumns()];
		for (int j=0; j<controlador.getRows(); j++) //Para cada fila
			for (int i=0; i<controlador.getColumns(); i++) //para cada casilla de cada fila
				this.cuadrTablero[j][i] = new BoundingBox(
						new Vector3(MD.originX_BB()+i*MD.dim(),
									MD.originY_BB()+j*MD.dim(), 0),
						new Vector3(MD.originX_BB()+(i+1)*MD.dim(),
									MD.originY_BB()+(j+1)*MD.dim(), 0)
					);
			
		
	}

	@Override
	public void render () {	
		boardAnim.step();
		
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		batch.draw(Assets.back, 0, 0, MD.width(), MD.height());
		this.boardAnim.drawTablero(batch);
		batch.end();
		
		if (Gdx.input.justTouched ()) 
			analizeEntry();
		
	}

	/**Analiza los toques en la pantalla y realiza las acciones adecuadas*/
	private void analizeEntry() {
		Vector3 pointer = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		for (int j=0; j<controlador.getRows(); j++) //Para cada fila
			for (int i=0; i<controlador.getColumns(); i++) //para cada casilla de cada fila
				if (this.cuadrTablero[j][i].contains(pointer)) {
					if (this.filaSelec < 0 || this.colSelec < 0) {//No se ha pulsado otro boton
						this.filaSelec = j;
						this.colSelec = i;
						//System.out.println("Casilla 1 (" + j + "," + i + ")");
					}
					else {
						this.controlador.intercambiar(j, i, filaSelec, colSelec);
						this.filaSelec = -1;
						this.colSelec = -1;
						//System.out.println("Casilla 2 (" + j + "," + i + ")");
					}
					
				}
			
	}

	public void updateData(Tablero tablero) {
		this.tablero = tablero;
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	/**Batch para dibujar*/
	private SpriteBatch batch;
	/**Tablero del modelo*/
	private Tablero tablero;
	/**Clase encargada de controlar la animación*/
	private BoardAnimation boardAnim;
	/**Zonas clicables de la pantalla*/
	private BoundingBox[][] cuadrTablero;
	
	private int filaSelec;
	private int colSelec;
	
	private Controlador controlador;
}
