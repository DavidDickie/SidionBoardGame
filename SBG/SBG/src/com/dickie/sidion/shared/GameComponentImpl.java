package com.dickie.sidion.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.Set;

public class GameComponentImpl implements GameComponent, java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public String toString() {
		return getClass().getName() + attributes;
	}
	
	@Override
	public String getKey() {
		return getValue("LKEY");
	}
	
	@Override
	public void setKey(String key) {
		setValue("LKEY", key);
	}


	@Override
	public Player getOwner(Game game) {
		Player p =  game.getPlayer(getValue("PLAYER"));
		if (p == null){
			throw new RuntimeException ("There is no player " + getValue("PLAYER") + "; " +
					game.getPlayers().toArray());
		}
		return p;
	}
	
	@Override
	public void setOwner(Player p) {
		setValue("PLAYER", p.getKey());
	}

	@Override
	public void setValue(String field, String value) {
		if (!validAttributes.contains(field)){
			throw new RuntimeException(field + " is not a valid field for " + this.getClass().getName());
		}
		attributes.put(field, value);
	}

	@Override
	public void selected() {
		for (GameComponentListener o : observers){
			o.componentEvent("SELECTED", this);
		}
	}
	
	public void selectedForOrder(){
		for (GameComponentListener o : observers){
			o.componentEvent("SELECTED_FOR_ORDER", this);
		}
	}

	@Override
	public String getValue(String field) {
		if (!validAttributes.contains(field)){
			throw new RuntimeException(field + " is not a valid field for " + this.getClass().getName());
		}
		return attributes.get(field);
	}
	
	public List<String> getKeys(){
		return validAttributes;
	}
	
	
	protected Map<String, String> attributes = new HashMap<String, String>();
	protected List<String> validAttributes = new ArrayList<String>();
	protected List<GameComponentListener> observers = new ArrayList<GameComponentListener>();
	@Override
	public void addObserver(GameComponentListener o) {
		observers.add(o);
	}

	@Override
	public Set<String> keySet() {
		return attributes.keySet();
	}

	@Override
	public List<String> values() {
		return new ArrayList<String>(attributes.values());
	}
}
