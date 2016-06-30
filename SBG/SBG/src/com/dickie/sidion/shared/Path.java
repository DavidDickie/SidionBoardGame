package com.dickie.sidion.shared;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.google.gwt.user.client.rpc.IsSerializable;

import com.dickie.sidion.client.SBG;

public class Path extends GameComponentImpl{
	
	public Path(){
		validAttributes = Arrays.asList("LKEY", "PLAYER", "TOWN1", "TOWN2", "BLOCKED");
	}
	
	public Town getTown1(Game game) {
		return game.getTown(getValue("TOWN1"));
	}

	public void setTown1(Town town1) {
		setValue("TOWN1", town1.getKey());
	}

	public Town getTown2(Game game) {
		return game.getTown(getValue("TOWN2"));
	}

	public void setTown2(Town town2) {
		setValue("TOWN2", town2.getKey());
	}

	public Boolean getBlocked() {
		return Boolean.valueOf(getValue("BLOCKED"));
	}

	public void setBlocked(Boolean blocked) {
		setValue("BLOCKED", blocked.toString());
	}

	public Town firstTown(Game game){
		return getTown1(game);
	}
	
	public Town secondTown(Game game){
		return getTown2(game);
	}
	
	public static Map<String,Path> createPath(Game game){
		Map<String,Path> paths = new HashMap<String,Path>();
		int square = (int) java.lang.Math.sqrt(game.getTowns().size());
		Town[][] towns = new Town[square][square];
		int x = 0;
		int y = 0;
		for (Town t: game.getTowns()){
			x = (t.getX() - 100)/100;
			y = (t.getY() - 100)/100;
			towns[x][y] = t;
			if (x == square){
				x = 0;
				y++;
			}
			
		}
		for (x = 0; x < square; x++){
			for (y = 0; y < square; y++){
				Path path = new Path();
				if (x != 0){ 
					path.setTown1(towns[x][y]);
					path.setTown2(towns[x-1][y]);
					path.setKey(x + "_" + y + "a");
					paths.put(path.getKey(), path);
				}
				path = new Path();
				if (y != 0){
					path.setTown1(towns[x][y]);
					path.setTown2(towns[x][y-1]);
					path.setKey(x + "_" + y + "b");
					paths.put(path.getKey(), path);
				}
			}
		}
		return paths;
		
	}
	
	public static Path getPath(Town t1, Town t2, Game game){
		for (Path p : game.getPaths()){
			if (p.getTown1(game) == t1 && p.getTown2(game) == t2){
				return p;
			}
			if (p.getTown1(game) == t2 && p.getTown2(game) == t1){
				return p;
			}
		}
		return null;
	}

}
