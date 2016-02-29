package com.mygdx.game.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.controlador.StuffList;

/** Clase que carga y almacena los recursos gráficos del juego.
 * Al principio del juego se debe llamar a loadElements() o loadCandies() para cargar
 * los sprites del juego (según la vista que queramos)
 */
public class Assets {
	/**
	 * Carga las imágenes (sprites) de los elementos del juego para la vista
	 * de Element Crash. Debe ser llamada al principio del juego.
	 */
	public static void loadElements(){
		
		//Normales
		azulN = new Texture("iconos/element images/aguaN.png");
		amarilloN = new Texture("iconos/element images/aireN.png");
		rojoN = new Texture("iconos/element images/fuegoN.png");
		naranjaN = new Texture("iconos/element images/tierraN.png");
		moradoN = new Texture("iconos/element images/sombraN.png");
		verdeN = new Texture("iconos/element images/vidaN.png");
		
		//Decoraciones y animaciones
		back = new Texture("iconos/element images/dragones_de_hielo.jpg");
		destroy = loadAnimation("iconos/gifs/destroy_ice_002_small.png", 2, 5, 0.5f);
		
		
	}
	
	/**
	 * Carga las imágenes (sprites) de los elementos del juego para la vista
	 * de Candy Crush. Debe ser llamada al principio del juego.
	 */
	public static void loadCandies(){
		//Normales
		azulN = new Texture("iconos/candy images/caramelos/NormRayEnv/normalAz.png");
		amarilloN = new Texture("iconos/candy images/caramelos/NormRayEnv/normalAm.png");
		rojoN = new Texture("iconos/candy images/caramelos/NormRayEnv/normalR.png");
		naranjaN = new Texture("iconos/candy images/caramelos/NormRayEnv/normalN.png");
		moradoN = new Texture("iconos/candy images/caramelos/NormRayEnv/normalM.png");
		verdeN = new Texture("iconos/candy images/caramelos/NormRayEnv/normalV.png");
		
		//Rallados H
		azulRH = new Texture("iconos/candy images/caramelos/NormRayEnv/RhorAz.png");
		amarilloRH = new Texture("iconos/candy images/caramelos/NormRayEnv/RhorAm.png");
		rojoRH = new Texture("iconos/candy images/caramelos/NormRayEnv/RhorR.png");
		naranjaRH = new Texture("iconos/candy images/caramelos/NormRayEnv/RhorN.png");
		moradoRH = new Texture("iconos/candy images/caramelos/NormRayEnv/RhorM.png");
		verdeRH = new Texture("iconos/candy images/caramelos/NormRayEnv/RhorV.png");
		
		//Rallados V
		azulRV = new Texture("iconos/candy images/caramelos/NormRayEnv/RvertAz.png");
		amarilloRV = new Texture("iconos/candy images/caramelos/NormRayEnv/RvertAm.png");
		rojoRV = new Texture("iconos/candy images/caramelos/NormRayEnv/RvertR.png");
		naranjaRV = new Texture("iconos/candy images/caramelos/NormRayEnv/RvertN.png");
		moradoRV = new Texture("iconos/candy images/caramelos/NormRayEnv/RvertM.png");
		verdeRV = new Texture("iconos/candy images/caramelos/NormRayEnv/RvertV.png");
		
		//Envueltos (inactivos)
		azulE = new Texture("iconos/candy images/caramelos/NormRayEnv/envueltoAz.png");
		amarilloE = new Texture("iconos/candy images/caramelos/NormRayEnv/envueltoAm.png");
		rojoE = new Texture("iconos/candy images/caramelos/NormRayEnv/envueltoR.png");
		naranjaE = new Texture("iconos/candy images/caramelos/NormRayEnv/envueltoN.png");
		moradoE = new Texture("iconos/candy images/caramelos/NormRayEnv/envueltoM.png");
		verdeE = new Texture("iconos/candy images/caramelos/NormRayEnv/envueltoV.png");
		
		//Envueltos (explotando)
		azulX = new Texture("iconos/candy images/caramelos/NormRayEnv/estalladoAz.png");
		amarilloX = new Texture("iconos/candy images/caramelos/NormRayEnv/estalladoAm.png");
		rojoX = new Texture("iconos/candy images/caramelos/NormRayEnv/estalladoR.png");
		naranjaX = new Texture("iconos/candy images/caramelos/NormRayEnv/estalladoN.png");
		moradoX = new Texture("iconos/candy images/caramelos/NormRayEnv/estalladoM.png");
		verdeX = new Texture("iconos/candy images/caramelos/NormRayEnv/estalladoV.png");
		
		//Chuches sin color
		bombaColor = new Texture("iconos/candy images/caramelos/bombaColor.png");
		cereza = new Texture("iconos/candy images/caramelos/cereza.png");
		cerezaAz = new Texture("iconos/candy images/caramelos/cerezaAz.png");
		cerezaR = new Texture("iconos/candy images/caramelos/cerezaR.png");
				
		//gelatinas
		gelatinaAz = new Texture("iconos/candy images/gelatina/gelatinaAz.png");
		gelatinaR = new Texture("iconos/candy images/gelatina/gelatinaR.png");
		
		//Decoraciones y animaciones
		back = new Texture("iconos/candy images/fondo.png");
		destroy = loadAnimation("iconos/gifs/destroy_ice_002_small.png", 2, 5, 0.5f);
	}
	
