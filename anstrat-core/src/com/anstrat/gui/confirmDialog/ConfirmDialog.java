package com.anstrat.gui.confirmDialog;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.core.Assets;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gui.GEngine;
import com.anstrat.guiComponent.ColorTable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class ConfirmDialog {

	public static final int TOP_RIGHT = 0;
	public static final int BOTTOM_RIGHT = 1;
	public static final int TOP_LEFT = 3;
	public static final int BOTTOM_LEFT = 4;
	
	public static final int distanceToEdge = 10;
	public static final int backgroundMargin = 10;
	
	public List<ConfirmRow> rows = new ArrayList<ConfirmRow>();
	TextureRegion background = Assets.getTextureRegion("TopPanel");
	TextureRegion okButton = Assets.getTextureRegion("Ok-button");
	TextureRegion cancelButton = Assets.getTextureRegion("cancel");
	ColorTable colorTable;
	public Rectangle okBounds, cancelBounds, dialogBounds;
	
	float x, y;
	
	public ConfirmDialog(int position){
		
		switch(position){
		case TOP_RIGHT:
			x=Gdx.graphics.getWidth()-ConfirmRow.ROW_WIDTH-distanceToEdge;
			y = Gdx.graphics.getHeight()*0.9f-distanceToEdge;
			break;
		default:
			x=Gdx.graphics.getWidth()-ConfirmRow.ROW_WIDTH-distanceToEdge;
			y = Gdx.graphics.getHeight()-distanceToEdge;
		}
		colorTable = new ColorTable(new Color(75/255f, 40/255f, 28/255f, 1f)); //brown wood
		colorTable.setBackground(Assets.SKIN.getPatch("single-border"));
		
		refreshBounds();
	}
	
	public void draw(SpriteBatch batch){
		
		float width = ConfirmRow.ROW_WIDTH;
		float incHeight = ConfirmRow.ROW_HEIGHT;
		batch.begin();
		
		//batch.draw(background, x, y-getHeight(), getWidth(), getHeight());
		//Assets.SKIN.getPatch("double-border").draw(batch, x, y-getHeight(), getWidth(), getHeight());
		
		colorTable.draw(batch, 1f);
		
		
		for(int i=0; i<rows.size(); i++){
			rows.get(i).draw(x, y-i*incHeight-incHeight, batch);
		}
		
		batch.draw(okButton, okBounds.x, okBounds.y, okBounds.width, okBounds.height);
		batch.draw(cancelButton, x+width/2, y-(rows.size()+2)*incHeight, width/2, incHeight*2);
		
		batch.end();
	}
	public float getWidth(){
		return ConfirmRow.ROW_WIDTH;
	}
	public float getHeight(){
		return ConfirmRow.ROW_HEIGHT * (rows.size()+2); // button row is below with twice size
	}
	public void refreshBounds(){
		okBounds = new Rectangle(x, y-getHeight(), 2*ConfirmRow.ROW_HEIGHT, 2*ConfirmRow.ROW_HEIGHT);
		cancelBounds = new Rectangle(x+2*ConfirmRow.ROW_HEIGHT, y-getHeight(), 2*ConfirmRow.ROW_HEIGHT, 2*ConfirmRow.ROW_HEIGHT);
		colorTable.x = x-backgroundMargin;
		colorTable.y = y-getHeight()-backgroundMargin;
		colorTable.width = getWidth()+2*backgroundMargin;
		colorTable.height = getHeight()+2*backgroundMargin;
	}
	
	public static ConfirmDialog moveConfirm(Unit unit, int cost, int dialogPosition){
		ConfirmDialog confirmDialog = new ConfirmDialog(dialogPosition);
		
		confirmDialog.rows.add(new TextRow("Move"));
		confirmDialog.rows.add(new APRow(unit, cost));
		
		confirmDialog.refreshBounds();
		
		return confirmDialog;
	}
	
}