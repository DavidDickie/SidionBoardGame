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
	public Player getPlayer(Game game);
	
	
	public String validateOrder(Game game);
	public void execute();
	public void executeOnServer(Game game);
	public void setPrecursors(String s, Game game);
	public String getPrecursorsAsString();
	public boolean isExecutable(Game game, Player player);
	public void addDoOrderParams();
}
