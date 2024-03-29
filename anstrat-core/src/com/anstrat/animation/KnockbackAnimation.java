package com.anstrat.animation;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.CombatLog;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.UnitType;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class KnockbackAnimation extends Animation {

	/** Time for entire animation */
	public float attackSpeed = 1.1f; 
	public float impactTime = 1.0f;
	public float impactAnimationTime = 1.0f;
	public float rangedDelay = 0.6f;
	
	private boolean pastImpact = false;
	
	/** Projectile positions */
	private Vector2 start, current, target;
	private float xoffset, yoffset, amtOffset;
	private boolean started, canMove;
	private GUnit gAttacker, gDefender;
	private CombatLog cl;
	private float timeElapsed;
	private final static float moveSpeed = 0.5f;
	private TileCoordinate originating, kbCoord;
	
	/**Knockback positions*/
	private Vector2 startP, currentP, endP;
	
	private String impactAnimationName;
	
	public KnockbackAnimation(CombatLog cl, boolean canMove, TileCoordinate originating, TileCoordinate kbCoord){
		this.originating = originating;
		this.kbCoord = kbCoord;
		this.cl = cl;
		this.canMove = canMove;		
		
		this.length = attackSpeed;
		this.lifetimeLeft = length;
		
		GEngine ge = GEngine.getInstance();
		start = ge.getMap().getTile(cl.attacker.tileCoordinate).getCenter();
		
		
		target = ge.getMap().getTile(cl.defender.tileCoordinate).getCenter();
		gAttacker = ge.getUnit(cl.attacker);
		gDefender = ge.getUnit(cl.defender);
		xoffset = target.x - start.x;
		yoffset = target.y - start.y;
		current = new Vector2();
		
		// Impact animation
		switch(cl.attacker.getUnitType()){
			default:
				impactAnimationName = "hammer-attack-effect";
				break;
		}
	}
	
	@Override
	public void run(float deltaTime) {
		
		// Run once
		if (!started) {
			GEngine ge = GEngine.getInstance();
			ge.updateUI();
			
			if(isVisible()) {
				Animation mAnimation = new MoveCameraAnimation(gDefender.getPosition());
				ge.animationHandler.runParalell(mAnimation);
			}
			
			//gAttacker.healthBar.text = String.valueOf(cl.newAttackerAP);
			gAttacker.healthBar.currentAP = cl.newAttackerAP;
			
			boolean facingRight = GEngine.getInstance().getUnit(cl.attacker).getPosition().x <= 
					GEngine.getInstance().getUnit(cl.defender).getPosition().x;
			gAttacker.setFacingRight(facingRight);
			gDefender.setFacingRight(!facingRight);
			gAttacker.playCustom(Assets.getAnimation("troll-ability"),false);
			
			if(canMove)
				ge.animationHandler.enqueue(new KnockbackEffectAnimation(cl, kbCoord));
			started = true;
		}
		
		if(!pastImpact && length - lifetimeLeft > impactTime){ // Time of impact
			// Show damage taken etc.
			GEngine ge = GEngine.getInstance();
			FloatingNumberAnimation animation = new FloatingNumberAnimation(originating, cl.attackDamage, 40f, Color.RED);
			ge.animationHandler.runParalell(animation);
			gDefender.playHurt();
			float healthPercentage = (float)cl.newDefenderHP/(float)cl.defender.getMaxHP();
			
			if(healthPercentage < 0f){
				healthPercentage = 0f;
			}
			
			//gDefender.healthBar.setValue(healthPercentage);
			gDefender.healthBar.setHealth(healthPercentage, cl.newDefenderHP);
			boolean directionLeft = start.x > target.x;
			GEngine.getInstance().animationHandler.runParalell(new BloodAnimation(gDefender,directionLeft));
			
			if(cl.newDefenderHP <= 0){
				Vector2 temp = new Vector2(gDefender.getPosition());
				ge.animationHandler.runParalell(new DeathAnimation(cl.defender, 
						temp.sub(gAttacker.getPosition()).nor()));
			}
			pastImpact = true;
		}
	}
	
	@Override
	public void draw(float deltaTime, SpriteBatch batch){
		super.draw(deltaTime, batch);
		
		float animationTimePassed = length - lifetimeLeft;
		
		// Have the projectile reached its target?
		if(animationTimePassed > rangedDelay){
			
			UnitType type = cl.attacker.getUnitType();
			TextureRegion region = null;
			
			if(type == UnitType.AXE_THROWER){
				region = Assets.getAnimation("axe-effect").getKeyFrame(animationTimePassed, true);
			}
			else if((type == UnitType.SHAMAN || type == UnitType.GOBLIN_SHAMAN) && length - lifetimeLeft < impactTime){
				region = Assets.getAnimation("shaman-fireball").getKeyFrame(animationTimePassed, true);
			}
			
			// Draw impact effect
			if(region != null) batch.draw(region, current.x - region.getRegionWidth() / 2, current.y + region.getRegionHeight() / 2);
		}
	}

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return Fog.isVisible(gAttacker.unit.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId) ||
				Fog.isVisible(gDefender.unit.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId);
	}
}

