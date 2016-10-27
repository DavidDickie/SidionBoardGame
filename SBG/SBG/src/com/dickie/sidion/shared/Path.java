package com.dickie.sidion.shared;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Path extends GameComponentImpl{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
		Path path = new Path();
		path.setTown1(towns[0][0]);
		path.setTown2(towns[1][1]);
		path.setKey("0_0c");
		paths.put(path.getKey(), path);
		
		path = new Path();
		path.setTown1(towns[square-1][0]);
		path.setTown2(towns[square-2][1]);
		path.setKey((square-1) + "_" + 0 + "c");
		paths.put(path.getKey(), path);
		
		path = new Path();
		path.setTown1(towns[square-1][square-1]);
		path.setTown2(towns[square-2][square-2]);
		path.setKey((square-1) + "_" + (square-1) + "c");
		paths.put(path.getKey(), path);
		
		path = new Path();
		path.setTown1(towns[0][square-1]);
		path.setTown2(towns[1][square-2]);
		path.setKey(0 + "_" + (square-1) + "c");
		paths.put(path.getKey(), path);
		
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
		System.out.println("Could not find path from " + t1.getName() + " to " + t2.getName());
		return null;
	}

}
