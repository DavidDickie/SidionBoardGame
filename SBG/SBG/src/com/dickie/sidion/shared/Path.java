package com.dickie.sidion.shared;

import java.util.HashMap;
import java.util.Map;
import com.google.gwt.user.client.rpc.IsSerializable;

import com.dickie.sidion.client.SBG;

public class Path implements GameComponent, IsSerializable{
	
	public Path(){
		
	}

	@Override
	public String toString() {
		return "Path [town1=" + town1 + ", town2=" + town2 + ", key=" + key + ", blocked=" + blocked + "]";
	}

	@Override
	public String getKey() {
		return key;
	}
	
	public void setKey(String key){
		this.key = key;
	}

	@Override
	public Town location(Game game) {
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
		
	}
	
	public String getTown1() {
		return town1;
	}

	public void setTown1(String town1) {
		this.town1 = town1;
	}

	public String getTown2() {
		return town2;
	}

	public void setTown2(String town2) {
		this.town2 = town2;
	}

	private String town1;
	private String town2;
	private String key;
	private Boolean blocked = false;
	
	
	
	public Boolean getBlocked() {
		return blocked;
	}

	public void setBlocked(Boolean blocked) {
		this.blocked = blocked;
	}

	public Town firstTown(Game game){
		return game.getTown(town1);
	}
	
	public Town secondTown(Game game){
		return game.getTown(town2);
	}
	
	public static Map<String,Path> createPath(Game game){
		Map<String,Path> paths = new HashMap<String,Path>();
		int square = (int) java.lang.Math.sqrt(game.getTowns().size());
		Town[][] towns = new Town[square][square];
		int x = 0;
		int y = 0;
		for (Town t: game.getTowns()){
			towns[x++][y] = t;
			if (x == square){
				x = 0;
				y++;
			}
			
		}
		for (x = 0; x < square; x++){
			for (y = 0; y < square; y++){
				Path path = new Path();
				if (x != 0){ 
					path.setTown1(towns[x][y].getKey());
					path.setTown2(towns[x-1][y].getKey());
					path.setKey(x + "_" + y + "a");
					paths.put(path.getKey(), path);
				}
				path = new Path();
				if (y != 0){
					path.setTown1(towns[x][y].getKey());
					path.setTown2(towns[x][y-1].getKey());
					path.setKey(x + "_" + y + "b");
					paths.put(path.getKey(), path);
				}
			}
		}
		return paths;
		
	}

}
