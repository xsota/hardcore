package com.xsota;


import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
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
	
	/**
	 * プレイヤーの死亡時に実行される
	 * @param event
	 */
	@EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		player.sendMessage("死んでしまうとはなさけない");	
	}
}
