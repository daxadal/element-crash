package com.mygdx.game.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Contiene los datos necesarios para mostrar una chuchería por pantalla, tales
 * como la posición, el dibujo o el movimiento/animación que está realizando.
 * {@link BoardAnimation} contiene una martiz de estos elementos, para poder mostrar el tablero 
 * por pantalla
 */
public class AnimationCell {
	
	private int initX;
	private float currentX;
	private int goalX;
	
	private int initY;
	private float currentY;
	private int goalY;
	
	private float time;
	private AnimationType movement;
	private Texture icon;
	private TextureRegion sprite;
	
	private static final float GRAVITY = 9.8f*60f;
	private static final float SWAP_TIME = 0.3f;
	
	private void staticX(int col) {
		this.initX = MD.originX() + col*MD.dim();
		this.currentX = MD.originX() + col*MD.dim();
		this.goalX = MD.originX() + col*MD.dim();
	}
	
	private void staticY(int fila) {
		this.initY = MD.originY() - (fila+1)*MD.dim();
		this.currentY = MD.originY() - (fila+1)*MD.dim();
		this.goalY = MD.originY() - (fila+1)*MD.dim();
	}
	
	public AnimationCell(int fila, int col, Texture icon) {
		staticY(fila);
		staticX(col);
		this.icon = icon;
		this.movement = AnimationType.NONE;
		time = 0;
		this.sprite = Assets.destroy.getKeyFrame(time, false);
	}
	
	public void newFall(int filaIni, int col, int filaFin, Texture icon) {
		this.initY = MD.originY() - (filaIni+1)*MD.dim();
		this.currentY = MD.originY() - (filaIni+1)*MD.dim();
		this.goalY = MD.originY() - (filaFin+1)*MD.dim();
		
		staticX(col);
		this.icon = icon;
		time = 0;
		movement = AnimationType.FALL;
	}
	
	public void newFallToLeft(int fila, int colIni, int colFin, Texture icon) {
		
		this.icon = icon;
		time = 0;
		movement = AnimationType.FALL_L;
		
		staticY(fila);
		this.initX = MD.originX() + colIni*MD.dim();
		this.currentX = MD.originX() + colIni*MD.dim();
		this.goalX = MD.originX() + colFin*MD.dim();	
	}
	
	public void newFallToRight(int fila, int colIni, int colFin, Texture icon) {
		
		this.icon = icon;
		time = 0;
		movement = AnimationType.FALL_R;
		
		staticY(fila);
		this.initX = MD.originX() + colIni*MD.dim();
		this.currentX = MD.originX() + colIni*MD.dim();
		this.goalX = MD.originX() + colFin*MD.dim();	
	}

	
	public void newFreeSwap(int filaIni, int colIni, int filaFin, int colFin, Texture icon) {
		
		this.icon = icon;
		time = 0;
		movement = AnimationType.SWAP_FREE;
		//TODO Hacer animación de FREE SWAP
	}
	
	public void newHorizontalSwap(int fila, int colIni, int colFin, Texture icon) {
		
		this.icon = icon;
		time = 0;
		movement = AnimationType.SWAP_H;
		
		staticY(fila);
		this.initX = MD.originX() + colIni*MD.dim();
		this.currentX = MD.originX() + colIni*MD.dim();
		this.goalX = MD.originX() + colFin*MD.dim();	
	}
	
	public void newVerticalSwap(int filaIni, int filaFin, int col, Texture icon) {
		
		this.icon = icon;
		time = 0;
		movement = AnimationType.SWAP_V;

		staticX(col);
		this.initY = MD.originY() - (filaIni+1)*MD.dim();
		this.currentY = MD.originY() - (filaIni+1)*MD.dim();
		this.goalY = MD.originY() - (filaFin+1)*MD.dim();
	}
	
	public void newDestroy() {
		this.icon = null;
		time = 0;
		this.sprite = Assets.destroy.getKeyFrame(time, false);
		movement = AnimationType.DESTROY;
	}
	
	public float getX() {
		if (movement != AnimationType.NONE)
			return currentX;
		else
			return goalX;
	}
	
	public float getY() {
		if (movement != AnimationType.NONE) 
			return currentY;
		else
			return goalY;
	}

	public Texture getIcon() {
		return icon;
	}
	
	public TextureRegion getSprite() {
		return sprite;
	}

	public boolean step() {
		boolean shouldBeRemoved = false;
		this.time += Gdx.graphics.getDeltaTime();
		
		switch (movement) {
			case FALL: 
				currentY = (float) (initY -0.5*GRAVITY*time*time);
				if (currentY<goalY) {
					this.movement = AnimationType.NONE;
					shouldBeRemoved = true;
					time = 0;
				}
			break;
			
			case FALL_L: 
				currentX = (float) (initX -0.5*GRAVITY*time*time);
				if (currentX<goalX) {
					this.movement = AnimationType.NONE;
					shouldBeRemoved = true;
					time = 0;
				}
			break;
			
			case FALL_R: 
				currentX = (float) (initX +0.5*GRAVITY*time*time);
				if (currentX>goalX) {
					this.movement = AnimationType.NONE;
					shouldBeRemoved = true;
					time = 0;
				}
			break;
			
			case DESTROY: 
				sprite = Assets.destroy.getKeyFrame(time, false);
				if (Assets.destroy.isAnimationFinished(time)) {
					this.movement = AnimationType.NONE;
					shouldBeRemoved = true;
					time = 0;
				}
			break;
			
			case SWAP_H:
				float s = (float) Math.cos(Math.PI*time/SWAP_TIME);
				currentX = ( initX + goalX + s*(initX-goalX) )/2;
				if (time/SWAP_TIME > 1) {
					this.movement = AnimationType.NONE;
					shouldBeRemoved = true;
					time = 0;
				}
			break;
			
			case SWAP_V:
				float t = (float) Math.cos(Math.PI*time/SWAP_TIME);
				currentY = ( initY + goalY + t*(initY-goalY) )/2;
				if (time/SWAP_TIME > 1) {
					this.movement = AnimationType.NONE;
					shouldBeRemoved = true;
					time = 0;
				}
			break;
			
			default:
				shouldBeRemoved = true;
			break;
		} 
		
		return shouldBeRemoved;
	}
}
