package com.dickie.sidion.client;

import java.util.List;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Path;
import com.dickie.sidion.shared.Town;

public class Draw {
	
	MapPanel mp = null;

	public MapPanel getMp() {
		return mp;
	}

	public void setMp(MapPanel mp) {
		this.mp = mp;
	}

	int defaultSize = 40;
	
	public void drawMap(Game game){
		try{
			mp.clear();
			mp.drawBackgroundImage();
			for (Path p : game.getPaths()) {
				draw(p, game);
			}
			for (Town t : game.getTowns()) {
				draw(t, game);
			}
		} catch (Throwable t){
			Utils.displayMessage(t.getMessage());
		}
	}
	
	public void draw(Town t, Game game){
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
		
		List<Hero> heros = t.getHeros(game);
		int number = heros.size() - 1;
		for (Hero h : heros){
			if (!h.isPrince()){
				mp.setLineColor("white");
			} else { 
				mp.setLineColor(h.getOwner(game).getColor()); 
			}	
			mp.setFillColor(h.getOwner(game).getColor());
			mp.drawCircle(t.getX() - defaultSize/2 * number/2, t.getY() + defaultSize, defaultSize/8, null, h);
			number--;
		}
	}
	
	public void draw(Path p, Game game){
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
