package com.xsota.hardcore.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class Selfharm : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, arg2: String, arg3: Array<String>): Boolean {
    var player: Player? = null
    if (sender is Player) {
      player = sender
    }

    if (player == null) {
      return false
    }

    if (command.name.equals("selfharm", ignoreCase = true)) {
      sender.sendMessage(player.name + "はどこからか取り出した毒を飲んだ")
      player.addPotionEffect(PotionEffect(PotionEffectType.POISON, 360000, 1))
      return true
    }

    return false
  }

}
