package com.dickie.sidion.shared;


public interface GameComponent {
	public String getKey();
	public Town location(Game game);
	public Player owner(Game game);
	public void setValue(String field, Object value);
	public void selected();
}

