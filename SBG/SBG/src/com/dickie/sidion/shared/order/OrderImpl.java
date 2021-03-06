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
import com.dickie.sidion.shared.VarString;


public abstract class OrderImpl extends GameComponentImpl implements Order {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		String s = getClass().getSimpleName() + " for " + getHeroAsString();
		return s;
	}

	public OrderImpl(){
		validAttributes = Arrays.asList("PLAYER", "LKEY", "TYPE", "PRECURSORS");	
		setValue("TYPE", getClass().getName());
	}

	protected Map<String, GameComponent> precursors = new HashMap<String, GameComponent>();
	
	public Map<String, GameComponent> precursorFetch() {
		return precursors;
	}

	public String getPrecursorsAsString() {
		if (precursors == null || precursors.size() == 0){
			return "No precursors";
		}
		StringBuffer sb = new StringBuffer();
		for (String key : precursors.keySet()) {
			GameComponent gc = precursors.get(key);
			if (gc == null){
				System.out.println("[could not find precursor for " + key + " on " + getClass().getSimpleName() + "]");
			} else {
				sb.append(gc.getClass()).append(";").append(key).append(";").append(gc.getKey()).append("|");
			}
		}

		return sb.toString();

	}
	
	public void setPrecursors(Game game){
		String s = getValue("PRECURSORS");
		if (s == null || s.equals("No precursors")){
			return;
		}
		String[] strings = s.split("\\|");
		for (int i = 0; i < strings.length; i++) {
			if (strings[i].length() < 1){
				continue;
			}
			String[] param = strings[i].split(";");
			param[0] = param[0].split(" ")[1];
			if (param[0].equals(Town.class.getName())){
				precursors.put(param[1], game.getTown(param[2]));
			} else if (param[0].equals(Hero.class.getName())){
				precursors.put(param[1], game.getHero(param[2]));
			} else if (param[0].equals(Path.class.getName())){
				precursors.put(param[1], game.getPath(param[2]));
			} else if (param[0].equals(Var.class.getName())){
				Var v = new Var();
				v.setKey(param[2]);
				precursors.put(param[1], v);
			}else if (param[0].equals(VarString.class.getName())){
				VarString v = new VarString();
				v.setValue(param[2]);
				precursors.put(param[1], v);
			} else {
				throw new RuntimeException("Could not figure out what to do with '" + strings[i] + "' for " + this);
			}
		}
		//attributes.remove("PRECURSORS");
	}
	
	public Player getPlayer(Game game){
		return game.getPlayer(getValue("PLAYER"));
	}
	
	public void setPlayer(Player player){
		setValue("PLAYER", player.getKey());
	}

	public String validateOrder(Game game) {
		try{
			if (getValue("PLAYER") == null) {
				return "No player set";
			}
			if (getValue("PRECURSORS") == null){
				return "No parameters for order";
			}
		} catch (Exception e) {
			return e.getMessage();
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
		setValue("LKEY", h.getName());
	}
	
	@Override
	public Hero getHero(Game game) {
		return game.getHero(getValue("LKEY"));
	}
	
	public String getHeroAsString(){
		return getValue("LKEY");
	}
	
	public String checkForHero(Game game){
		if (game.getHero(getValue("LKEY")) == null){
			if (getValue("LKEY")==null){
				return "No hero is set?";
			}
			return "Hero has been removed from game";
		}
		return null;
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
//		if (hero) precursors.put("HERO", new Hero());  don't need it, the key for an order is the hero
		if (town) precursors.put("TOWN", new Town());
		if (path) precursors.put("PATH", new Path());
		if (resource) precursors.put("RESOURCE", new Resource());
		if (x) precursors.put("X", new Var());
		if (y) precursors.put("Y", new Var());
		if (number) precursors.put("NUMBER", new Var());
	}
	
	public void clearAttrs(){
		precursors = new HashMap<String, GameComponent>();
	}

}