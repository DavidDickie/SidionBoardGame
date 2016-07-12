package com.dickie.sidion.shared.order;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Player;

public class RecruitOrder  extends OrderImpl {
	
	public RecruitOrder(){
		super.addPrecursors(true, false, false, false, false, false, false);
	}
	
	public String validateOrder(Game game) {
		
		super.validateOrder(game);
		if (getHero(game) == null){
			return "No hero is set";
		}
		if (!getHero(game).isPrince()){
			return "Only princes may recruit";
		}
		if (getOwner(game).getResource("GOLD") < 1){
			return "Insufficient funds";
		}
		if (getHero(game).getLocation(game).getHeros(game).size() > 1){
			return "You can only recruit in an empty town";
		}

		return null;
	}
	
	@Override
	public boolean isExecutable(Game game) {
		if (game.getGameState() == game.ORDER_PHASE){
			return true;
		}
		if (game.getGameState() == game.RETREAT){
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
		hero.setLevel(0);
		hero.setLocation(this.getHero(game).getLocation(game));
		hero.setOwner(getOwner(game));
		game.addGameComponent(hero);
		getOwner(game).addResource("GOLD", -1);
	}
	

}
