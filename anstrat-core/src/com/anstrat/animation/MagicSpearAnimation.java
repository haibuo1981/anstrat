package com.anstrat.animation;


import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.Unit;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class MagicSpearAnimation extends Animation {

	/** Time for entire animation */
	public float attackSpeed = 0.8f; 
	public float impactTime = 0.5f;
	public float impactAnimationTime = 0.3f;
	public float rangedDelay = 0.6f;
	
	private boolean pastImpact = false;
	private boolean pastImpactAnimation = false;
	
	/** Projectile positions */
	private Vector2 start, current, target;
	private float xoffset, yoffset;
	private boolean started;
	private GUnit gAttacker, gDefender;
	
	private Unit sourceUnit, targetUnit;
	private int damage;
	private boolean firstUnit;
	boolean facingRight;
	private Sprite spearSprite = null;
	
	public MagicSpearAnimation(Unit sourceUnit, Unit targetUnit, int damage){
		this.sourceUnit = sourceUnit;
		this.targetUnit = targetUnit;
		this.damage = damage;
	
		
		// Set animation timings
		rangedDelay = 0.3f;
		attackSpeed = 1.2f;
		impactTime = 1f;
		impactAnimationTime = impactTime; // start playing exactly when spear hits

		this.length = attackSpeed;
		this.lifetimeLeft = length;
		
		GEngine ge = GEngine.getInstance();
		start = ge.getMap().getTile(sourceUnit.tileCoordinate).getCenter();
		start.y -= 65;
		
		target = ge.getMap().getTile(targetUnit.tileCoordinate).getCenter();
		target.y -= 65;
		
		gAttacker = ge.getUnit(sourceUnit);
		gDefender = ge.getUnit(targetUnit);
		xoffset = target.x - start.x;
		yoffset = target.y - start.y;
		current = new Vector2();
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
				
			if(firstUnit){
				gAttacker.healthBar.currentAP = sourceUnit.currentAP;
			}
			
			facingRight = start.x <= target.x;
			gAttacker.setFacingRight(facingRight);
			//gAttacker.playAttack();
			gAttacker.playCustom(Assets.getAnimation("valkyrie-ability"), false);

			started = true;
		}
		
		if(!pastImpactAnimation && length - lifetimeLeft > impactAnimationTime){ // Time of impact animation (slightly before actual impact
			//GEngine.getInstance().animationHandler.runParalell(new GenericVisualAnimation(Assets.getAnimation(impactAnimationName), target, 100)); // size 100 is slightly smaller than a tile
			gDefender.playHurt();
			pastImpactAnimation = true;
		}
		
		if(!pastImpact && length - lifetimeLeft > impactTime){ // Time of impact
			// Show damage taken etc.
			GEngine ge = GEngine.getInstance();
			FloatingNumberAnimation fanimation = new FloatingNumberAnimation(targetUnit.tileCoordinate, damage, 40f, Color.RED);
			ge.animationHandler.runParalell(fanimation);
			float healthPercentage = (float)targetUnit.currentHP/(float)targetUnit.getMaxHP();
			
			if(healthPercentage < 0f){
				healthPercentage = 0f;
			}
			
			//gDefender.healthBar.setValue(healthPercentage);t
			gDefender.healthBar.setHealth(healthPercentage, targetUnit.currentHP);
			boolean directionLeft = start.x > target.x;
			GEngine.getInstance().animationHandler.runParalell(new BloodAnimation(gDefender,directionLeft));
			
			if(targetUnit.currentHP <= 0){
				Vector2 temp = new Vector2(gDefender.getPosition());
				ge.animationHandler.runParalell(new DeathAnimation(targetUnit, 
						temp.sub(gAttacker.getPosition()).nor()));
			}
			pastImpact = true;
		}

		// Update projectile position
		float timeTaken = attackSpeed - lifetimeLeft;
		float amtOffset = (timeTaken - rangedDelay) / (impactTime - rangedDelay);
		current.set(start.x + xoffset * amtOffset, start.y + yoffset * amtOffset);
	}			
	
	@Override
	public void draw(float deltaTime, SpriteBatch batch){
		super.draw(deltaTime, batch);
		
		float animationTimePassed = length - lifetimeLeft;
		
		// Have the projectile reached its target?
		
			if(animationTimePassed > rangedDelay){
				TextureRegion region = null;
				region = Assets.getAnimation("valkyrie-ability-effect").getKeyFrame(animationTimePassed, true);
				if(spearSprite==null)
					spearSprite = new Sprite(region);
				spearSprite.setScale(0.77f);
				
				// Draw impact effect
				if(spearSprite != null) {
					spearSprite.setPosition(current.x - region.getRegionWidth() / 2, current.y);
					spearSprite.setRotation((float)getRotationAngle());	
					spearSprite.draw(batch);
				}
			}
	}
	
	private double getRotationAngle() {
        float dx = (int) (target.x-start.x);
        float dy = (int) (target.y-start.y);
        float x = Math.abs(dx);
        float y = Math.abs(dy);
        double res;
        if (y == 0) {
            if ( dx < 0 ){
                res = 270;
            }
            else {
                res = 90;
            }
        }
        else if (x != 0) {
            res = Math.toDegrees(Math.atan(y/x));
            if (dx < 0 && dy < 0) {
                res += 270;
            }
            else if (dx < 0 && dy > 0) {
                res = 90 - res;
                res += 180;
            }
            else if (dx > 0 && dy > 0) {
                res += 90;
            }
            else { //normal
                res = 90-res;
            }
        }
        else {
            if (dy > 0) {
                res = 180;
            }
            else {
                res = 0;
            }
        }
        return res;
    }
	
	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return Fog.isVisible(sourceUnit.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId) ||
				Fog.isVisible(targetUnit.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId);
	}
}
