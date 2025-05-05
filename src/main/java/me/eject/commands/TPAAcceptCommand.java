package me.eject.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPAAcceptCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player) {
         Player p = (Player)sender;
         TPAHandler.showAcceptGUI(p);
         return true;
      } else {
         return false;
      }
   }
}
