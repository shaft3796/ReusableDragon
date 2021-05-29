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

        //Updater
        Logger logger = this.getLogger();
        new UpdateChecker(this, 12345).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                logger.info("There is not a new update available.");
            } else {
                logger.info("There is a new update available.");
            }
        });

        //Server alert
        System.out.println("Reusable dragon starting . .");

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
        }

    }

    @Override
    public void onDisable() {

        //Server alert
        System.out.println("Reusable dragon shutting down . .");

    }

}
