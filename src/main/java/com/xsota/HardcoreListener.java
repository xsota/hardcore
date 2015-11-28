package com.xsota;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;


public class HardcoreListener implements Listener{
	String LOGIN_MESSAGE = "このサーバはハードコアです。死ぬとn時間BANされます";
	
	@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.sendMessage(LOGIN_MESSAGE);		
	}
	
}
