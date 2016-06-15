package com.dickie.sidion.shared;

public class Var implements GameComponent{

	String key; 
	@Override
	public String getKey() {
		return key;
	}
	
	public void setKey(String key){
		this.key = key;
	}

	@Override
	public Town location(Game game) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Player owner(Game game) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValue(String field, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void selected() {
		// TODO Auto-generated method stub
		
	}

}
