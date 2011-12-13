package net.dmg2.RegenBlock;

import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class RegenBlockEntityListener extends EntityListener{

	//============================================================
	private RegenBlock plugin;
	public RegenBlockEntityListener(RegenBlock instance) {
		this.plugin = instance;
	}
	//============================================================
	
	public void onExplosionPrime(ExplosionPrimeEvent event){
		if (event.isCancelled()) return; //=======================

		plugin.getServer().broadcastMessage("HI! TNT is set on fire! omg!!");
	}

}
