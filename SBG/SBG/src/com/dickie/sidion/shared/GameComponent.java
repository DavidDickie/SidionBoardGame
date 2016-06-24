package com.dickie.sidion.shared;

import java.util.List;
import java.util.Observer;
import java.util.Set;

public interface GameComponent {
	public String getKey();
	public List<String> getKeys();
	public void setKey(String key);
	public Player getOwner(Game game);
	public void setOwner(Player player);
	public void setValue(String field, String value);
	public String getValue(String field);
	public Set<String> keySet();
	public List<String> values();
	public void selected();
	public void addObserver(GameComponentListener o);
}

