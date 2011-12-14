package net.dmg2.RegenBlock;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
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
	public void onBlockPlace(BlockPlaceEvent event) {
		if(event.isCancelled()) return; //========================
		regenBlock(event.getBlock(), Material.AIR);
	}
	//##########################################################################################################
	

	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	public void regenBlock(Block b, Material m) {
		//Make sure mat passed in is not fire ... happens sometimes it seems =.=
		if (m == Material.FIRE) m = Material.AIR;
		final Material mat = m;
		final Block block = b;

		//Check if block is in any of the defined regions (-1 if not)
		int respawnTime = blockIsInRegion(block); 
		if (respawnTime == -1) return;

		//Schedule a re-spawn task
    	BukkitScheduler scheduler = plugin.getServer().getScheduler();
    	scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
    		public void run() {
    			block.setType(mat);
    			plugin.config.removeBlock(block);
    		}
    	}, respawnTime);
    	return;

	}
	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	
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
	
	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	public int blockIsInRegion(Block block) {
		
		if (plugin.config.listRegion() == null) return -1;

		for (String regionName : plugin.config.listRegion()) {

			//Get world name
			String worldName = plugin.config.getString("region." + regionName + ".world");

			//Make sure block is in this region's world before checking further
			if (block.getWorld().getName().equalsIgnoreCase(worldName) == false) continue;

			//Get region coordinates
			int leftX = plugin.config.getInt("region." + regionName + ".left.X");
			int leftY = plugin.config.getInt("region." + regionName + ".left.Y");
			int leftZ = plugin.config.getInt("region." + regionName + ".left.Z");
			int rightX = plugin.config.getInt("region." + regionName + ".right.X");
			int rightY = plugin.config.getInt("region." + regionName + ".right.Y");
			int rightZ = plugin.config.getInt("region." + regionName + ".right.Z");
			
			//Check if block is within the region
			if (Math.abs(leftX - rightX) == Math.abs(leftX - block.getX()) + Math.abs(rightX - block.getX()) &&
					Math.abs(leftY - rightY) == Math.abs(leftY - block.getY()) + Math.abs(rightY - block.getY()) &&
					Math.abs(leftZ - rightZ) == Math.abs(leftZ - block.getZ()) + Math.abs(rightZ - block.getZ())) {
				
				//Check if the block is already being regenerated
				if ( plugin.config.getString("blocksToRegen." + worldName + "." + "x" + block.getX() + "y" + block.getY() + "z" + block.getZ()) != null) return -1;

				//Save block to configuration in case we crash
				plugin.config.setBlock(block);
				
				//Return re-spawn time
		    	return plugin.config.getInt("region." + regionName + ".respawnTime");

			}
			
		}	
		return -1;
	}
	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

}
