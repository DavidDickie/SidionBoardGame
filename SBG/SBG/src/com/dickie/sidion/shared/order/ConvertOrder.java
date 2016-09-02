package com.dickie.sidion.shared.order;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.Var;
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
		if (game.getGameState() == Game.MAGIC_PHASE){
			VarString vs = (VarString) precursors.get("TYPE");
			Var numToConvert = (Var) precursors.get("NUM_TO_CONVERT");
			if (numToConvert == null || numToConvert.getKey() == null){
				return "No number to convert set";
			}
			int iNumToConvert = numToConvert.getValue();
			if (vs == null){
				return "No type to convert mana to";
			}
			if (!(vs.getValue().equals("GOLD") || vs.getValue().equals("INF"))){
				return "Invalid type " + precursors.get("TYPE").getKey();
			}
			if (getHero(game).getOwner(game).getMana() < iNumToConvert){
				return "insufficient mana to do transfer; need " + (int) (ratio(game)*iNumToConvert);
			}
		}
		return null;
	}
	
	private double ratio(Game game){
		if (getHero(game).getLevel() == 3){
			return 0.5;		
		} else if  (getHero(game).getLevel() == 2){
			return 0.333;		
		} else {
			return 0.25;		
		} 
	}
	
	public void addType(String type){
		VarString vs = new VarString();
		vs.setValue(type);
		precursors.put("TYPE", vs);
	}
	
	public void addAmountToConvert(int amount){
		Var v = new Var();
		v.setValue(amount);
		precursors.put("NUM_TO_CONVERT", v);
	}
	
	@Override
	public void execute() {
		super.execute(); 
	}
	
	@Override
	public boolean isExecutable(Game game) {
		if (game.getGameState() == Game.ORDER_PHASE){
			return true;
		}
		if (game.getGameState() == Game.MAGIC_PHASE){
			addDoOrderParams();
			return true;
		}
		return false;
	}

	@Override
	public void addDoOrderParams() {
		precursors.put("TYPE", new VarString());
		precursors.put("NUM_TO_CONVERT", new Var());
	}
	
	@Override
	public void executeOnServer(Game game){
		if (game.getGameState() == Game.ORDER_PHASE){
			return;
		}
		String rType = precursors.get("TYPE").getKey();
		int numToConvert = Integer.parseInt(precursors.get("NUM_TO_CONVERT").getKey());
		int converted = (int) (numToConvert*ratio(game));
		game.addMessage(getHero(game).getName() + " [" + 
				getPlayer(game).getName() + "] converted " + numToConvert + " mana into " + converted + " " + rType);
		getPlayer(game).addResource("MANA", -numToConvert);
		getPlayer(game).addResource(rType, converted);
	}

}
