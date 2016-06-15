package com.dickie.sidion.client;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Path;
import com.dickie.sidion.shared.Town;

public class Draw {
	
	MapPanel mp = null;
	Game game = null;
	
	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public MapPanel getMp() {
		return mp;
	}

	public void setMp(MapPanel mp) {
		this.mp = mp;
	}

	int defaultSize = 40;
	
	public void drawMap(){
		
		for (Path p : game.getPaths()) {
			Utils.logMessage("Path " + p);
			draw(p);
		}
		for (Town t : game.getTowns()) {
			Utils.logMessage("Town " + t);
			draw(t);
		}
	}
	
	public void draw(Town t){
		mp.setFillColor("#1111111");
		mp.setLineColor("red");
		if (t.isLocked()){
			mp.setFillColor(t.owner(game).getColor());
		}
		mp.drawRec(t.getX(), t.getY(), defaultSize, t.getName(), t);
	}
	
	public void draw(Path p){
		if (p.getBlocked()){
			mp.setLineColor("red");
			mp.setFillColor("red");
		} else {
			mp.setLineColor("yellow");
			mp.setFillColor("yellow");
		}
		mp.drawPath(p.firstTown(game).getX(), 
				p.firstTown(game).getY(),
				p.secondTown(game).getX(),
				p.secondTown(game).getY(),
				p);
	}

}
