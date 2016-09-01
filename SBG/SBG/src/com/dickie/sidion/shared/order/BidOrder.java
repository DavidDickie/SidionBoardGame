package com.dickie.sidion.shared.order;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Var;

public class BidOrder extends OrderImpl {
	
	public BidOrder(){
		super.addPrecursors(false, false, false, false, false, false, false);
	}
	
	public String validateOrder(Game game) {
		
		super.validateOrder(game);
		if (checkForHero(game) != null){
			return checkForHero(game);
		}

		if (game.getGameState() == game.RETREAT){
			int gold = ((Var)getPrecursors().get("GOLD")).getValue();
			if (gold > this.getPlayer(game).getResource("GOLD")){
				return "Insufficient gold";
			}
			int mana = ((Var)getPrecursors().get("MANA")).getValue();
			if (mana > this.getPlayer(game).getResource("MANA")){
				return "Insufficient mana";
			}
			int inf = ((Var)getPrecursors().get("INF")).getValue();
			if (inf > this.getPlayer(game).getResource("INF")){
				return "Insufficient influence";
			}
		}
		return null;
	}
	
	@Override
	public boolean isExecutable(Game game) {
		if (game.getGameState() == game.ORDER_PHASE){
			return true;
		}
		if (game.getGameState() == game.RETREAT ){
			addDoOrderParams();
			return true;
		}
		return false;
	}

	@Override
	public void addDoOrderParams() {
		precursors.put("MANA", new Var());
		precursors.put("GOLD", new Var());
		precursors.put("INF", new Var());
	}
	
	@Override
	public void executeOnServer(Game game){
		getPlayer(game).addResource("ARTIFACTS", 1);
	}

}
