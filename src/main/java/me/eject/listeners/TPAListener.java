package me.eject.listeners;

import me.eject.commands.TPAHandler;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TPAListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            Player player = (Player)e.getWhoClicked();
            Inventory inv = e.getInventory();
            String title = e.getView().getTitle();
            ItemStack clicked = e.getCurrentItem();
            if (clicked != null && clicked.hasItemMeta() && clicked.getItemMeta().getDisplayName() != null) {
                e.setCancelled(true);
                String displayName = clicked.getItemMeta().getDisplayName();
                List lore;
                String targetName;
                Player target;
                if (title.startsWith("§aSend TPA to ")) {
                    if (displayName.contains("Confirm")) {
                        lore = clicked.getItemMeta().getLore();
                        if (lore == null || lore.isEmpty()) {
                            player.sendMessage(String.valueOf(ChatColor.RED) + "Something went wrong.");
                            return;
                        }

                        targetName = (String)lore.get(0);
                        target = Bukkit.getPlayer(targetName);
                        if (target != null && target.isOnline()) {
                            TPAHandler.sendTPARequest(player, target, true);
                        } else {
                            player.sendMessage(String.valueOf(ChatColor.RED) + "Player not found or offline.");
                        }
                    } else if (displayName.contains("Cancel")) {
                        player.sendMessage(String.valueOf(ChatColor.RED) + "TPA request cancelled.");
                    }

                    player.closeInventory();
                } else if (!title.startsWith("§aSend TPAHere to ")) {
                    if (title.equals("§aTeleport Request")) {
                        if (displayName.contains("Accept")) {
                            TPAHandler.acceptRequest(player);
                            player.sendMessage(String.valueOf(ChatColor.GRAY) + "Accepted the teleport request.");
                        } else if (displayName.contains("Decline")) {
                            TPAHandler.denyRequest(player);
                            player.sendMessage(String.valueOf(ChatColor.RED) + "Declined the teleport request.");
                        }

                        player.closeInventory();
                    }

                } else {
                    if (displayName.contains("Confirm")) {
                        lore = clicked.getItemMeta().getLore();
                        if (lore == null || lore.isEmpty()) {
                            player.sendMessage(String.valueOf(ChatColor.RED) + "Something went wrong.");
                            return;
                        }

                        targetName = (String)lore.get(0);
                        target = Bukkit.getPlayer(targetName);
                        if (target != null && target.isOnline()) {
                            TPAHandler.sendTPARequest(player, target, false);
                            String var10001 = String.valueOf(ChatColor.GREEN);
                            player.sendMessage(var10001 + "TPAHERE request sent to " + target.getName() + ".");
                        } else {
                            player.sendMessage(String.valueOf(ChatColor.RED) + "Player not found or offline.");
                        }
                    } else if (displayName.contains("Cancel")) {
                        player.sendMessage(String.valueOf(ChatColor.RED) + "TPAHERE request cancelled.");
                    }

                    player.closeInventory();
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (TPAHandler.isTeleporting(player)) {
            if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()) {
                TPAHandler.cancelTeleport(player, String.valueOf(ChatColor.RED) + "Teleport cancelled due to movement.");
            }

        }
    }
}
