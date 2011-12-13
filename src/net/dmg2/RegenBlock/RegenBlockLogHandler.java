package net.dmg2.RegenBlock;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

public class RegenBlockLogHandler {
	//==============================================================
	private RegenBlock plugin;
	private Logger logger;
	public RegenBlockLogHandler(RegenBlock plugin){
		this.plugin = plugin;
		this.logger = Logger.getLogger("Minecraft");
	}
	//==============================================================
	//Generate message for console
	private String buildString(String message) {
		PluginDescriptionFile pdFile = plugin.getDescription();
		return "[" + pdFile.getName() +"] (" + pdFile.getVersion() + ") " + message;
	}
	//Generate message for player
	private String buildStringPlayer(String message, ChatColor color) {
		PluginDescriptionFile pdFile = plugin.getDescription();
		return color + "[" + pdFile.getName() +"] " + message;
	}
	

	//Log message as INFO
	public void info(String message){
		this.logger.info(this.buildString(message));
	}
	//Log message as WARNING
	public void warn(String message){
		this.logger.warning(this.buildString(message));
	}
	
	
	//Normal message to the player
	public void sendPlayerNormal(Player player, String message) {
		player.sendMessage(this.buildStringPlayer(message, ChatColor.AQUA));
	}
	//Normal message to the player
	public void sendPlayerWarn(Player player, String message) {
		player.sendMessage(this.buildStringPlayer(message, ChatColor.RED));
	}
	
	//Region create message to the player
	public void sendPlayerRegionCreate(Player player, String regionName, int respawnTime) {
		sendPlayerNormal(player, "Region " + regionName + " is saved.");
		sendPlayerNormal(player, "Respawn time: " + respawnTime/20 + "s.");
		String right = plugin.playerSelectionRight.get(player.getName()).getBlockX() + " " + plugin.playerSelectionRight.get(player.getName()).getBlockY() + " " + plugin.playerSelectionRight.get(player.getName()).getBlockZ();
		String left = plugin.playerSelectionLeft.get(player.getName()).getBlockX() + " " + plugin.playerSelectionLeft.get(player.getName()).getBlockY() + " " + plugin.playerSelectionLeft.get(player.getName()).getBlockZ();
		sendPlayerNormal(player, "Location: [Left]" + left + " [Right]" + right);
		//Log
		info(player.getName() + " created/updated region " + regionName + ".");
	}
	
	//List all regions to the player
	public void listRegion(Player player, List<String> listRegion) {
		for (String regionName : listRegion) {
			//Get XYZ for left and right
			String left = plugin.config.getString("region." + regionName + ".left.X") + " " + plugin.config.getString("region." + regionName + ".left.Y") + " "+ plugin.config.getString("region." + regionName + ".left.Z");
			String right = plugin.config.getString("region." + regionName + ".right.X") + " " + plugin.config.getString("region." + regionName + ".right.Y") + " " + plugin.config.getString("region." + regionName + ".right.Z");
			//Get world name
			String worldName = plugin.config.getString("region." + regionName + ".world");
			//Get re-spawn time
			int respawnTime = Integer.parseInt(plugin.config.getString("region." + regionName + ".respawnTime")) / 20;
			//Message the player
			sendPlayerNormal(player, regionName + ": [W] " + worldName + " [L] " + left +"; [R] " + right + "; [T] " + respawnTime + "s.");
		}
	}

}
