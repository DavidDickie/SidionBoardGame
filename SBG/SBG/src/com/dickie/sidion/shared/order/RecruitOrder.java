package com.dickie.sidion.shared.order;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Player;

public class RecruitOrder  extends OrderImpl {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RecruitOrder(){
		super.addPrecursors(true, false, false, false, false, false, false);
	}
	
	public String validateOrder(Game game) {
		
		super.validateOrder(game);
		if (checkForHero(game) != null){
			return checkForHero(game);
		}
		Hero prince = getHero(game);
		if (!prince.isPrince()){
			return "Only princes may recruit";
		}
		if (getOwner(game).getGold() < getRecruitCost()){
			return "Insufficient funds";
		}
		if (!prince.getLocation(game).hasHero()){
			return "There is no nuetral hero to recruit";
		}

		return null;
	}
	
	public static int getRecruitCost(){
		return 5;
	}
	
	@Override
	public boolean isExecutable(Game game) {
		if (game.getGameState() == Game.ORDER_PHASE){
			return true;
		}
		if (game.getGameState() == Game.RETREAT){
			return true;
		}
		return false;
	}

	@Override
	public void addDoOrderParams() {
		//precursors.put("TOWN", new Town());
	}
	
	@Override
	public void executeOnServer(Game game){
		int maxInt = -1;
		for (Hero h : game.getHeros()){
			int c = Integer.valueOf(h.getName().substring(h.getName().indexOf("_")+1));
			if (c > maxInt){
				maxInt = c;
			}
		}
		maxInt++;
		Hero hero = new Hero();
		hero.setKey("Hero_" + maxInt);
		hero.setLevel(1);
		hero.setLocation(this.getHero(game).getLocation(game));
		hero.setOwner(getOwner(game));
		getHero(game).getLocation(game).setHasHero(false);
		game.addGameComponent(hero);
		getOwner(game).addResource("GOLD", -getRecruitCost());
		game.addMessage(getHero(game).getName() + " [" + 
				getPlayer(game).getName() + "] recuited a new hero in " + getHero(game).getLocation(game).getName()); 
	}
	

}
