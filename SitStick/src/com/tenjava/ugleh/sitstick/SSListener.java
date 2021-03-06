package com.tenjava.ugleh.sitstick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SSListener implements Listener {
	public Map<String, List<Entity>> sitStickList = new HashMap<String, List<Entity>>(); //List for storing the entities selected per player.
	@EventHandler
	
	/*
	 * SitStickUsedEvent
	 * Being used to check which entities are being selected by the player.
	 */
	public void SitStickUsedEvent(EntityDamageByEntityEvent event){
		Entity e = event.getDamager();
		if(e instanceof Player){ //Player hitting an entity.
			Player p = (Player)e;
			if(p.hasPermission("sitstick.use")){ //Check Permission
				if((p.getItemInHand() != null) && p.getItemInHand().equals(SitStick.sitStickItem)){//NPE check, and then checks if stick in hand.
					event.setCancelled(true); //SitStick doesn't cause damage.
					addToSitStickList(p, event.getEntity()); //Damager, Selection
				}
			}else{//Doesn't have the right permissions.
				p.sendMessage(SitStick.msgP + "You do not have permission to use the Sit Stick.");
				p.getInventory().remove(SitStick.sitStickItem); //No Permission, just remove the stick from them.
			}
		}
	}
	
	
	/*
	 * PlayerInteract
	 * Being used to check if a player is selecting themselves, or clearing the selection list.
	 */
	@EventHandler
	public void PlayerInteract(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(p.hasPermission("sitstick.use")){ //Check Permission
			if((p.getItemInHand() != null) && p.getItemInHand().equals(SitStick.sitStickItem)){//NPE check, and then checks if stick in hand.
				event.setCancelled(true); //Item shouldn't interact normally.
				if((event.getAction() == Action.RIGHT_CLICK_AIR) || event.getAction() == Action.RIGHT_CLICK_BLOCK){ //Right Clicking clears list
					clearSelection(p, true);
				}else if(event.getAction() == Action.LEFT_CLICK_BLOCK){ //Left click checks block underneath
					if(event.getClickedBlock().getRelative(BlockFace.UP).getLocation().equals(p.getLocation().getBlock().getLocation())){ //The block they clicked is underneath them.
						addToSitStickList(p, ((Entity)p));	
						
					}
				}
			}
		}else{//Doesn't have the right permissions.
			p.sendMessage(SitStick.msgP + "You do not have permission to use the Sit Stick.");
			p.getInventory().remove(SitStick.sitStickItem); //No Permission, just remove the stick from them.
		}

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
	if(tempPlayerSelections.size() == 2){ //Time for a new selection.
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
			setPassenger(p, tempPlayerSelections);
			sitStickList.remove(p.getName());

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

@SuppressWarnings("deprecation") //1.7.2 New deprecation, but still works.
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
 * Player p
 * Not used ATM, may be used.
 * List<Entity> entityList
 * Index 0 should be the Passenger where Index 1 should be the Vehicle.
*/	

private void setPassenger(Player p, List<Entity> entityList) {
	Entity passenger = entityList.get(0);
	Entity vehicle = entityList.get(1);
	if((passenger.getPassenger() != null) && (passenger.getPassenger().getEntityId() == vehicle.getEntityId())){ //Bug prevention. Cant have previous passenger be vehicle.
		passenger.eject(); 
	}
	vehicle.setPassenger(passenger); //Set Passenger.
	vehicle.getWorld().playEffect(vehicle.getLocation(), Effect.EXTINGUISH, 1);
	p.playNote(p.getLocation(), Instrument.PIANO, Note.natural(1, Tone.A));
	clearSelection(p, false); //Clear their selection.

}

/*
 * clearSelection
 * Clears the players selection.
 * Player p
 * The player who is having their selections cleared.
*/	
private void clearSelection(Player p, boolean messagePlayer) {
	String playerName = p.getName();
	if(sitStickList.containsKey(playerName)){
		sitStickList.remove(playerName);
		if(messagePlayer)
			p.sendMessage(SitStick.msgP + "Selections Cleared.");
	}else{
		if(messagePlayer)
			p.sendMessage(SitStick.msgP + "No selection to clear.");
	}
	}

	
}
