package com.anstrat.gui;

import com.anstrat.core.Assets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * 
 * @author Anton
 * All occurances access this class in a static way to avoid multiple instances.
 */
public class APPieDisplay {
	
	public static boolean ONLY_USE_NUMBER_8 = false;
	
	
	/**
	 * Draws a pie-chart for displaying ap.
	 * First version may only support drawing on the map (Y is downwards)
	 * @param x top left corner
	 * @param y top left corner
	 * @param width
	 * @param height
	 * @param currentAP current ap of the display
	 * @param maxAP maximum ap the display can show
	 * @param apReg how much ap is regenerated next turn (use for displaying this to user)
	 * @param attackCost used to display if the unit can attack with this amount of AP
	 * @param batch
	 */
	public static void draw(float x, float y, float size, 
			int currentAP, int maxAP, int apReg, int nextAttackCost, SpriteBatch batch){
		
		if(ONLY_USE_NUMBER_8)
			maxAP = 8;
		
		batch.setColor(Color.toFloatBits(1f, 1f, 1f, 1f));
		TextureRegion background = Assets.getTextureRegion("APPie-bg");
		batch.draw(background, x, y, size/2, size/2, size, size, 1f, 1f, 0f);
		
		int pieceNumber;
		batch.setColor(Color.CYAN);
		for(pieceNumber=0; pieceNumber < currentAP; pieceNumber++ ){
			drawPiece(x, y, size, maxAP, pieceNumber, batch);
		}
		batch.setColor(Color.toFloatBits(0.0f, 0.3f, 0.3f, 1f));
		for(pieceNumber=pieceNumber; pieceNumber < currentAP+apReg && pieceNumber < maxAP; pieceNumber++ ){
			drawPiece(x, y, size, maxAP, pieceNumber, batch);
		}
		batch.setColor(Color.WHITE);
		
		TextureRegion foreground = Assets.getTextureRegion("APPie-front-"+maxAP);
		batch.draw(foreground, x, y, size/2, size/2, size, size, 1f, 1f, 180f);
		
		//batch.dr
		/* pseudocode 
		 * drawBackground();
		 * drawNextTurnAP(); // what is regenerated next turn. Gray
		 * drawCurrentAP(); // Teal
		 * drawOverlay(); // Black borders for entire pie
		 * drawAttackArrow(); // Small red arrow pointing at attack cost. Do not draw if 0
		 * */
	}
	private static void drawPiece(float x, float y, float size, int maxAP, int pieceNumber, SpriteBatch batch){
		TextureRegion piece = Assets.getTextureRegion("APPie-piece-"+maxAP); // change to correct number later
		float rotation = 360/maxAP;
		float initialRotation = 180+rotation;
		
		batch.draw(piece, x, y, size/2, size/2, size, size, 1f, 1f, initialRotation + rotation*pieceNumber);
	}
	

}
