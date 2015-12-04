package commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Selfharm implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command command, String arg2, String[] arg3) {
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
