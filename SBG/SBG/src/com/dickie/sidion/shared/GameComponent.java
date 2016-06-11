package com.dickie.sidion.shared;


public interface GameComponent {
	public String getKey();
	public Town location();
	public Player owner();
	public void setValue(String field, Object value);
	public void selected();
}

