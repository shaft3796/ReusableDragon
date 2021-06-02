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

        //Server alert
        System.out.println("--------------------------------------------------------------------");
        System.out.println(" Reusable Dragon is starting now !! ");
        System.out.println(" ");

        UpdateChecker updater = new UpdateChecker(this, 92504);
        if (updater.checkForUpdates()) {
            System.out.println("There is a new update available !");
        }


        System.out.println(" ");
        System.out.println("If you just updated the plugin to a new version don't forget to look");
        System.out.println("at the CONFIGLINES file located in github, find the link in the Reusable Dragon spigot page");
        System.out.println(" ");


        //Manager Ini
        new RdManager(this);

        //config
        this.saveDefaultConfig();

        //Save area if there is no file, to prevent error
        for(Difficulty difficulty : Difficulty.values()){

            File area = new File(this.getDataFolder() + "/DATA/" + difficulty.getFileName().split("/")[0]);
            File entities = new File(this.getDataFolder() + "/DATA/" + difficulty.getFileName().split("/")[1]);
            if(!area.exists() || !entities.exists() ){
                //Blocs
                World world = RdManager.getWorld();
                BuildManager.sampleRegion(RdManager.getBattleArenaRoots(), RdManager.getBattleArenaEnd(), difficulty);
                for(Sample sample : difficulty.getSamples()){
                    BuildManager.registerSamples(sample);
                }
                BuildManager.convertSamples(RdManager.getBattleArenaRoots(), RdManager.getBattleArenaEnd(), difficulty);
                BuildManager.saveSamples(difficulty);
                BuildManager.generateSamples(difficulty);

                //Entities ( crystal )
                BuildManager.registerEntities(world, difficulty);
                BuildManager.saveEntities(difficulty);
                BuildManager.generateEntities(world, difficulty);
            }else{
                //File loading
                BuildManager.generateSamples(difficulty);
                BuildManager.generateEntities(RdManager.getWorld(), difficulty);
            }

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
