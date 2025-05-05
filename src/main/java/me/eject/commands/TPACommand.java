package me.eject.commands;

import java.util.Collections;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TPACommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (!(sender instanceof Player)) {
         return false;
      } else {
         Player player = (Player)sender;
         if (args.length != 1) {
            player.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /tpa <player>");
            return true;
         } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null && target.isOnline()) {
               if (TPAHandler.hasPendingRequest(player, target)) {
                  player.sendMessage(String.valueOf(ChatColor.RED) + "You can only send one TPA request to this player.");
                  return true;
               } else {
                  this.openConfirmGUI(player, target);
                  return true;
               }
            } else {
               player.sendMessage(String.valueOf(ChatColor.RED) + "Player not found or offline.");
               return true;
            }
         }
      }
   }

   public void openConfirmGUI(Player player, Player target) {
      Inventory gui = Bukkit.createInventory((InventoryHolder)null, 27, "§aSend TPA to " + target.getName());
      ItemStack confirm = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
      ItemMeta confirmMeta = confirm.getItemMeta();
      confirmMeta.setDisplayName("§aConfirm");
      confirmMeta.setLore(Collections.singletonList(target.getName()));
      confirm.setItemMeta(confirmMeta);
      ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
      ItemMeta cancelMeta = cancel.getItemMeta();
      cancelMeta.setDisplayName("§cCancel");
      cancel.setItemMeta(cancelMeta);
      gui.setItem(11, cancel);
      gui.setItem(15, confirm);
      player.openInventory(gui);
   }
}
