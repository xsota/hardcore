package com.xsota;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Hardcore extends JavaPlugin {
	@Override
	public void onEnable() {
		getLogger().info("plugin has been enable.");
	}

	@Override
	public void onDisable() {
		getLogger().info("plugin has been disable.");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;			
		}
		
		if(player==null){
			return false;
		}
				
		if (command.getName().equalsIgnoreCase("selfharm")) {
			sender.sendMessage(player.getName()+"はどこからか取り出した毒を飲んだ");
			player.addPotionEffect(new PotionEffect(PotionEffectType.POISON,360000, 1));
			return true;
		}

		return false;

	}

}
