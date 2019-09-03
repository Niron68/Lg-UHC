package commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
import org.bukkit.entity.Player;

import com.niron.lgUhc.LgUHC;

public class CommandsLg implements CommandExecutor {
	
	private LgUHC lg;
	
	public CommandsLg(LgUHC lg) {
		this.lg = lg;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		
		if(args.length == 0) {
			sender.sendMessage("commande invalide");
		}
		
		if (args[0].equalsIgnoreCase("start")){
			lg.game.startGame();
		}
		
		if (args[0].equalsIgnoreCase("givebase")){
			Bukkit.broadcastMessage("test give kit");
			//lg.game.giveKit(lg.playerList.get(0), "base");
		}
		
		
		if (args[0].equalsIgnoreCase("getrole")){
			Bukkit.broadcastMessage("test getRole");
			System.out.println(lg.game.getPlayer((Player)sender) == null);
			System.out.println(lg.game.getPlayer((Player)sender).role == null);
			System.out.println(lg.game.getPlayer((Player)sender).role.rolename == null);
			System.out.println(lg.game.getPlayer((Player)sender).role.name == null);
			//Bukkit.broadcastMessage(lg.game.getPlayer((Player)sender).role.name);
		}
		
		
		return false;
	}

}
