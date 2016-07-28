package com.dickie.sidion.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Order;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.Town;
import com.dickie.sidion.shared.order.Retreat;
import com.dickie.sidion.shared.order.StandOrder;

public class GameEngine {
	
	public void flagOriginalOwner(Game game){
		
		for (Town t : game.getTowns()){
			t.setTempOwner(null);
			for (Hero h : t.getHeros(game)){
				t.setTempOwner(h.getOwner(game));
			}
		}
	}
	
	public void resolveCombat(Game game){
		/*
		 * set the original owner before moves and teleports
		 * get the list of heros in a town
		 * if the town has heros from more than one player
		 *     determine who the original owner is
		 *     count up the points for each player
		 *     if there is  max, they win
		 *     if there is a tie
		 *         if the original owner is in the combat, they win
		 *         if the original owner has left, there is no winner
		 *     for each hero that is not owned by the winner
		 *         mark the hero as needing to retreat
		 *         see if the player has gold
		 *         see if there is a valid retreat spot
		 *         queue up a retreat order
		 */
		
		for (Town t : game.getTowns()){
			Player originalOwner = t.getTempOwner(game);
			if (originalOwner != null){
				System.out.println("Town " + t.getName() + " original owner is " + originalOwner);
			}
			Map<Player, List<Hero>> occupiers = new HashMap<Player, List<Hero>>();
			for (Hero h : t.getHeros(game)){
				List<Hero> hs = new ArrayList<Hero>();
				if (occupiers.containsKey(h.getOwner(game))){
					hs = occupiers.get(h.getOwner(game));
				}
				hs.add(h);
				occupiers.put(h.getOwner(game), hs);
			}
			if (occupiers.keySet().size() > 1){
				System.out.println("  There is combat");
				int maxPoints = -1;
				Player winner = null;
				boolean tie = false;
				
				for (Player curP : occupiers.keySet()){
					int curPoints = 0;
					List<Hero> list = occupiers.get(curP);
					for (Hero h2 : list){
						curPoints += h2.getLevel() + 1;
					}
					if (curPoints > maxPoints){
						winner = curP;
						maxPoints = curPoints;
						tie = false;
					} else if (curPoints == maxPoints){
						tie = true;
					}
				}
				if (tie && originalOwner != null && occupiers.containsKey(originalOwner)){
					tie = false;
					winner = originalOwner;
					System.out.println("There was a tie, original owner wins");
				}
				if (!tie){
					System.out.println("  Town was won by " + winner.getName() + " with " + maxPoints + " combat points");
					for (Hero h2 : t.getHeros(game)){
						if (h2.getOwner(game).equals(originalOwner)){
							// they are ok
						} else {
							if (h2.getOwner(game).getResource("GOLD") == 0){
								System.out.println("  Player " + h2.getOwner(game).getName() + " has Nn gold");
								game.removeGameComponent(h2);
							} else {
								Retreat ro = new Retreat();
								ro.setPlayer(h2.getOwner(game));
								ro.setHero(h2);
								if (ro.validateOrder(game) != null){
									System.out.println("  " + ro.validateOrder(game));
									game.removeGameComponent(h2);
									continue;
								}
								game.addGameComponent(ro);
								h2.setMustRetreat(true);
							}		
						}
					}
				}
				
			} else {
//				System.out.println("There is no combat");
			}
		}
	}
	
	public void resetOrderOnConflict(Game game){
		for (Town t: game.getTowns()){
			Map<Player, List<Hero>> occupiers = new HashMap<Player, List<Hero>>();
			for (Hero h : t.getHeros(game)){
				List<Hero> hs = new ArrayList<Hero>();
				if (occupiers.containsKey(h.getOwner(game))){
					hs = occupiers.get(h.getOwner(game));
				}
				hs.add(h);
				occupiers.put(h.getOwner(game), hs);
			}
			if (occupiers.keySet().size() > 1){
				// town has a conflict, set everyone to "stand"
				for (Hero h : t.getHeros(game)){
					Order o = new StandOrder();
					o.setOwner(h.getOwner(game));
					o.setHero(h);
					game.addGameComponent(o);
				}
			}
		}
		
	}
	
	public void produce(Game game){
		flagOriginalOwner(game);
		for (Town t: game.getTowns()){
			StringBuffer sb = new StringBuffer();
			sb.append("Producing for " + t.getName());
			Player p = t.getTempOwner(game);
			if (p != null){
				if (t.getGold() == 1){
					p.addResource("GOLD", 1);
					sb.append("GOLD ");
				}
				if (t.getMana() == 1){
					p.addResource("MANA", 1);
					sb.append("MANA ");
				}
				if (t.getInf() == 1){
					p.addResource("INF", 1);
					sb.append("INF ");
				}
				sb.append("Producted");
			} else {
				sb.append("  No Owner");
			}
			System.out.println(sb);
		}
	}

}
