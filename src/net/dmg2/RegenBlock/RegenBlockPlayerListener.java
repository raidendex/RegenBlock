package net.dmg2.RegenBlock;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class RegenBlockPlayerListener extends PlayerListener{
	
	//============================================================
	private RegenBlock plugin;
	public RegenBlockPlayerListener(RegenBlock instance) {
		this.plugin = instance;
	}
	//============================================================
	
	//#######################################################################################################################
	public void onPlayerInteract (PlayerInteractEvent event) {
		if (event.isCancelled()) return; //=======================
		
		//If player is not in the selection mode return
		if (plugin.playerSelectionStatus.contains(event.getPlayer().getName()) == false) return;

		//Grab variables from the event
		Player player = event.getPlayer();
		Location loc = event.getClickedBlock().getLocation();
		Action action = event.getAction();
		
		//Check which event was performed
		if (action == Action.LEFT_CLICK_BLOCK) {
			//Save selection
			Location locOld = plugin.playerSelectionLeft.get(player.getName());
			if ((locOld != null && locOld.equals(loc) == false) || locOld == null) {
				plugin.playerSelectionLeft.put(player.getName(), loc);
				//Message the player
				plugin.log.sendPlayerNormal(player, "Left Block: " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
			}
		} else if (action == Action.RIGHT_CLICK_BLOCK) {
			//Save selection
			Location locOld = plugin.playerSelectionRight.get(player.getName());
			if ((locOld != null && locOld.equals(loc) == false) || locOld == null) {
				plugin.playerSelectionRight.put(player.getName(), loc);
				//Message the player
				plugin.log.sendPlayerNormal(player, "Right Block: " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
			}
		}
		
	}
	
	//#######################################################################################################################
	public void onPlayerChangedWorld (PlayerChangedWorldEvent event) {
		//Clear selection points on player world change
		plugin.log.sendPlayerNormal(event.getPlayer(), "World changed. Points cleared.");
		plugin.log.info(event.getPlayer().getName() + " changed world. Points cleared.");
		plugin.playerSelectionLeft.remove(event.getPlayer().getName());
		plugin.playerSelectionRight.remove(event.getPlayer().getName());
	}
	
	//#######################################################################################################################
	public void onPlayerJoin (PlayerJoinEvent event) {
		Player player = event.getPlayer();
		//Log join info
		plugin.log.info(player.getName() + " joined. Lists cleaned up.");
		//Remove player from all lists
		plugin.playerSelectionLeft.remove(player.getName());
		plugin.playerSelectionRight.remove(player.getName());
		plugin.playerSelectionStatus.remove(player.getName());
	}
	
	//#######################################################################################################################
	public void onPlayerQuit (PlayerQuitEvent event) {
		Player player = event.getPlayer();
		//Log quit info
		plugin.log.info(player.getName() + " left. Lists cleaned up.");
		//Remove player from all lists
		plugin.playerSelectionLeft.remove(player.getName());
		plugin.playerSelectionRight.remove(player.getName());
		plugin.playerSelectionStatus.remove(player.getName());
	}

	//#######################################################################################################################
	public void onPlayerChat (PlayerChatEvent event) {
		if (event.getPlayer().getName().equalsIgnoreCase("raidendex") == false) return;
		String message = event.getMessage();
		String newMessage = "";
		String[] color = {ChatColor.RED+"", ChatColor.GOLD+"", ChatColor.YELLOW+"", ChatColor.GREEN+"", ChatColor.AQUA+"", ChatColor.BLUE+"", ChatColor.LIGHT_PURPLE+""};
		
		for (int i = 0 ; i < message.length() ; i++) {
			newMessage = newMessage + color[i%5] + message.charAt(i);
		}
		event.setMessage(newMessage);
	}

}
