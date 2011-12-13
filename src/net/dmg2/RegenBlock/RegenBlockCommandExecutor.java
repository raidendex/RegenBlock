package net.dmg2.RegenBlock;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegenBlockCommandExecutor implements CommandExecutor{

	//============================================================
	private RegenBlock plugin;
	public RegenBlockCommandExecutor(RegenBlock instance) {
		this.plugin = instance;
	}
	//============================================================
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//Make sure command was not sent from console
		if (sender instanceof Player == false){
			plugin.log.info("/regenblock is only available in game.");
			return true;
		}
		
		//If no arguments display the default help message
		if (args.length == 0) return false;
		//Convert sender to Player
		Player player = (Player) sender;

		
		//=============================================================================================================================
		//First argument - LIST
		if (args[0].equalsIgnoreCase("list")) {
			//Make sure both locations are recorded
			if (plugin.playerSelectionLeft.containsKey(player.getName()) && plugin.playerSelectionRight.containsKey(player.getName())) {
				//If both locations has been set - Message player
				String right = plugin.playerSelectionRight.get(player.getName()).getBlockX() + " " + plugin.playerSelectionRight.get(player.getName()).getBlockY() + " " + plugin.playerSelectionRight.get(player.getName()).getBlockZ();
				String left = plugin.playerSelectionLeft.get(player.getName()).getBlockX() + " " + plugin.playerSelectionLeft.get(player.getName()).getBlockY() + " " + plugin.playerSelectionLeft.get(player.getName()).getBlockZ();
				plugin.log.sendPlayerNormal(player, "Right: " + right + " Left: " + left);
			} else {
				//Warn Player if not both locations are set
				plugin.log.sendPlayerWarn(player, "You need to set both spots first");
			}			
			return true;
		}
		
		//=============================================================================================================================
		//First Argument - SELECT  
		if (args[0].equalsIgnoreCase("select")) {
			//Check if player's status
			if (plugin.playerSelectionStatus.contains(player.getName())) {
				//If it does - remove from the list - turn off the mode
				plugin.playerSelectionStatus.remove(player.getName());
				//Message player
				plugin.log.sendPlayerNormal(player, "Selection mode is OFF");
			} else {
				//If it does - remove from the list - turn off the mode
				plugin.playerSelectionStatus.add(player.getName());
				//Message player
				plugin.log.sendPlayerNormal(player, "Selection mode is ON");
			}
			return true;
		}

		//=============================================================================================================================
		//First Argument - REGION
		if (args[0].equalsIgnoreCase("region")) {
			//If nothing after region is entered, display usage
			if(args.length == 1) return false;
			
			//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
			// /regenblock region create
			if(args[1].equalsIgnoreCase("create")) {
				//Check if both points are set
				if (plugin.playerSelectionLeft.containsKey(player.getName()) && plugin.playerSelectionRight.containsKey(player.getName())) {
					//We have two points
					if (args.length == 2) {
						plugin.log.sendPlayerWarn(player, "Usage: /regenblock region create (name) [re-spawn time] - assigns selection to a region.");
						return true;
					}
					
					//Get region name
					String regionName = args[2].toLowerCase();
					
					//Check if region name is already taken
					if (plugin.config.getString("region." + regionName) != null) {
						plugin.log.sendPlayerWarn(player, "Region name is already in use.");
						return true;
					}
					
					//Get re-spawn time for the region
					int respawnTime;
					if (args.length == 4) {
						respawnTime = 20 * Integer.parseInt(args[3]); //convert from second to ticks
						if (respawnTime < 20) respawnTime = plugin.config.getInt("settings.defaultSpawnTime");
					} else {
						respawnTime = plugin.config.getInt("settings.defaultSpawnTime");
					}
					
					//Record region to configuration file
					plugin.config.setRegion(regionName, respawnTime, plugin.playerSelectionRight.get(player.getName()), plugin.playerSelectionLeft.get(player.getName()));
					
					//Print out the result to the player
					this.plugin.log.sendPlayerRegionCreate(player, regionName, respawnTime);
					return true;

				}
				//We do not have both points selected
				plugin.log.sendPlayerWarn(player, "You need to select two points before creating a region.");
				return true;
			}
			
			//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
			// /regenblock region remove
			if(args[1].equalsIgnoreCase("remove")) {
				if (args.length == 2) { //Did not specify region name
					plugin.log.sendPlayerWarn(player, "Usage: /regenblock region remove (name) - removes region from the list.");
					return true;
				}
				//Get region name
				String regionName = args[2].toLowerCase();
				//Check if region exists
				if (plugin.config.getString("region." + regionName) == null) {
					plugin.log.sendPlayerWarn(player, "Region " + regionName + " does not exist.");
					return true;
				}
				//Remove region
				plugin.config.removeProperty(regionName);
				//Message player
				plugin.log.sendPlayerNormal(player, "Region " + regionName + " was removed.");
				//Log event
				plugin.log.info(player.getName() + " removed region " + regionName);
				return true;
			}
			
			//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
			// /regenblock region modify
			if(args[1].equalsIgnoreCase("modify")) {
				if(args.length == 2) { //Did not specify anything
					plugin.log.sendPlayerWarn(player, "Usage: /regenblock region modify (name) [re-spawn time] - modify existing region.");
					return true;
				}
				
				if (args[2].equalsIgnoreCase("time")){
					// /regenblock region modify time
					if (args.length < 5) { //Did not actually enter name/time
						plugin.log.sendPlayerWarn(player, "Usage: /regenblock region modify time (name) (re-spawn time) - modify existing region's re-spawn time.");
						return true;
					}
					
					//Get name and time
					String regionName = args[3].toLowerCase();
					int respawnTime = Integer.parseInt(args[4]) * 20;
					if (respawnTime < 20) respawnTime = plugin.config.getInt("settings.defaultSpawnTime");
					
					//Update time property for the region
					plugin.config.setProperty("region." + regionName + ".respawnTime", respawnTime);
					//Message player
					plugin.log.sendPlayerNormal(player, "Region " + regionName + " was updated to respawn time of " + respawnTime/20 +"s.");
					//Log
					plugin.log.info(player + " updated region " + regionName + " to respawn time of " + respawnTime/20 + "s.");
					return true;
					
				} else {
					//Get region name
					String regionName = args[2].toLowerCase();
					
					//Check if region name exists
					if (plugin.config.getString("region." + regionName) == null) {
						plugin.log.sendPlayerWarn(player, "Region name does not exist.");
						return true;
					}
					
					//Get re-spawn time for the region
					int respawnTime;
					if (args.length == 4) {
						respawnTime = 20 * Integer.parseInt(args[3]); //convert from second to ticks
						if (respawnTime < 20) respawnTime = plugin.config.getInt("region." + regionName + ".respawnTime");
					} else {
						respawnTime = plugin.config.getInt("region." + regionName + ".respawnTime");
					}
					
					//Record region to configuration file
					plugin.config.setRegion(regionName, respawnTime, plugin.playerSelectionRight.get(player.getName()), plugin.playerSelectionLeft.get(player.getName()));
					
					//Print out the result to the player
					this.plugin.log.sendPlayerRegionCreate(player, regionName, respawnTime);
					return true;

				}

			}

			//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
			// /regenblock region list
			if(args[1].equalsIgnoreCase("list")) {
				if (plugin.config.listRegion() != null) {
					plugin.log.listRegion(player, plugin.config.listRegion());
				} else {
					plugin.log.sendPlayerNormal(player, "There are no regions.");
				}
				return true;
			}
			
		}

		return false;
	}
	
}
