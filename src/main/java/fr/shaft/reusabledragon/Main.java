package fr.shaft.reusabledragon;

import fr.shaft.reusabledragon.build.BuildManager;
import fr.shaft.reusabledragon.build.Sample;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;


public class Main extends JavaPlugin {

    @Override
    public void onEnable() {

        //Server alert
        System.out.println("--------------------------------------------------------------------");
        System.out.println(" Reusable Dragon is starting now ! ");
        System.out.println(" ");

        new UpdateChecker(this, 92504).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                System.out.println("There is no update available.");
            } else {
                System.out.println("There is a new update available.");
            }
        });

        System.out.println(" ");

        System.out.println("If you just updated the plugin to a new version don't forget to look");
        System.out.println("at the CONFIGLINES file located in github, find the link in the Reusable Dragon spigot page");
        System.out.println(" ");


        //Manager Ini
        new RdManager(this);

        //config
        this.saveDefaultConfig();

        //Save area if there is no file, to prevent error
        File area = new File(this.getDataFolder() + "/DATA/" + "Area.rd");
        File entities = new File(this.getDataFolder() + "/DATA/" + "Entities.rd");
        if(!area.exists() || !entities.exists() ){
            //Blocs
            World world = RdManager.getWorld();
            BuildManager.sampleRegion(RdManager.getBattleArenaRoots(), RdManager.getBattleArenaEnd());
            for(Sample sample : BuildManager.getSamples()){
                BuildManager.registerSamples(sample);
            }
            BuildManager.convertSamples(RdManager.getBattleArenaRoots(), RdManager.getBattleArenaEnd());
            BuildManager.saveSamples();
            BuildManager.generateSamples();

            //Entities ( crystal )
            BuildManager.registerEntities(world);
            BuildManager.saveEntities();
            BuildManager.generateEntities(world);
        }else{
            //File loading
            BuildManager.generateSamples();
            BuildManager.generateEntities(RdManager.getWorld());
        }

        System.out.println("Reusable Dragon successfully started ! have fun : )");
        System.out.println("--------------------------------------------------------------------");

    }

    @Override
    public void onDisable() {

        //Server alert
        System.out.println("Reusable dragon shutting down . .");

    }

}
