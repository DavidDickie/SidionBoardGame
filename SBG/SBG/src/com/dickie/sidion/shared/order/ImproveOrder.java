package com.dickie.sidion.shared.order;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Player;

public class ImproveOrder extends OrderImpl {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ImproveOrder(){
		super.addPrecursors(true, false, false, false, false, false, false);
	}
	
	public String validateOrder(Game game) {
		
		super.validateOrder(game);
		if (getHero(game) == null){
			return "No hero is set";
		}
		
		if (game.getGameState() == Game.RETREAT){
			Hero hero = (Hero) precursors.get("TARGET_HERO");
			if (hero == null){
				return "No target hero set";
			}
			if (getHero(game).getOwner(game) != hero.getOwner(game)){
				return "You can only improve your own heroes";
			}
			if (hero.getLevel() > 2){
				return "You cannot improve a hero past level 3";
			}
			int cost = (hero.getLevel() + 1) * (hero.getLevel() + 1);
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
	
	public void addTargetHero(Hero h){
		precursors.put("TARGET_HERO", h);
	}

	@Override
	public void addDoOrderParams() {
		precursors.put("TARGET_HERO", new Hero());
	}
	
	@Override
	public void executeOnServer(Game game){
		Hero target = (Hero)precursors.get("TARGET_HERO");
		int newLevel = target.getLevel() + 1;
		target.setLevel(newLevel);
		getHero(game).getOwner(game).addResource("GOLD",-newLevel*newLevel);
		game.addMessage(getHero(game).getName() + " [" + 
				getPlayer(game).getName() + "] increases level of " + target.getName() + 
				" to level " + target.getLevel());
	}

}
