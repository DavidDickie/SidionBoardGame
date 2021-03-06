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
			if (t.hasOwner(game)){
				t.setTempOwner(t.getOwner(game));
			} else {
				t.setTempOwner(null);
			}
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
//				System.out.println("Town " + t.getName() + " original owner is " + originalOwner);
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
				System.out.println("  There is combat :");
				for (Player tp: occupiers.keySet()){
					System.out.println("    " + tp.getName());
					for (Hero th : occupiers.get(tp)){
						System.out.println("      " + th);
					}
				}
				int maxPoints = -1;
				Player winner = null;
				boolean tie = false;
				
				for (Player curP : occupiers.keySet()){
					int curPoints = 0;
					List<Hero> list = occupiers.get(curP);
					for (Hero h2 : list){
						curPoints += h2.getLevel();
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
					game.addMessage("There was a combat in " + t.getName() +", the original owner " + winner.getName() + " wins on a tie");
				}
				if (!tie){
					game.addMessage("There was a combat in " + t.getName() +", " + winner.getName() + " wins with " + maxPoints + " combat points");
					for (Hero h2 : t.getHeros(game)){
						if (h2.getOwner(game).equals(winner)){
							// they are ok
						} else {
							if (h2.getOwner(game).getGold() == 0){
								game.addMessage(h2.getName() + " [" + h2.getOwner(game).getName() + "] cannot retreat (no gold) ... removed from game");
								game.removeGameComponent(h2);
							} else {
								Retreat ro = new Retreat();
								ro.setPlayer(h2.getOwner(game));
								ro.setHero(h2);
								if (ro.validateOrder(game) != null){
									game.addMessage(h2.getName() + " [" + h2.getOwner(game).getName() + "] cannot retreat; " + ro.validateOrder(game));
									game.removeGameComponent(h2);
									continue;
								}
								if (h2.getOwner(game).isNpc()){
									List<Town> towns = h2.getLocation(game).getNeighbors(game);
									for (Town t3 : towns){
										if (t3.getTempOwner(game) == null || t3.getTempOwner(game).equals(h2.getOwner(game))){
											ro.setTown(t3);
											System.out.println("Queued NPC retreat: " + ro);
											game.addGameComponent(ro);
											break;
										}
									}
								}
								game.addGameComponent(ro);
								h2.setMustRetreat(true);
							}		
						}
					}
				}
//				System.out.println("Game state after combat:\n" + game);
			} else {
//				System.out.println("There is no combat");
			}
		}
	}
	
	public void resetOrderOnConflict(Game game){
		// if we are in the retreat phase, don't do this, since retreat orders should not be eliminated
		if (game.getGameState() > Game.PHYS_PHASE){
			return;
		}
		ArrayList<Town> towns = new ArrayList<Town>(game.getTowns());
		for (Town t: towns){
			Map<Player, List<Hero>> occupiers = new HashMap<Player, List<Hero>>();
			ArrayList<Hero> heroes = new ArrayList<Hero>(t.getHeros(game));
			for (Hero h : heroes){
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
					if (!(game.getOrder(h.getName()) instanceof StandOrder)){
						game.addMessage(h.getName() + " [" + h.getOwner(game).getName() + "] orders lost due to conflict");
					}
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
			

			Player p = t.getTempOwner(game);
			if (p != null){
				StringBuffer sb = new StringBuffer();
				sb.append("Producing for " + t.getName() +"[" + p.getDisplayName() + "]: ");
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
				System.out.println(sb);
			} 
			
		}
	}

}
