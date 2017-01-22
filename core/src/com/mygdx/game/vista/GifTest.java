package com.mygdx.game.vista;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**@deprecated*/
public class GifTest implements ApplicationListener{


	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void create() {		
		
		Texture walkSheet = new Texture(Gdx.files.internal("iconos/gifs/destroy_ice_002.png")); // #9
		TextureRegion[][] tmp = TextureRegion.split(walkSheet,
				walkSheet.getWidth() / FRAME_COLS, walkSheet.getHeight()
						/ FRAME_ROWS); // #10
		TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
		int index = 0;

		for (int i = 0; i < FRAME_ROWS; i++) {
			for (int j = 0; j < FRAME_COLS; j++) {
				walkFrames[index++] = tmp[i][j];
			}
		}
		walkAnimation = new Animation(0.07f, walkFrames); // #11
		spriteBatch = new SpriteBatch(); // #12
		stateTime = 0f; // #13
		
		Assets.loadCandies();
		anim = new AnimationData(5, 5, Assets.azulN);
		anim.newDestroy();

	}	
		
	

	@Override
	public void render() {
		stateTime += Gdx.graphics.getDeltaTime();; // #15
		currentFrame = walkAnimation.getKeyFrame(stateTime, false); // #16
		
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		spriteBatch.begin();
		anim.step();
		//spriteBatch.draw(anim.getIcon(), anim.getX(), anim.getY());
		spriteBatch.draw(anim.getSprite(), 0, 0, 200, 200); // #17
		spriteBatch.end();
		
		
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

	private static final int FRAME_COLS = 5; // #1
	private static final int FRAME_ROWS = 3; // #2

	AnimationData anim;
	Animation walkAnimation; // #3
	SpriteBatch spriteBatch; // #6
	TextureRegion currentFrame; // #7
	float stateTime; // #8


}
