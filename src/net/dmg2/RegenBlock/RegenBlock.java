package net.dmg2.RegenBlock;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RegenBlock extends JavaPlugin {
	
	//=============================================================================================
	protected RegenBlockLogHandler log;
	private RegenBlockCommandExecutor commandExecutor;
	public String pluginPath;
	public File configFile;
	public RegenBlockConfig config;
	private final RegenBlockBlockListener blockListener = new RegenBlockBlockListener(this);
	private final RegenBlockPlayerListener playerListener = new RegenBlockPlayerListener(this);
	//private final RegenBlockEntityListener entityListener = new RegenBlockEntityListener(this);
	//=============================================================================================
	
	//---------------------------------------------------------------------------------------------
	//HashMaps to store player's selections
	public HashMap<String, Location> playerSelectionLeft = new HashMap<String, Location>();
	public HashMap<String, Location> playerSelectionRight = new HashMap<String, Location>();
	public ArrayList<String> playerSelectionStatus = new ArrayList<String>();
	//---------------------------------------------------------------------------------------------
	
	
    public void onEnable(){
    	//Log
    	this.log = new RegenBlockLogHandler(this);
    	this.log.info("Enabled. Good Day.");
    	
    	//Events handler
    	PluginManager pm = this.getServer().getPluginManager();
    	
    	pm.registerEvent(Event.Type.BLOCK_BREAK, this.blockListener, Event.Priority.Normal, this);
    	pm.registerEvent(Event.Type.BLOCK_PLACE, this.blockListener, Event.Priority.Normal, this);
    	pm.registerEvent(Event.Type.BLOCK_BURN, this.blockListener, Event.Priority.Normal, this);
    	pm.registerEvent(Event.Type.BLOCK_PHYSICS, this.blockListener, Event.Priority.Normal, this);
    	pm.registerEvent(Event.Type.BLOCK_FADE, this.blockListener, Event.Priority.Normal, this);
    	pm.registerEvent(Event.Type.BLOCK_FORM, this.blockListener, Event.Priority.Normal, this);
    	pm.registerEvent(Event.Type.BLOCK_IGNITE, this.blockListener, Event.Priority.Normal, this);

    	pm.registerEvent(Event.Type.PLAYER_INTERACT, this.playerListener, Event.Priority.Normal, this);
    	pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Normal, this);
    	pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Normal, this);
    	pm.registerEvent(Event.Type.PLAYER_CHANGED_WORLD, this.playerListener, Event.Priority.Normal, this);
    	
    	//pm.registerEvent(Event.Type.EXPLOSION_PRIME, this.entityListener, Event.Priority.Normal, this);
    	
    	//Settings file - [Update API]
    	this.pluginPath = this.getDataFolder().getAbsolutePath();
    	this.configFile = new File(this.pluginPath + File.separator + "config.yml");
    	this.config = new RegenBlockConfig(this.configFile);
    	
    	
    	//Commands handler
    	this.commandExecutor = new RegenBlockCommandExecutor(this);
    	this.getCommand("regenblock").setExecutor(this.commandExecutor);
    	
    	//Restore blocks from old sessions
    	blockListener.regenOldBlocks();
    	
    }

    public void onDisable(){
    	//Restore blocks from this session before shut down
    	blockListener.regenOldBlocks();

    	//Save configuration file
    	this.config.save();

    	//Log
    	this.log.info("Disabled. Good Bye.");
    	
    }
    
}
