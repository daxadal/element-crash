package com.icecubelab.elementcrash.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GifData {

	public GifData(SpriteBatch batch){
		this.batch = batch;
		
		Texture walkSheet = new Texture(Gdx.files.internal("iconos/gifs/ice_002.png"));
		TextureRegion[][] tmp = TextureRegion.split(walkSheet,
				walkSheet.getWidth() / FRAME_COLS, 
				walkSheet.getHeight()/ FRAME_ROWS
			);
		
		TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * (FRAME_ROWS-3)];
		int index = 0;
		for (int i = 3; i < FRAME_ROWS; i++) {
			for (int j = 0; j < FRAME_COLS; j++) {
				walkFrames[index++] = tmp[i][j];
			}
		}
		
		walkAnimation = new Animation<TextureRegion>(ANIM_DURATION, walkFrames); 
		batch = new SpriteBatch(); 
		time = 0f; 
	}
	
	public boolean step() {
		time += Gdx.graphics.getDeltaTime(); // #15
		currentFrame = walkAnimation.getKeyFrame(time, true); // #16
		
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();	
		batch.draw(currentFrame, 0, 0); // #17
		batch.end();
		
		return walkAnimation.isAnimationFinished(time);
	}
	
	private static final float ANIM_DURATION = 0.5f;
	private static final int FRAME_COLS = 5; // #1
	private static final int FRAME_ROWS = 6; // #2
	
	private Animation<TextureRegion> walkAnimation; // #3
	private SpriteBatch batch; // #6
	private TextureRegion currentFrame; // #7
	private float time; // #8
}
