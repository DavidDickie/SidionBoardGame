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
			if (hero.getLevel() > t.getLevel() - 1){
				return "This hero cannot improve a town that is level " + t.getLevel();
			}
			int cost = (t.getLevel() + 1) * (t.getLevel() + 1);
			if (getOwner(game).getResource("GOLD") < cost){
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
		if (t.getGold() == 0){
			options.add("GOLD");
		}
		if (t.getMana() == 0){
			options.add("MANA");
		}
		if (t.getInf() == 0){
			options.add("INF");
		}
		System.out.println("Options for town improvement are: " + options);
		int i = (int) (java.lang.Math.random()* options.size());
		System.out.println("  Option is " + options.get(i));
		t.setValue(options.get(i), "1");
	}
}
