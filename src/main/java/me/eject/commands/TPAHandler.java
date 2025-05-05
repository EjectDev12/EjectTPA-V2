package me.eject.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class TPAHandler {
    private static final Map<Player, Player> requests = new HashMap();
    private static final Map<UUID, UUID> sentRequests = new HashMap();
    private static final Map<Player, Boolean> requestTypes = new HashMap();
    private static final Set<Player> teleporting = new HashSet();
    private static final Map<UUID, Long> cooldowns = new HashMap();
    private static final long TELEPORT_COOLDOWN = 10000L;

    public static boolean hasPendingRequest(Player sender, Player target) {
        return sentRequests.containsKey(sender.getUniqueId()) && ((UUID)sentRequests.get(sender.getUniqueId())).equals(target.getUniqueId());
    }

    public static void sendTPARequest(Player from, Player to, boolean isTpa) {
        if (hasPendingRequest(from, to)) {
            from.sendMessage(String.valueOf(ChatColor.RED) + "You can only send one TPA request to this player.");
        } else {
            requests.put(to, from);
            sentRequests.put(from.getUniqueId(), to.getUniqueId());
            requestTypes.put(to, isTpa);
            String var10001;
            if (isTpa) {
                var10001 = String.valueOf(ChatColor.AQUA);
                to.sendMessage(var10001 + from.getName() + String.valueOf(ChatColor.GRAY) + " wants to teleport to you.");
            } else {
                var10001 = String.valueOf(ChatColor.AQUA);
                to.sendMessage(var10001 + from.getName() + String.valueOf(ChatColor.GRAY) + " wants you to teleport to them.");
                var10001 = String.valueOf(ChatColor.GRAY);
                to.sendActionBar(var10001 + "Teleport request from " + String.valueOf(ChatColor.AQUA) + from.getName());
            }

            var10001 = String.valueOf(ChatColor.GRAY);
            to.sendMessage(var10001 + "Use " + String.valueOf(ChatColor.GREEN) + "/tpaccept" + String.valueOf(ChatColor.GRAY) + " to respond.");
            var10001 = String.valueOf(ChatColor.GRAY);
            from.sendMessage(var10001 + "Teleport request sent to " + String.valueOf(ChatColor.AQUA) + to.getName() + String.valueOf(ChatColor.GRAY) + ".");
        }
    }

    public static void showAcceptGUI(Player target) {
        Inventory gui = Bukkit.createInventory((InventoryHolder)null, 27, String.valueOf(ChatColor.GREEN) + "Teleport Request");
        ItemStack accept = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta acceptMeta = accept.getItemMeta();
        acceptMeta.setDisplayName(String.valueOf(ChatColor.GREEN) + "Accept");
        accept.setItemMeta(acceptMeta);
        ItemStack decline = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta declineMeta = decline.getItemMeta();
        declineMeta.setDisplayName(String.valueOf(ChatColor.RED) + "Decline");
        decline.setItemMeta(declineMeta);
        gui.setItem(11, decline);
        gui.setItem(15, accept);
        target.openInventory(gui);
    }

    public static void acceptRequest(final Player target) {
        final Player requester = (Player)requests.remove(target);
        final Boolean isTpa = (Boolean)requestTypes.remove(target);
        if (requester != null && requester.isOnline()) {
            sentRequests.remove(requester.getUniqueId());
            long now = System.currentTimeMillis();
            if (cooldowns.containsKey(requester.getUniqueId())) {
                long last = (Long)cooldowns.get(requester.getUniqueId());
                if (now - last < 10000L) {
                    requester.sendMessage(String.valueOf(ChatColor.RED) + "You must wait before teleporting again.");
                    return;
                }
            }

            teleporting.add(requester);
            requester.sendMessage(String.valueOf(ChatColor.GREEN) + "Teleporting in 5 seconds...");
            requester.playSound(requester.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
            (new BukkitRunnable() {
                int countdown = 5;

                public void run() {
                    if (!TPAHandler.teleporting.contains(requester)) {
                        requester.sendMessage(String.valueOf(ChatColor.RED) + "Teleport canceled.");
                        this.cancel();
                    } else if (this.countdown != 0) {
                        Player var10000 = requester;
                        String var10001 = String.valueOf(ChatColor.GRAY);
                        var10000.sendActionBar(var10001 + "Teleporting in " + String.valueOf(ChatColor.AQUA) + this.countdown + "s...");
                        --this.countdown;
                    } else {
                        if (isTpa != null && !isTpa) {
                            target.teleport(requester.getLocation());
                        } else {
                            requester.teleport(target.getLocation());
                        }

                        requester.sendTitle(String.valueOf(ChatColor.GREEN) + "Teleported!", "", 10, 40, 10);
                        requester.playSound(requester.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                        TPAHandler.cooldowns.put(requester.getUniqueId(), System.currentTimeMillis());
                        TPAHandler.teleporting.remove(requester);
                        this.cancel();
                    }
                }
            }).runTaskTimer(Bukkit.getPluginManager().getPlugin("Ulitverse"), 0L, 20L);
        } else {
            target.sendMessage(String.valueOf(ChatColor.RED) + "The teleport requester is no longer online.");
        }
    }

    public static void denyRequest(Player target) {
        Player requester = (Player)requests.remove(target);
        if (requester != null) {
            requester.sendMessage(String.valueOf(ChatColor.RED) + "Your teleport request was declined.");
            sentRequests.remove(requester.getUniqueId());
            requestTypes.remove(target);
        }

    }

    public static boolean isRequest(Player target) {
        return requests.containsKey(target);
    }

    public static boolean isTeleporting(Player player) {
        return teleporting.contains(player);
    }

    public static void cancelTeleport(Player player, String reason) {
        if (teleporting.remove(player)) {
            player.sendActionBar(reason);
            player.sendMessage(String.valueOf(ChatColor.RED) + "Teleport canceled due to movement.");
        }

    }

    public static void openConfirmGUI(Player player, Player target, boolean isHere) {
        String var10002 = isHere ? String.valueOf(ChatColor.GREEN) + "Send TPAHere to " : String.valueOf(ChatColor.GREEN) + "Send TPA to ";
        Inventory gui = Bukkit.createInventory((InventoryHolder)null, 27, var10002 + target.getName());
        ItemStack confirm = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName(String.valueOf(ChatColor.GREEN) + "Confirm");
        confirmMeta.setLore(Collections.singletonList(target.getName()));
        confirm.setItemMeta(confirmMeta);
        ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName(String.valueOf(ChatColor.RED) + "Cancel");
        cancel.setItemMeta(cancelMeta);
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);

        for(int i = 0; i < gui.getSize(); ++i) {
            gui.setItem(i, filler);
        }

        gui.setItem(11, cancel);
        gui.setItem(15, confirm);
        player.openInventory(gui);
    }

    public static void handleConfirmClick(Player player, String guiTitle, String targetName) {
        boolean isHere = guiTitle.contains("TPAHere");
        Player target = Bukkit.getPlayer(targetName);
        if (target != null && target.isOnline()) {
            sendTPARequest(player, target, !isHere);
            String var10001 = String.valueOf(ChatColor.GREEN);
            player.sendMessage(var10001 + "TPA request sent to " + target.getName() + ".");
        } else {
            player.sendMessage(String.valueOf(ChatColor.RED) + "Player not found or offline.");
        }

    }
}