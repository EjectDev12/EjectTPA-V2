package me.eject.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPAHereCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (!(sender instanceof Player)) {
         return false;
      } else {
         Player player = (Player)sender;
         if (args.length != 1) {
            player.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /tpahere <player>");
            return true;
         } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null && target.isOnline()) {
               if (TPAHandler.hasPendingRequest(player, target)) {
                  player.sendMessage(String.valueOf(ChatColor.RED) + "You can only send one TPA request to this player.");
                  return true;
               } else {
                  TPAHandler.openConfirmGUI(player, target, true);
                  return true;
               }
            } else {
               player.sendMessage(String.valueOf(ChatColor.RED) + "Player not found or offline.");
               return true;
            }
         }
      }
   }
}
