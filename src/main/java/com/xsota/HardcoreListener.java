package com.xsota;

import java.util.Calendar;
import java.util.Date;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * リスナクラスだよ
 * @author xsota
 *
 */

public class HardcoreListener implements Listener{
	int BAN_TIME = 12;
	String LOGIN_MESSAGE = "このサーバはハードコアです。死ぬと"+BAN_TIME+"時間BANされます";
	
	/**
	 * ログイン時のイベント
	 * @param event
	 */
	@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.sendMessage(LOGIN_MESSAGE);		
	}
	
	/**
	 * プレイヤーが死んだらBANしてKICKするぞ
	 * @param event
	 */
	@EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
		//プレイヤー取得
		Player player = event.getEntity();
		
		//banリスト取得		
		BanList banList = Bukkit.getBanList(BanList.Type.NAME);
		
		//BANする時間
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, BAN_TIME);
		Date expire = calendar.getTime();
		
		//プレイヤーBANリストに追加
		banList.addBan(player.getName(), event.getDeathMessage(), expire, "Hardcore");
		
		//BANリストに追加するだけだとそのまま遊べちゃうのでkick
		player.kickPlayer("");
	}
}
