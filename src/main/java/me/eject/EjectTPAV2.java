package me.eject;


import me.eject.commands.TPAAcceptCommand;
import me.eject.commands.TPACommand;
import me.eject.commands.TPAHereCommand;
import me.eject.listeners.TPAListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class EjectTPAV2 extends JavaPlugin {
    public void onEnable() {
        this.getCommand("tpa").setExecutor(new TPACommand());
        this.getCommand("tpaccept").setExecutor(new TPAAcceptCommand());
        this.getCommand("tpahere").setExecutor(new TPAHereCommand());
        this.getServer().getPluginManager().registerEvents(new TPAListener(), this);
    }

    public void onDisable() {
    }
}
