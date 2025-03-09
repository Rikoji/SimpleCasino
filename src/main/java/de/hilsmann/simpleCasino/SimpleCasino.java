package de.hilsmann.simpleCasino;

import de.hilsmann.simpleCasino.listeners.SlotMachineListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleCasino extends JavaPlugin {
    private static SimpleCasino instance;
    public static String prefix = "💰🎲 ";

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(new SlotMachineListener(), this);
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public static SimpleCasino getInstance() {
        return instance;
    }
}