package com.anstrat.gameCore.playerAbilities;

import java.util.List;

import com.anstrat.animation.Animation;
import com.anstrat.animation.UncleLokiWantsYouAnimation;
import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.Tile;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.Gdx;

public class UncleLokiWantsYou extends TargetedPlayerAbility {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UncleLokiWantsYou(Player player) {
		super(player, PlayerAbilityType.UNCLE_LOKI);
	}
	
	@Override
	public void activate(Player player, TileCoordinate tile){
		super.activate();
		Unit target = StateUtils.getUnitByTile(tile);
		Gdx.app.log("PlayerAbility", "Uncle Loki wants you was cast");
		target.ownerId = player.playerId;
		List<Tile> tiles = State.activeState.map.getNeighbors(tile);
		if (State.activeState.map.getTile(tile).visible[GameInstance.activeGame.getUserPlayer().playerId] < 1)
			State.activeState.map.getTile(tile).visible[GameInstance.activeGame.getUserPlayer().playerId] = 1;
		for(Tile t : tiles) {
			if (t.visible[GameInstance.activeGame.getUserPlayer().playerId] < 1) {
				t.visible[GameInstance.activeGame.getUserPlayer().playerId] = 1;
			}
		}
		Fog.recalculateFog(GameInstance.activeGame.getUserPlayer().playerId, State.activeState);
		Animation animation = new UncleLokiWantsYouAnimation(target);
		GEngine.getInstance().animationHandler.enqueue(animation);
	}
	
	@Override
	public boolean isAllowed(Player player, TileCoordinate target) {
		Unit targetUnit = StateUtils.getUnitByTile(target);
		return super.isAllowed(player) && 
				targetUnit != null &&
				targetUnit.ownerId != player.playerId &&
				Fog.isVisible(target, player.playerId);
	}
}
