package com.xsota;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * リスナクラスだよ
 * 
 * @author xsota
 *
 */

public class HardcoreListener implements Listener {
	int BAN_TIME;
	String LOGIN_MESSAGE;
	JavaPlugin plugin;
	ConsoleCommandSender console;
	String dateFormat;

	public HardcoreListener(JavaPlugin plugin) {
		this.dateFormat = "yyyy/MM/dd HH:mm:ss";
		this.console = Bukkit.getServer().getConsoleSender();
		this.plugin = plugin;
		this.BAN_TIME = this.plugin.getConfig().getInt("BANhour");
		this.LOGIN_MESSAGE = "このサーバはハードコアです。死ぬと" + BAN_TIME + "時間BANされます";
	}

	/**
	 * ログイン時のイベント
	 * 
	 * @param event
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (this.plugin.getConfig().getBoolean("ShowLoginMessage")) {
			player.sendMessage(LOGIN_MESSAGE);
		}
	}

	/**
	 * プレイヤーが死んだらBANしてKICKするぞ
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		// プレイヤー取得
		final Player player = event.getEntity();

		if (player.isOp() == false) {
			String DEATH_MESSAGE = event.getDeathMessage();

			// Inventory inventory = player.getInventory();
			// banリスト取得
			// BanList banList = Bukkit.getBanList(BanList.Type.NAME);

			// BANする時間
			// Calendar calendar = Calendar.getInstance();
			// calendar.add(Calendar.HOUR, BAN_TIME);

			// Date expire = calendar.getTime();
			String expire = new SimpleDateFormat(this.dateFormat)
					.format(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * BAN_TIME));

			// 死亡時の座標にお墓
			Location location = player.getLocation();
			location.setY(location.getY());
			Block block = location.getBlock();

			block.setType(Material.SIGN_POST);
			Sign sign = (Sign) block.getState();

			if (DEATH_MESSAGE.length() >= 21) {
				sign.setLine(1, DEATH_MESSAGE.substring(0, 21));
				sign.setLine(2, DEATH_MESSAGE.substring(21));
			} else {
				sign.setLine(1, DEATH_MESSAGE);
			}

			sign.setLine(3, this.getNow());
			sign.update();
			
			//死亡した座標にゾンビをうみだす
			EntityType entity = EntityType.ZOMBIE;
			
			Zombie zombie = (Zombie) player.getWorld().spawnEntity(location, entity);
			zombie.setRemoveWhenFarAway(false);
			zombie.getEquipment().setHelmet(new ItemStack(Material.GOLD_HELMET));
			zombie.setCustomName(player.getName()+"のゾンビ");			
			
			// 次にログインした時に死亡画面だとなんかアレなので強制リスポーン
			// player.spigot().respawn();

			// プレイヤーBANリストに追加
			// banList.addBan(player.getName(), DEATH_MESSAGE, expire,
			// "Hardcore");
			this.plugin.getConfig().set("BAN_PLAYERS." + player.getName(), expire);
			this.plugin.saveConfig();

			// BANリストに追加するだけだとそのまま遊べちゃうのでkick
			player.kickPlayer(DEATH_MESSAGE);
		}
	}

	/**
	 * ban中のプレイヤーはJoinさせない
	 * 
	 * @param event
	 */
	@EventHandler
	private void onLogin(PlayerLoginEvent event) {
		String ban = this.plugin.getConfig().getString("BAN_PLAYERS." + event.getPlayer().getName());

		if (ban == null) {
			return;
		}

		Date now = new Date(System.currentTimeMillis());

		SimpleDateFormat simpleDateFromat = new SimpleDateFormat(this.dateFormat);
		Date banDate = new Date();

		try {
			banDate = simpleDateFromat.parse(ban);
		} catch (ParseException e) {
			this.console.sendMessage("日付変換失敗:" + e);
		}

		if (banDate.compareTo(now) > 0) {
			event.disallow(event.getResult().KICK_OTHER, "まだ死んでから" + this.BAN_TIME + "時間たってないよ");
		}
	}

	/**
	 * モンスター
	 * 
	 * @param event
	 */
	@EventHandler
	public void onSpawn(CreatureSpawnEvent event) {
		Entity entity = event.getEntity();
		EntityType entityType = event.getEntityType();

		if (entityType == EntityType.ZOMBIE) {
			Zombie zombie = (Zombie) entity;
			zombie.setCanPickupItems(true);
			zombie.setMaxHealth(60);
			zombie.setHealth(60);
			return;
		}

		if (entityType == EntityType.SKELETON) {
			Skeleton skeleton = (Skeleton) entity;
			skeleton.setCanPickupItems(true);
			skeleton.setMaxHealth(40);
			skeleton.setHealth(40);
			skeleton.getEquipment().setItemInHand(new ItemStack(Material.BOW));
			return;
		}

	}

	/**
	 * 雷が爆発
	 * 
	 * @param event
	 */
	/*
	 * @EventHandler public void onLightningStrike(LightningStrikeEvent event) {
	 * Location location = event.getLightning().getLocation();
	 * event.getWorld().createExplosion(location.getX(), location.getY(),
	 * location.getZ(), (float) 2, true, true);
	 * 
	 * }
	 */

	/**
	 * 現在時刻取得
	 * 
	 * @return
	 */
	private String getNow() {
		final DateFormat dateFormat = new SimpleDateFormat(this.dateFormat);
		final Date date = new Date(System.currentTimeMillis());
		return dateFormat.format(date);
	}
}
