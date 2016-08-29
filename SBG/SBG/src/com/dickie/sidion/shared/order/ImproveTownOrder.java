package com.dickie.sidion.shared.order;

import java.util.ArrayList;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.Town;

public class ImproveTownOrder extends OrderImpl{

	private static final long serialVersionUID = 1L;

	public ImproveTownOrder(){
		super.addPrecursors(true, false, false, false, false, false, false);
	}
	
	public String validateOrder(Game game) {
		
		super.validateOrder(game);
		Hero hero = getHero(game);
		if (hero == null){
			return "No hero is set";
		}
		
		if (game.getGameState() == Game.PHYS_PHASE){
			Town t = hero.getLocation(game);
			if (hero.getLevel() <= t.getLevel()){
				return "This hero cannot improve a town that is level " + t.getLevel();
			}
			int cost =t.getUpgradeCost();
			if (hero.getOwner(game).getResource("GOLD") < cost){
				return "You do not have " + cost + " gold";
			}
		}
		return null;
	}
	
	@Override
	public boolean isExecutable(Game game) {
		if (game.getGameState() == Game.ORDER_PHASE){
			return true;
		}
		if (game.getGameState() == Game.PHYS_PHASE){
			addDoOrderParams();
			return true;
		}
		return false;
	}

	@Override
	public void addDoOrderParams() {
	}
	
	@Override
	public void executeOnServer(Game game){
		Town t = getHero(game).getLocation(game);
		ArrayList<String> options = new ArrayList<String>();
		int cost = t.getUpgradeCost();
		getHero(game).getOwner(game).addResource("GOLD", -cost);
		if (t.getGold() == 0){
			options.add("GOLD");
		}
		if (t.getMana() == 0){
			options.add("MANA");
		}
		if (t.getInf() == 0){
			options.add("INF");
		}
		int i = (int) (java.lang.Math.random()* options.size());
		game.addMessage(getHero(game).getName() + " [" + 
				getPlayer(game).getName() + "] improved " + t.getName() + " to produce " +  options.get(i));
		t.setValue(options.get(i), "1");
	}
}
