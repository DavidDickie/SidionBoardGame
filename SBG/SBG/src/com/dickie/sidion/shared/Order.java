package com.dickie.sidion.shared;

import java.util.Map;

public interface Order extends GameComponent{
	public Map<String, GameComponent> getPrecursors();
	public void setX( int x);
	public void setY(int x);
	public void setTown(Town gc);
	public void setNumber(int x);
	public void setHero(Hero h);
	public void setPath(Path gc);
	
	public int getX();
	public int getY();
	public Town getTown();
	public int getNumber();
	public Hero getHero();
	public Path getPath();
	
	
	public String validateOrder();
	public void execute();
	public void setPrecursors(String s, Game game);
	public String getPrecursorsAsString();
}
