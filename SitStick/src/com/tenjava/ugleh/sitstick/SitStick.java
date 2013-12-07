package com.tenjava.ugleh.sitstick;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class SitStick extends JavaPlugin{
	public static Plugin main;
	//Store the sitStickItem so we can call it later and give it to a player easily.
	public static ItemStack sitStickItem;
	//I always create a prefix string, makes it cleaner when sending messages.
	public static String msgP = ChatColor.YELLOW + "[" + ChatColor.LIGHT_PURPLE + "SitStick" + ChatColor.YELLOW + "] " + ChatColor.DARK_RED;
	public  void onEnable(){
		main = this;
		createSitStick(); //Making onEnable clean so the stick creation is done in its own function.
		getServer().getPluginManager().registerEvents(new SSListener(), this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 * I am using onCommand for the sitstick command.
	 */
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if(cmd.getName().equalsIgnoreCase("sitstick")){
    		if(sender instanceof Player){//Player only command.
    			Player p = (Player)sender;
    			if(p.hasPermission("sitstick.use")){ //Check Permission
        			p.sendMessage(msgP + ChatColor.DARK_PURPLE + "Select your passanger first and then its vehicle by hitting them with the stick.");
        			p.sendMessage(msgP + ChatColor.LIGHT_PURPLE + "Right click to clear selections.");
        			p.sendMessage(msgP + ChatColor.DARK_PURPLE + "Select yourself hit the block underneath you.");
        			giveSitStick(p); //Give the player the Sit Stick.
    			}else{//Doesn't have the right permissions.
    				p.sendMessage(msgP + "You do not have permission to use that command.");
    			}
    		}else{//Just to prevent errors, prevent the console from doing this command.
    			sender.sendMessage(msgP + "Must be a in game to use this command");
    		}
    		return true; 
    	}
    	return false; 
    }
/*
 * giveSitStick
 * Gives the player the Sit Stick.
 * Player p
 * Player to give the stick to.
 */
	private void giveSitStick(Player p) {
		if(p.getInventory().contains(sitStickItem)){
			p.getInventory().remove(sitStickItem); //The user may have put the stick inside his inventory somewhere, so just remove the old one.
		}
		Inventory i = p.getInventory();
		if(i.firstEmpty() != -1){ //Inventory is not full, give them the Sit Stick.
			p.getInventory().addItem(sitStickItem);
		}else{ //Inventory is full, display an error message.
			p.sendMessage(msgP + "Your inventory is too full to obtain the Sit Stick.");
		}
	}
	
	/*
	 * createSitStick
	 * Creates the stick onEnable so we do not have to do it every time the player needs one.
	 */
	
	private void createSitStick() {
		sitStickItem = new ItemStack(Material.STICK, 1);
		ItemMeta sitStickMeta = sitStickItem.getItemMeta();
		sitStickMeta.setDisplayName(ChatColor.RED + "Sit Stick");
		List<String> sitStickLore = new ArrayList<String>();
		sitStickLore.add(ChatColor.LIGHT_PURPLE + "First hit selects a Passenger.");
		sitStickLore.add(ChatColor.LIGHT_PURPLE + "Second hit Selects its Vehicle.");
		sitStickLore.add(ChatColor.DARK_PURPLE + "Select yourself by selecting under you.");
		sitStickLore.add(ChatColor.DARK_RED + "Right click to clear selections.");
		sitStickMeta.setLore(sitStickLore);
		sitStickItem.setItemMeta(sitStickMeta);
	}
}
