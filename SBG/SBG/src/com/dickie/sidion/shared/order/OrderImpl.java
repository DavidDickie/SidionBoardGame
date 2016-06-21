package com.dickie.sidion.shared.order;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.GameComponentImpl;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Order;
import com.dickie.sidion.shared.Path;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.Resource;
import com.dickie.sidion.shared.Town;
import com.dickie.sidion.shared.Var;


public class OrderImpl extends GameComponentImpl implements Order {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return  getClass().getName() + this.getPrecursorsAsString();
	}

	public OrderImpl(){
		validAttributes = Arrays.asList("PLAYER", "KEY", "TYPE", "PRECURSORS");	
		setValue("TYPE", getClass().getName());
	}

	protected Map<String, GameComponent> precursors = new HashMap<String, GameComponent>();
	
	public Map<String, GameComponent> precursorFetch() {
		return precursors;
	}

	public String getPrecursorsAsString() {

		StringBuffer sb = new StringBuffer();
		for (String key : precursors.keySet()) {
			GameComponent gc = precursors.get(key);
			sb.append(gc.getClass()).append(";").append(key).append(";").append(gc.getKey()).append("|");
		}

		return sb.toString();

	}
	
	public void setPrecursors(String s, Game game){
		String[] strings = s.split("\\|");
		for (int i = 0; i < strings.length; i++) {
			String[] param = strings[i].split(";");
			param[0] = param[0].split(" ")[1];
			if (param[0].equals(Town.class.getName())){
				precursors.put("TOWN", game.getTown(param[2]));
			} else if (param[0].equals(Hero.class.getName())){
				precursors.put("HERO", game.getHero(param[2]));
			} else if (param[0].equals(Path.class.getName())){
				precursors.put("PATH", game.getHero(param[2]));
			} else if (param[0].equals(Var.class.getName())){
				Var v = new Var();
				v.setKey(param[2]);
				precursors.put(param[1], v);
			} else {
				throw new RuntimeException("Could not figure out what to do with " + strings[i] + " for " + this);
			}
		}
	}

	public String validateOrder(Game game) {
		if (getValue("PLAYER") == null) {
			return "No player set";
		}
		if (getValue("PRECURSORS") == null){
			return "No parameters for order";
		}
		return null;
	}

	@Override
	public Map<String, GameComponent> getPrecursors() {
		return precursors;
	}

	@Override
	public void setX(int x) {
		Var var = new Var();
		var.setValue(x);
		precursors.put("X", var);
	}
	
	public int getX(){
		return ((Var)precursors.get("X")).getValue();
	}

	@Override
	public void setY(int y) {
		Var var = new Var();
		var.setValue(y);
		precursors.put("Y", var);
	}
	
	public int getY(){
		return ((Var)precursors.get("Y")).getValue();
	}
	
	@Override
	public void setTown(Town gc) {
		precursors.put("TOWN", gc);		
	}
	
	@Override
	public Town getTown() {
		return (Town) precursors.get("TOWN");
	}


	@Override
	public void setNumber(int x) {
		Var var = new Var();
		var.setValue(x);
		precursors.put("NUMBER", var);
	}
	
	public int getNumber(){
		return ((Var)precursors.get("NUMBER")).getValue();
	}

	@Override
	public void setHero(Hero h) {
		precursors.put("HERO", h);
	}
	
	@Override
	public Hero getHero() {
		return (Hero) precursors.get("HERO");
	}

	@Override
	public void setPath(Path gc) {
		precursors.put("PATH", gc);	
	}
	
	@Override
	public Path getPath(){
		return (Path) precursors.get("PATH");
	}
	
	@Override
	public void execute() {
		this.setValue("PRECURSORS", this.getPrecursorsAsString());
		precursors = new HashMap<String, GameComponent>();
	}
	
	public void addPrecursors(boolean hero, boolean town, boolean path, boolean resource, boolean x, boolean y, boolean number){
		if (hero) precursors.put("HERO", new Hero());
		if (town) precursors.put("TOWN", new Town());
		if (path) precursors.put("PATH", new Path());
		if (resource) precursors.put("RESOURCE", new Resource());
		if (x) precursors.put("X", new Var());
		if (y) precursors.put("Y", new Var());
		if (number) precursors.put("NUMBER", new Var());
	
	}

	@Override
	public boolean isExecutable(Game game, Player player){
		return false;
	}

	@Override
	public void addDoOrderParams() {
		return;
	}



}