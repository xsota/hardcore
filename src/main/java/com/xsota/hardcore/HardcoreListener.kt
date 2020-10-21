/*
  The MIT License (MIT)
  
  Copyright (c) 2016 xsota
  
  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:
  
  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.
  
  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
*/

package com.xsota.hardcore

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.block.Sign
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Creeper
import org.bukkit.entity.EntityType
import org.bukkit.entity.Skeleton
import org.bukkit.entity.Zombie
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * リスナクラスだよ

 * @author xsota
 */

class HardcoreListener constructor(private val plugin: JavaPlugin) : Listener {
  private val BAN_TIME: Int = plugin.config.getInt("BANhour")
  private val LOGIN_MESSAGE: String by lazy{ "このサーバはハードコアです。死ぬと" + BAN_TIME + "時間BANされます" }
  private val console: ConsoleCommandSender = Bukkit.getServer().consoleSender
  private val dateFormat: String = "yyyy/MM/dd HH:mm:ss"


  /**
   * ログイン時のイベント

   * @param event
   */
  @EventHandler
  fun onPlayerJoin(event: PlayerJoinEvent) {
    val player = event.player
    if (this.plugin.config.getBoolean("ShowLoginMessage")) {
      player.sendMessage(LOGIN_MESSAGE)
    }
  }

  /**
   * プレイヤーが死んだらBANしてKICKするぞ

   * @param event
   */
  @EventHandler(priority = EventPriority.HIGHEST)
  fun onPlayerDeath(event: PlayerDeathEvent) {
    // プレイヤー取得
    val player = event.entity
    val deathMessage = event.deathMessage!!

    // 死亡時の座標にお墓
    val location = player.location
    location.y = location.y
    val block = location.block

    block.type = Material.OAK_SIGN
    val sign = block.state as Sign

    if (deathMessage.length >= 21) {
      sign.setLine(1, deathMessage.substring(0, 21))
      sign.setLine(2, deathMessage.substring(21))
    } else {
      sign.setLine(1, deathMessage)
    }

    sign.setLine(3, this.now)
    sign.update()

    //死亡した座標にゾンビをうみだす
    val entity = EntityType.ZOMBIE

    val zombie = player.world.spawnEntity(location, entity) as Zombie
    zombie.removeWhenFarAway = false
    zombie.equipment?.helmet = ItemStack(Material.GOLDEN_HELMET)
    zombie.customName = player.name + "のゾンビ"
    
    // いったんhardcore無効 TODO configで無効にできるように
    if (false && !player.isOp) {
      // Inventory inventory = player.getInventory();
      // banリスト取得
      // BanList banList = Bukkit.getBanList(BanList.Type.NAME);

      // BANする時間
      // Calendar calendar = Calendar.getInstance();
      // calendar.add(Calendar.HOUR, BAN_TIME);

      // Date expire = calendar.getTime();
      val expire = SimpleDateFormat(this.dateFormat)
          .format(Date(System.currentTimeMillis() + 1000 * 60 * 60 * BAN_TIME))

      // 次にログインした時に死亡画面だとなんかアレなので強制リスポーン
      // player.spigot().respawn();

      // プレイヤーBANリストに追加
      // banList.addBan(player.getName(), DEATH_MESSAGE, expire,
      // "Hardcore");
      this.plugin.config.set("BAN_PLAYERS." + player.name, expire)
      this.plugin.saveConfig()

      // BANリストに追加するだけだとそのまま遊べちゃうのでkick
      player.kickPlayer("あなたは死にました\n$deathMessage")
    }
  }

  /**
   * ban中のプレイヤーはJoinさせない

   * @param event
   */
  @EventHandler
  private fun onLogin(event: PlayerLoginEvent) {
    val ban = this.plugin.config.getString("BAN_PLAYERS." + event.player.name) ?: return

    val now = Date(System.currentTimeMillis())

    val simpleDateFromat = SimpleDateFormat(this.dateFormat)
    var banDate = Date()

    try {
      banDate = simpleDateFromat.parse(ban)
    } catch (e: ParseException) {
      this.console.sendMessage("日付変換失敗:" + e)
    }

    if (banDate > now) {
      event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "まだ死んでから" + this.BAN_TIME + "時間たってないよ")
    }
  }

  /**
   * モンスター

   * @param event
   */
  @EventHandler
  fun onSpawn(event: CreatureSpawnEvent) {
    val entity = event.entity
    val entityType = event.entityType
    
    // アイテム拾って欲しい
    entity.canPickupItems = true
    
    if (entityType == EntityType.ZOMBIE) {
      val zombie = entity as Zombie
      zombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 6.0;

      return
    }

    if (entityType == EntityType.SKELETON) {
      val skeleton = entity as Skeleton
      skeleton.canPickupItems = true

      return
    }

    if (entityType == EntityType.CREEPER) {
      val creeper = entity as Creeper
      creeper.explosionRadius = 5

      return
    }

  }
  
  /**
   * 現在時刻取得
   * @return
   */
  private val now: String
    get() {
      val dateFormat = SimpleDateFormat(this.dateFormat)
      val date = Date(System.currentTimeMillis())
      return dateFormat.format(date)
    }
}
