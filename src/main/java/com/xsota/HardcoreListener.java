package com.xsota;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * リスナクラスだよ
 * 
 * @author xsota
 *
 */

public class HardcoreListener implements Listener {
	int BAN_TIME = 12;
	String LOGIN_MESSAGE = "このサーバはハードコアです。死ぬと" + BAN_TIME + "時間BANされます";

	/**
	 * ログイン時のイベント
	 * 
	 * @param event
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.sendMessage(LOGIN_MESSAGE);
	}

	/**
	 * プレイヤーが死んだらBANしてKICKするぞ
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		String DEATH_MESSAGE = event.getDeathMessage();

		// プレイヤー取得
		final Player player = event.getEntity();

		// banリスト取得
		BanList banList = Bukkit.getBanList(BanList.Type.NAME);

		// BANする時間
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, BAN_TIME);
		Date expire = calendar.getTime();

		// 死亡時の座標にお墓
		Location location = player.getLocation();
		location.setY(location.getY());
		Block block = location.getBlock();

		block.setType(Material.SIGN_POST);
		Sign sign = (Sign) block.getState();
		
		sign.setLine(1, DEATH_MESSAGE);
		sign.setLine(2, this.getNow());
		sign.update();

		// 次にログインした時に死亡画面だとなんかアレなので強制リスポーン
		//player.spigot().respawn();

		if (player.isOp() == false) {
			// プレイヤーBANリストに追加
			banList.addBan(player.getName(), DEATH_MESSAGE, expire, "Hardcore");

			// BANリストに追加するだけだとそのまま遊べちゃうのでkick
			player.kickPlayer(DEATH_MESSAGE);
		}
	}

	private String getNow() {
		final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		final Date date = new Date(System.currentTimeMillis());
		return dateFormat.format(date);
	}
}
