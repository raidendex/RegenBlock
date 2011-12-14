package net.dmg2.RegenBlock;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.config.Configuration;

//Have to update to YamlConfiguration====================================================== UPDATE IT LATER
//import org.bukkit.configuration.file.YamlConfiguration;
@SuppressWarnings("deprecation") //======================================================== remove it later

public class RegenBlockConfig {
	
	private Configuration config;
	private HashMap<String, Object> configDefaultsHash = new HashMap<String, Object>();
	
	public RegenBlockConfig(File configFile) {
		this.config = new Configuration(configFile);
		
		//Some default settings
		this.configDefaultsHash.put("settings.defaultSpawnTime", 100);
		
		//Check if configuration file exists
		if (configFile.exists()){
			//If does, load it
			this.config.load();
		} else {
			//Otherwise create and populate default values
			for (String key : this.configDefaultsHash.keySet()) {
				this.config.setProperty(key, this.configDefaultsHash.get(key));
			}
			this.config.save();
		}
		
	}

	//#############################################################################################
	public int getInt(String key) {
		//If key is in the defaultHashMap use it as default value
		if (this.configDefaultsHash.containsKey(key) == true) {
			return this.config.getInt(key, Integer.parseInt(this.configDefaultsHash.get(key).toString()));			
		}
		//Otherwise use -1 for default value
		return this.config.getInt(key, -1);
	}
	
	public String getString(String key) {
		return this.config.getString(key);
	}
	
	public void setProperty(String key, Object value) {
		this.config.setProperty(key, value);
		this.config.save();
	}

	public void save() {
		this.config.save();
	}
	
	public void setRegion(String regionName, int respawnTime, Location right, Location left) {
		//Record all the properties
		this.config.setProperty("region." + regionName + ".respawnTime", respawnTime);
		this.config.setProperty("region." + regionName + ".left.X", left.getBlockX());
		this.config.setProperty("region." + regionName + ".left.Y", left.getBlockY());
		this.config.setProperty("region." + regionName + ".left.Z", left.getBlockZ());
		this.config.setProperty("region." + regionName + ".right.X", right.getBlockX());
		this.config.setProperty("region." + regionName + ".right.Y", right.getBlockY());
		this.config.setProperty("region." + regionName + ".right.Z", right.getBlockZ());
		this.config.setProperty("region." + regionName + ".world", right.getWorld().getName());
		//Save the configuration file
		this.config.save();
	}
	
	public void setBlock(Block block) {
		//Save block to configuration in case we crash before restoring
		String blockName = "x" + block.getX() + "y" + block.getY() + "z" + block.getZ();
		this.config.setProperty("blocksToRegen." + block.getWorld().getName() + "." + blockName + ".X", block.getX());
		this.config.setProperty("blocksToRegen." + block.getWorld().getName() + "." + blockName + ".Y", block.getY());
		this.config.setProperty("blocksToRegen." + block.getWorld().getName() + "." + blockName + ".Z", block.getZ());
		this.config.setProperty("blocksToRegen." + block.getWorld().getName() + "." + blockName + ".TypeID", block.getTypeId());
		this.config.setProperty("blocksToRegen." + block.getWorld().getName() + "." + blockName + ".Data", block.getData());
		this.config.save();
	}

	public void removeBlock(Block block) {
		//Remove block after we have restored it from the configuration file
		String blockName = "x" + block.getX() + "y" + block.getY() + "z" + block.getZ();
		this.config.removeProperty("blocksToRegen." + block.getWorld().getName() + "." + blockName);
		//Remove world entry is it's empty of blocks
		if (this.config.getString("blocksToRegen." + block.getWorld().getName()) == "{}") {
			this.config.removeProperty("blocksToRegen." + block.getWorld().getName());
		}
		this.config.save();
	}
	
	public List<String> listRegion() {
		return this.config.getKeys("region");
	}

	public List<String> listWorldsToRegen() {
		return this.config.getKeys("blocksToRegen");
	}

	public List<String> listBlocksToRegen(String worldName) {
		return this.config.getKeys("blocksToRegen." + worldName);
	}
	
	public void removeProperty(String regionName) {
		this.config.removeProperty("region." + regionName);
		//Save the configuration file
		this.config.save();
	}
	
}
