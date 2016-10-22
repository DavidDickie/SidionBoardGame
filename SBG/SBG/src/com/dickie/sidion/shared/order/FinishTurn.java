package com.dickie.sidion.shared.order;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Player;

public class FinishTurn extends OrderImpl {

	@Override
	public void executeOnServer(Game game) {
		this.getOwner(game).setTurnFinished(true); 
	}
	
	@Override
	public String toString() {
		String s = getClass().getSimpleName() + " for " + getValue("PLAYER");
		return s;
	}

	@Override
	public boolean isExecutable(Game game) {
		return true;
	}

	@Override
	public void addDoOrderParams() {
	}
	
	@Override
	public String validateOrder(Game game) {
		try{
			if (getValue("PLAYER") == null) {
				return "No player set";
			}
		} catch (Exception e) {
			return e.getMessage();
		}
		return null;
	}

}
