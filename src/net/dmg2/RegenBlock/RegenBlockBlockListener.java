package net.dmg2.RegenBlock;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitScheduler;

public class RegenBlockBlockListener extends BlockListener {

	//============================================================
	private RegenBlock plugin;
	public RegenBlockBlockListener(RegenBlock instance) {
		this.plugin = instance;
	}
	//============================================================

	//##########################################################################################################
	public void onBlockBreak(BlockBreakEvent event){
		if(event.isCancelled()) return; //========================
		regenBlock(event.getBlock(), event.getBlock().getType());
	}
	//##########################################################################################################
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if(event.isCancelled()) return; //========================
		regenBlock(event.getBlock(), event.getBlock().getType());
	}
	//##########################################################################################################
	public void onBlockPlace(BlockPlaceEvent event) {
		if(event.isCancelled()) return; //========================
		regenBlock(event.getBlock(), Material.AIR);
	}
	

	//------------------Cancel these----------------------------------------------------------------------------
	//##########################################################################################################
	public void onBlockBurn(BlockBurnEvent event) {
		if(event.isCancelled()) return; //========================
		event.setCancelled(true);
	}
	//##########################################################################################################
	public void onBlockFade(BlockFadeEvent event) {
		if(event.isCancelled()) return; //========================
		event.setCancelled(true);
	}
	//##########################################################################################################
	public void onBlockForm(BlockFormEvent event) {
		if(event.isCancelled()) return; //========================
		event.setCancelled(true);
	}
	//##########################################################################################################
	public void onBlockIgnite(BlockIgniteEvent event) {
		if(event.isCancelled()) return; //========================
		event.setCancelled(true);
	}
	//------------------Cancel these----------------------------------------------------------------------------
	//##########################################################################################################

	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	public void regenBlock(Block b, Material m) {
		//Get block and material from the event
		final Block block = b;
		if (m == Material.FIRE) m = Material.AIR;
		final Material mat = m;
		
		if (plugin.config.listRegion() == null) return;

		for (String regionName : plugin.config.listRegion()) {

			//Get world name
			String worldName = plugin.config.getString("region." + regionName + ".world");

			//Make sure block is in this region's world before checking further
			if (block.getWorld().getName().equalsIgnoreCase(worldName) == false) {
				continue;
			}

			//Get re-spawn time
			int respawnTime = Integer.parseInt(plugin.config.getString("region." + regionName + ".respawnTime"));

			//Get region coordinates
			int leftX = Integer.parseInt(plugin.config.getString("region." + regionName + ".left.X"));
			int leftY = Integer.parseInt(plugin.config.getString("region." + regionName + ".left.Y"));
			int leftZ = Integer.parseInt(plugin.config.getString("region." + regionName + ".left.Z"));
			int rightX = Integer.parseInt(plugin.config.getString("region." + regionName + ".right.X"));
			int rightY = Integer.parseInt(plugin.config.getString("region." + regionName + ".right.Y"));
			int rightZ = Integer.parseInt(plugin.config.getString("region." + regionName + ".right.Z"));
			
			//Check if block is within the region
			if (Math.abs(leftX - rightX) == Math.abs(leftX - block.getX()) + Math.abs(rightX - block.getX()) &&
					Math.abs(leftY - rightY) == Math.abs(leftY - block.getY()) + Math.abs(rightY - block.getY()) &&
					Math.abs(leftZ - rightZ) == Math.abs(leftZ - block.getZ()) + Math.abs(rightZ - block.getZ())) {
				
				//Check if the block is already being regenerated
				if ( plugin.config.getString("blocksToRegen." + worldName + "." + "x" + block.getX() + "y" + block.getY() + "z" + block.getZ()) != null) return;
				//Save block to configuration in case we crash
				plugin.config.setBlock(block);
				
		    	BukkitScheduler scheduler = plugin.getServer().getScheduler();
		    	scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
		    		public void run() {
		    			block.setType(mat);
		    			plugin.config.removeBlock(block);
		    		}    		
		    	}, respawnTime);
		    	return;

			}
			
		}		

	}
	
	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	public void regenOldBlocks() {
		//Make sure there are worlds listen
		if (plugin.config.listWorldsToRegen() == null) {
			return;
		}
		
		//Go through world names
		for (String worldName : plugin.config.listWorldsToRegen()) {
			if (plugin.config.listBlocksToRegen(worldName) != null) {
				plugin.log.info("Restoring Old Regen Blocks.");
				for (String blockName : plugin.config.listBlocksToRegen(worldName)) {
					//Get XYZ and TypeId
					int x = plugin.config.getInt("blocksToRegen." + worldName + "." + blockName + ".X");
					int y = plugin.config.getInt("blocksToRegen." + worldName + "." + blockName + ".Y");
					int z = plugin.config.getInt("blocksToRegen." + worldName + "." + blockName + ".Z");
					int typeId = plugin.config.getInt("blocksToRegen." + worldName + "." + blockName + ".TypeID");
					//Get block from the world
					Block block = plugin.getServer().getWorld(worldName).getBlockAt(x, y, z);
					//Set its type to what it should be
					block.setTypeId(typeId);
					//Remove entry from configuration file
					plugin.config.removeBlock(block);
				}
				
			}
			
		}
		
	}
	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	
	public boolean letsCancelEvent(Material m) {
		//Return true if we need to cancel the event
		if (m == Material.CHEST) return true;
		if (m == Material.BED) return true;
		if (m == Material.BED_BLOCK) return true;
		if (m == Material.FIRE) return true;
		if (m == Material.LAVA) return true;
		if (m == Material.LOCKED_CHEST) return true;
		if (m == Material.WOODEN_DOOR) return true;
		if (m == Material.WATER) return true;
		if (m == Material.TNT) return true;
		if (m == Material.SIGN) return true;
		if (m == Material.SIGN_POST) return true;
		if (m == Material.PAINTING) return true;
		return false;
	}

}
