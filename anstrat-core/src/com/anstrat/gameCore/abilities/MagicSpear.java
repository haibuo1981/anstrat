
package com.anstrat.gameCore.abilities;

import com.anstrat.animation.Animation;
import com.anstrat.animation.DeathAnimation;
import com.anstrat.animation.HealAnimation;
import com.anstrat.animation.MagicSpearAnimation;
import com.anstrat.animation.UpdateBarAnimation;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.SelectionHandler;
import com.anstrat.gui.confirmDialog.APRow;
import com.anstrat.gui.confirmDialog.ConfirmDialog;
import com.anstrat.gui.confirmDialog.ConfirmRow;
import com.anstrat.gui.confirmDialog.DamageRow;
import com.anstrat.gui.confirmDialog.TextRow;

public class MagicSpear extends TargetedAbility {
	private static final long serialVersionUID = 1L;
	private static final int AP_COST = 3;
	private static final int RANGE = 2;

	
	public MagicSpear(){
		super("Magic Spear","Uses a magic spear to attack from range",AP_COST, RANGE);
		iconName = "spear-button";
	}
	
	

	public boolean isAllowed(Unit source, TileCoordinate coordinates) {
		Unit targetUnit = StateUtils.getUnitByTile(coordinates);
		
		return super.isAllowed(source, coordinates) 
				&& targetUnit != null
				&& targetUnit.ownerId != source.ownerId;
	}

	@Override
	public void activate(Unit source, TileCoordinate coordinate) {
		super.activate(source, coordinate);
		
		Unit targetUnit = StateUtils.getUnitByTile(coordinate);
		
		targetUnit.currentHP -= source.getAttack();
		targetUnit.resolveDeath();
		
		Animation animation = new MagicSpearAnimation(source, targetUnit, source.getAttack());
		GEngine.getInstance().animationHandler.enqueue(animation);
		animation = new UpdateBarAnimation(targetUnit);
		GEngine.getInstance().animationHandler.enqueue(animation);
		if(!targetUnit.isAlive){
			animation = new DeathAnimation(targetUnit,source.tileCoordinate);
			GEngine.getInstance().animationHandler.enqueue(animation);
		}
	}
	
	@Override
	public ConfirmDialog generateConfirmDialog(Unit source, TileCoordinate target, int position){
		ConfirmRow nameRow = new TextRow(name);
		ConfirmRow apRow = new APRow(source, apCost);
		ConfirmRow damageRow = new DamageRow(source.getAttack(), source.getAttack());
		return ConfirmDialog.abilityConfirm(position, nameRow, apRow, damageRow);
	}
	
	@Override
	public String getIconName(Unit source) {
		if(!isAllowed(source)) return "heal-button-gray";
		if(GEngine.getInstance().selectionHandler.selectionType == SelectionHandler.SELECTION_TARGETED_ABILITY){
			return "heal-button-active";
		}
		return "heal-button";
	}

}
