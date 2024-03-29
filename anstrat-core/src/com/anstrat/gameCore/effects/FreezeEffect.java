package com.anstrat.gameCore.effects;

import com.anstrat.gameCore.Unit;

public class FreezeEffect extends Effect implements APRegenerationModifier, TriggerOnTurnEnd {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ap;
	private int nrOfRoundsTotal;
	private int nrOfRoundsSoFar = 0;
	public FreezeEffect(int ap, int nr) {
		this.ap = ap;
		this.nrOfRoundsTotal = nr;
		this.iconName = "sword";
	}
	
	@Override
	public void triggerOnTurnEnd(Unit u) {
		if (nrOfRoundsTotal <= nrOfRoundsSoFar ){
			sheduledRemove = true;
		}
		else{
			nrOfRoundsSoFar++;
		}
		
	}

	@Override
	public int APRegModication() {
		return -ap;
	}
}
