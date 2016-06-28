package com.dickie.sidion.shared.order;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.VarString;

public class ConvertOrder extends OrderImpl{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConvertOrder(){
		super.addPrecursors(true, false, false, false, false, false, false);
	}
	
	public String validateOrder(Game game) {
		super.validateOrder(game);
	
		if (getHero(game) == null){
			return "No wizard is set";
		}
		if (getHero(game).getLevel() == 0){
			return getHero(game).getName() + " is " + getHero(game).getLevel() + "; they must be at least level 1";
		}
		if (game.getGameState() == game.MAGIC_PHASE){
			VarString vs = (VarString) precursors.get("TYPE");
			if (vs == null){
				return "No type to convert mana to";
			}
			if (!(vs.getValue().equals("GOLD") || vs.getValue().equals("INF"))){
				return "Invalid type " + precursors.get("TYPE").getKey();
			}
			if (getPlayer(game).getResource(vs.getValue()) - 3 + getHero(game).getLevel() < 0){
				return "insufficient mana to do transfer; need " + (4 - getHero(game).getLevel());
			}
		}
		return null;
	}
	
	@Override
	public void execute() {
		super.execute(); 
	}
	
	@Override
	public boolean isExecutable(Game game, Player player) {
		if (player.isAdmin()){
			return false;
		}
		if (game.getGameState() == game.ORDER_PHASE){
			return true;
		}
		if (game.getGameState() == game.MAGIC_PHASE && player.equals(game.getCurrentPlayer())){
			addDoOrderParams();
			return true;
		}
		return false;
	}

	@Override
	public void addDoOrderParams() {
		precursors.put("TYPE", new VarString());
	}
	
	@Override
	public void executeOnServer(Game game){
		game.addGameComponent(this);
		getHero(game).setOrder(true);
		if (game.getGameState() == game.ORDER_PHASE){
			return;
		}
		String rType = precursors.get("TYPE").getKey();
		getPlayer(game).addResource("MANA", getHero(game).getLevel().intValue()-4);
		getPlayer(game).addResource(rType, 4-getHero(game).getLevel().intValue());
	}

}
