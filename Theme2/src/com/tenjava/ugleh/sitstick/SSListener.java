package com.tenjava.ugleh.sitstick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SSListener implements Listener {
	public Map<String, List<Entity>> sitStickList = new HashMap<String, List<Entity>>(); //List for storing the entities selected per player.
	@EventHandler
	public void SitStickUsedEvent(EntityDamageByEntityEvent event){
		Entity e = event.getDamager();
		if(e instanceof Player){ //Player hitting an entity.
			Player p = (Player)e;
			if((p.getItemInHand() != null) && p.getItemInHand().equals(SitStick.sitStickItem)){//NPE check, and then checks if stick in hand.
				event.setCancelled(true); //SitStick doesn't cause damage.
				addToSitStickList(p, event.getEntity()); //Damager, Selection
			}
		}
	}
	
	@EventHandler
	public void PlayerInteract(PlayerInteractEvent event){
		
	}
/*
 * addToSitStickList
 * Adds to the players selection list.
 * Player p
 * The player who is selecting an entity to use.
 * Entity selection
 * The Selection thats going to be used.
*/	
	private void addToSitStickList(Player p, Entity selection) {
	String playerName = p.getName();
	List<Entity> tempPlayerSelections = new ArrayList<Entity>();
	if(sitStickList.containsKey(playerName)){//Player already has a selection made.
		tempPlayerSelections = sitStickList.get(playerName);
	}else{//Create new sitStickList for playerName
		tempPlayerSelections = new ArrayList<Entity>(); 
	}
	if(tempPlayerSelections.size() == 0){
		tempPlayerSelections.add(selection);
		String entityName = getEntityName(selection);
		p.sendMessage(SitStick.msgP + "Passenger Set: " + ChatColor.LIGHT_PURPLE + entityName);
	}else if(tempPlayerSelections.size() == 1){
		if(tempPlayerSelections.get(0).getEntityId() != selection.getEntityId()){ //Can't have the same Passenger as the Vehicle.
			tempPlayerSelections.add(selection);
			String entityName = getEntityName(selection);
			p.sendMessage(SitStick.msgP + "Vehicle Set: " + ChatColor.LIGHT_PURPLE + entityName);
			setPassenger(tempPlayerSelections);
		}else{
			p.sendMessage(SitStick.msgP + "Can't have the same Passenger as its Vehicle.");
		}
	}
	sitStickList.put(playerName, tempPlayerSelections); //Adding new selection to the overall list.
	
	}
	
/*
 * String getEntityName
 * Better way at getting an entities name.
 * Entity selection
 * The entity we are getting the name from.
*/	

private String getEntityName(Entity selection) {
	if(selection instanceof Player){
		return ((Player)selection).getDisplayName();
	}else{
		return selection.getType().getName().toLowerCase().replace("_", " ");
	}
}

/*
 * setPassenger
 * Sets the passenger of the vehicle using entityList as the passenger and vehicle.
 * List<Entity> entityList
 * Index 0 should be the Passenger where Index 1 should be the Vehicle.
*/	

private void setPassenger(List<Entity> entityList) {
	Entity passenger = entityList.get(0);
	Entity vehicle = entityList.get(1);
	vehicle.eject(); //Remove current Passenger.
	vehicle.setPassenger(passenger); //Set Passenger.
}
	
	
}
