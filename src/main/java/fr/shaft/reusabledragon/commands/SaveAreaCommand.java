package fr.shaft.reusabledragon.commands;

import fr.shaft.reusabledragon.RdManager;
import fr.shaft.reusabledragon.build.BuildManager;
import fr.shaft.reusabledragon.build.Sample;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SaveAreaCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

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

        return true;

    }
}
