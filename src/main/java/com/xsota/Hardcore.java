package com.xsota;

import org.bukkit.plugin.java.JavaPlugin;


import commands.Selfharm;

public class Hardcore extends JavaPlugin {
	@Override
	public void onEnable() {
		getLogger().info("plugin has been enable.");
		
		//set command
		getCommand("selfharm").setExecutor(new Selfharm());
	}

	@Override
	public void onDisable() {
		getLogger().info("plugin has been disable.");
	}



}