	/**
	 * A partir de un ID, devuelve el sprite correspondiente
	 * @param thing ID del objeto
	 * @return Texture con el sprite del objeto
	 */
	public static Texture getIcon(StuffList thing) {
		Texture icon = null;
		switch (thing) {
			//Caramelos normales
			case CARAMELO_NORMAL_AMARILLO:	icon = amarilloN;	break;
			case CARAMELO_NORMAL_AZUL:		icon = azulN;	break;
			case CARAMELO_NORMAL_ROJO:		icon = rojoN;	break;
			case CARAMELO_NORMAL_NARANJA:	icon = naranjaN;	break;
			case CARAMELO_NORMAL_VERDE:		icon = verdeN;	break;
			case CARAMELO_NORMAL_MORADO:	icon = moradoN;	break;
			
			//Caramelos rallados H
			case CARAMELO_RALLADO_H_AMARILLO:	icon = amarilloRH;	break;
			case CARAMELO_RALLADO_H_AZUL:		icon = azulRH;	break;
			case CARAMELO_RALLADO_H_ROJO:		icon = rojoRH;	break;
			case CARAMELO_RALLADO_H_NARANJA:	icon = naranjaRH;	break;
			case CARAMELO_RALLADO_H_VERDE:		icon = verdeRH;	break;
			case CARAMELO_RALLADO_H_MORADO:		icon = moradoRH;	break;
			
			//Caramelos rallados v
			case CARAMELO_RALLADO_V_AMARILLO:	icon = amarilloRV;	break;
			case CARAMELO_RALLADO_V_AZUL:		icon = azulRV;	break;
			case CARAMELO_RALLADO_V_ROJO:		icon = rojoRV;	break;
			case CARAMELO_RALLADO_V_NARANJA:	icon = naranjaRV;	break;
			case CARAMELO_RALLADO_V_VERDE:		icon = verdeRV;	break;
			case CARAMELO_RALLADO_V_MORADO:		icon = moradoRV;	break;
			
			//Envueltos (inactivos)
			case CARAMELO_ENVUELTO_AMARILLO:	icon = amarilloE;	break;
			case CARAMELO_ENVUELTO_AZUL:		icon = azulE;	break;
			case CARAMELO_ENVUELTO_ROJO:		icon = rojoE;	break;
			case CARAMELO_ENVUELTO_NARANJA:		icon = naranjaE;	break;
			case CARAMELO_ENVUELTO_VERDE:		icon = verdeE;	break;
			case CARAMELO_ENVUELTO_MORADO:		icon = moradoE;	break;
			
			//Envueltos (explotando)
			case CARAMELO_EXPLOTANDO_AMARILLO:	icon = amarilloX;	break;
			case CARAMELO_EXPLOTANDO_AZUL:		icon = azulX;	break;
			case CARAMELO_EXPLOTANDO_ROJO:		icon = rojoX;	break;
			case CARAMELO_EXPLOTANDO_NARANJA:	icon = naranjaX;	break;
			case CARAMELO_EXPLOTANDO_VERDE:		icon = verdeX;	break;
			case CARAMELO_EXPLOTANDO_MORADO:	icon = moradoX;	break;
			
			//Chuches sin color
			case BOMBA_COLOR:	icon = bombaColor;	break;
			case CEREZA:		icon = cereza;	break;
			case CEREZA_AZUL:	icon = cerezaAz;	break;
			case CEREZA_ROJA:	icon = cerezaR;	break;
			
		}
		return icon;
	}
	
	/**
	 * Carga una animación a partir de una imágen, con los frames organizados 
	 * en una matriz
	 * @param url dirección del archivo
	 * @param FRAME_ROWS número de filas de frames
	 * @param FRAME_COLS número de columnas de frames
	 * @param ANIM_DURATION duración de la animación
	 * @return Cclase Animation cargada con la animación
	 */
	private static Animation loadAnimation(String url, int FRAME_ROWS, int FRAME_COLS, float ANIM_DURATION) {
		//Animations
		Texture walkSheet = new Texture(Gdx.files.internal(url));
		TextureRegion[][] tmp = TextureRegion.split(walkSheet,
				walkSheet.getWidth() / FRAME_COLS, 
				walkSheet.getHeight()/ FRAME_ROWS
			);
		
		TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
		int index = 0;
		for (int i = 0; i < FRAME_ROWS; i++) {
			for (int j = 0; j < FRAME_COLS; j++) {
				walkFrames[index++] = tmp[i][j];
			}
		}
		
		return new Animation(ANIM_DURATION / (FRAME_COLS * FRAME_ROWS), walkFrames); 
	}
	
	//Normales
	public static Texture azulN;
	public static Texture amarilloN;
	public static Texture rojoN;
	public static Texture naranjaN;
	public static Texture moradoN;
	public static Texture verdeN;
	
	//Rallados H
	public static Texture azulRH;
	public static Texture amarilloRH;
	public static Texture rojoRH;
	public static Texture naranjaRH;
	public static Texture moradoRH;
	public static Texture verdeRH;
	
	//Rallados V
	public static Texture azulRV;
	public static Texture amarilloRV;
	public static Texture rojoRV;
	public static Texture naranjaRV;
	public static Texture moradoRV;
	public static Texture verdeRV;
	
	//Envueltos (inactivos)
	public static Texture azulE;
	public static Texture amarilloE;
	public static Texture rojoE;
	public static Texture naranjaE;
	public static Texture moradoE;
	public static Texture verdeE;
	
	//Envueltos (explotando)
	public static Texture azulX;
	public static Texture amarilloX;
	public static Texture rojoX;
	public static Texture naranjaX;
	public static Texture moradoX;
	public static Texture verdeX;
	
	//Chuches sin color
	public static Texture bombaColor;
	public static Texture cereza;
	public static Texture cerezaAz;
	public static Texture cerezaR;
	
	//Gelatinas
	public static Texture gelatinaR;
	public static Texture gelatinaAz;

	public static Texture back;
	
	public static Animation destroy;
}
