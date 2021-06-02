package fr.shaft.reusabledragon;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {

        //Manager Ini
        RdManager.setUp(this);

    }

    @Override
    public void onDisable() {

        //Server alert
        System.out.println("Reusable dragon shutting down . .");

        //Save stats
        StatsManager.saveStats();

    }

}
