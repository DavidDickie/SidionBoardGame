package com.dickie.sidion.client;

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
	
	public void draw(Town t){
		mp.setFillColor("#1111111");
		mp.setLineColor("red");
		if (t.isLocked()){
			mp.setFillColor(t.owner().getColor());
		}
		mp.drawRec(t.getX(), t.getY(), defaultSize, t.getName(), t);
	}

}
