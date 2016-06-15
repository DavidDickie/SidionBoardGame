package com.dickie.sidion.shared;

public class Resource implements GameComponent, java.io.Serializable{
	
	public Resource(){
		
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
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
	
	String type;
	Integer number;
	Player owner;

}
