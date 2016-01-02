package com.xsota;

import org.bukkit.plugin.java.JavaPlugin;


import commands.Selfharm;

public class Hardcore extends JavaPlugin {
	@Override
	public void onEnable() {
		getLogger().info("plugin has been enable.");
		
		//make default config file
		this.saveDefaultConfig();
		this.saveConfig();
		
		//copy new config value
		getConfig().options().copyDefaults(true);
		
		//set command
		getCommand("selfharm").setExecutor(new Selfharm());
		
		//set listener
		getServer().getPluginManager().registerEvents(new HardcoreListener(this), this);
	}

	@Override
	public void onDisable() {
		getLogger().info("plugin has been disable.");
	}



}
