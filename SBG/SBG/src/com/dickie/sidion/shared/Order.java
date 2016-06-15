package com.dickie.sidion.shared;

import java.util.HashMap;
import java.util.Map;


public class Order implements java.io.Serializable {
	
	public Order(){
		
	}

	protected Map<String, GameComponent> precursors = new HashMap<String, GameComponent>();
	protected String orderType;
	private String gameName;

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public Map<String, GameComponent> precursorFetch() {
		return precursors;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public Order(String type) {
		orderType = type;
		if (type.equals("Move")) {
			precursors.put("_Hero", new Hero());
			precursors.put("Move to", new Town());
		} else if (type.equals("Block")) {
			precursors.put("_Hero", new Hero());
			precursors.put("Path", new Town());
		} else if (type.equals("Block")) {
			precursors.put("_Hero", new Hero());
			precursors.put("Path", new Town());
		} else if (type.equals("Produce")) {
			precursors.put("_Hero", new Hero());
		} else if (type.equals("Transmut")) {
			precursors.put("_Hero", new Hero());
			precursors.put("Change to", new Resource());
		} else {
			throw new RuntimeException("Unknown converter " + type);
		}
	}

	public String getOrder() {
		return toString();
	}

	public void setOrder(String s) {
		Order c = Order.valueOf(s);
		this.orderType = c.orderType;
		this.precursors = c.precursors;
	}

	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append(orderType);
		for (String key : precursors.keySet()) {
			GameComponent gc = precursors.get(key);
			sb.append("|").append(gc.getClass()).append(";").append(key).append(";").append(gc.getKey());
		}

		return sb.toString();

	}

	public static Order valueOf(String s) {
		String[] strings = s.split("|");
		Order c = new Order(strings[0]);
		for (int i = 1; i < strings.length; i++) {
			String[] strings2 = strings[i].split(";");
			if (strings2[0].equals("x")) {

				// precursors.add( the x from with the right key);

			}

		}

		return c;

	}

	public String validateOrder() {
		if (gameName == null){
			return "no game name";
		}
		if (precursors.get("_player") == null) {
			return "No player set";
		}

		if (!orderType.equals("Transmute") && !orderType.equals("Bid")) {
			if (precursors.get("_hero") == null) {
				return "No hero set";
			}
		}

		if (orderType.equals("Edit")) {
			return validateEdit();
		}

		return null;
	}

	private String validateEdit() {

		Town t = (Town) precursors.get("_town");
		if (t == null)
			return "no town";
		if (precursors.get("_x") == null || precursors.get("_y") == null) {
			return "No coordinates to put town";
		}
		return null;

	}

}