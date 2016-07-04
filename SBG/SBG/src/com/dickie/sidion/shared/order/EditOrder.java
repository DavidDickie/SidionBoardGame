package com.dickie.sidion.shared.order;


import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Order;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.Town;
import com.dickie.sidion.shared.VarString;

public class EditOrder extends OrderImpl implements Order {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EditOrder (){
		super.addPrecursors(false, true, false, false, true, true, true);
		precursors.put("EDIT_TYPE", new VarString());
	}
	
	public String validateOrder(Game game) {
		super.validateOrder(game);
		String orderType = ((VarString)precursors.get("EDIT_TYPE")).getValue();
		
		if (orderType == null || orderType.equals("") || orderType.equals("TOWN")){
				
			Town t = (Town) precursors.get("TOWN");
			if (t == null)
				return "no town";
			if (precursors.get("X").getKey()== null || precursors.get("Y").getKey() == null) {
				return "No coordinates to put town";
			}
			return null;
		}
		if (orderType.equals("GAMESTATE")){
			return null;
		}
		return "Invalid order type " + orderType;
	}
	
	@Override
	public void executeOnServer(Game game) {
		String orderType = ((VarString)precursors.get("EDIT_TYPE")).getValue();
		if (orderType == null || orderType.equals("") || orderType.equals("TOWN")){
			Town t = getTown();
			t.setX(getX());
			t.setY(getY());
		}
		if (orderType.equals("GAMESTATE")){
			game.setGameState(getNumber());
		}
		
	}
	
	@Override
	public boolean isExecutable(Game game) {
		return true;
	}

	@Override
	public void addDoOrderParams() {
		return;
	}
}
