package fr.shaft.reusabledragon;

import fr.shaft.reusabledragon.build.BuildManager;
import fr.shaft.reusabledragon.build.Sample;
import fr.shaft.reusabledragon.enumerations.Difficulty;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;


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

    }

}
