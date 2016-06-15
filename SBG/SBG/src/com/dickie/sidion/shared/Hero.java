package com.dickie.sidion.shared;

import java.util.HashMap;
import java.util.Map;

public class Hero implements GameComponent, java.io.Serializable{
	
	public Hero() {
	};

	@Override
	public String toString() {
		return "Hero [name=" + name + ", level=" + level + ", owner=" + owner
				+ ", location=" + location + "]";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return name;
	}


	public String getLocation() {
		// TODO Auto-generated method stub
		return location;
	}

	public String getOwner() {
		// TODO Auto-generated method stub
		return owner;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	private String name;
	private Integer level = 0;
	private String owner = "";
	private String location = "";
	private Boolean isPrince = false;
	
	public Boolean getIsPrince() {
		return isPrince;
	}

	public void setIsPrince(Boolean isPrince) {
		this.isPrince = isPrince;
	}

	@Override
	public Town location(Game game) {
		return game.getTown(location);
	}

	@Override
	public Player owner(Game game) {
		// TODO Auto-generated method stub
		return game.getPlayer(owner);
	}

	@Override
	public void setValue(String field, Object value) {
		if (field.equals("NAME")){
			name = value.toString();
		} else if (field.equals("LEVEL")){
			level = Integer.valueOf(value.toString());
		} else if (field.equals("OWNER")){
			owner = value.toString();
		}else if (field.equals("LOCATION")){
			location = value.toString();
		}
		
	}

	@Override
	public void selected() {
		System.out.println(getName() + " has been seleced");
		
	}
	
	public static Map<String,Hero> createHeros(Game game){
		Map<String,Hero> heros = new HashMap<String,Hero>();
		int count = 0;
		int count2 = 0;
		int playerCount = 0;
		int split = game.getTowns().size()/game.getPlayers().size();
		for (Town t: game.getTowns()){
			if (count == 9){
				count = 0;
				Hero hero = new Hero();
				hero.setName("Prince_" + count2++);
				hero.setLevel(3);
				hero.setOwner(game.getPlayers().toArray(new Player[0])[playerCount].getName());
				hero.setIsPrince(true);
				hero.setLocation(t.getName());
				heros.put(hero.getName(), hero);
				hero = new Hero();
				hero.setName("Hero_" + count2++);
				hero.setLevel(0);
				hero.setOwner(game.getPlayers().toArray(new Player[0])[playerCount].getName());
				hero.setIsPrince(true);
				hero.setLocation(t.getName());
				heros.put(hero.getName(), hero);
				playerCount++;
				
			}
			count++;
		}
		return heros;
		
	}

}
