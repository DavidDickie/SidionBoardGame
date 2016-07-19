package com.dickie.sidion.shared.order;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Player;

public class RecruitOrder  extends OrderImpl {
	
	public RecruitOrder(){
		super.addPrecursors(true, false, false, false, false, false, false);
	}
	
	public String validateOrder(Game game) {
		Hero prince = getHero(game);
		super.validateOrder(game);
		if (prince == null){
			return "No hero is set";
		}
		if (!prince.isPrince()){
			return "Only princes may recruit";
		}
		if (getOwner(game).getResource("GOLD") < 1){
			return "Insufficient funds";
		}
		if (!prince.getLocation(game).hasHero()){
			return "There is no nuetral hero to recruit";
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
		hero.setLevel(1);
		hero.setLocation(this.getHero(game).getLocation(game));
		hero.setOwner(getOwner(game));
		getHero(game).getLocation(game).setHasHero(false);
		game.addGameComponent(hero);
		getOwner(game).addResource("GOLD", -1);
	}
	

}
