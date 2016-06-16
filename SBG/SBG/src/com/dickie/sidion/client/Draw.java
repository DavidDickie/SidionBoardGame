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
		mp.clear();
		mp.drawBackgroundImage();
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
		mp.setFillColor("black");
		if (t.isLocked()){
			mp.setFillColor(t.getOwner(game).getColor());
		} else {
			mp.setLineColor("red");
		}
		mp.drawRec(t.getX(), t.getY(), defaultSize, t.getName(), t);
		mp.setLineColor("white");
		if (t.isLocked()){
			mp.setFillColor(t.getOwner(game).getColor());
		} else {
			mp.setFillColor("gray");
		}
		mp.drawCircle(t.getX(), t.getY(), defaultSize/4, null, t);
		int scaler = 8;
		
		if (t.getGold() == 1){
			mp.setFillColor("yellow");
			mp.setLineColor("white");
			mp.drawRec(t.getX() + 3*defaultSize/scaler, t.getY() - 3*defaultSize/scaler , (defaultSize+8)/scaler, null, null);
		}
		if (t.getMana() == 1){
			mp.setFillColor("red");
			mp.setLineColor("white");
			mp.drawRec(t.getX() - 3*defaultSize/scaler, t.getY() - 3*defaultSize/scaler , (defaultSize+8)/scaler, null, null);
		}
		if (t.getInf() == 1){
			mp.setFillColor("#8ED6EA");
			mp.setLineColor("white");
			mp.drawRec(t.getX() + 3*defaultSize/scaler, t.getY() + 3*defaultSize/scaler , (defaultSize+8)/scaler, null, null);
		}
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
