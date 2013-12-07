package com.tenjava.ugleh.theme2;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	public static Plugin main;
	public  void onEnable(){
		main = this;
		getServer().getPluginManager().registerEvents(new MListener(), this);
	}
	
	public void onDisable(){
		
	}
	
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if(cmd.getName().equalsIgnoreCase("main")){
    		return true;
    	}
    	return false; 
    }


}
