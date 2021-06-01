package fr.shaft.reusabledragon.commands;

import fr.shaft.reusabledragon.Lang;
import fr.shaft.reusabledragon.RdManager;
import fr.shaft.reusabledragon.build.BuildManager;
import fr.shaft.reusabledragon.build.Sample;
import fr.shaft.reusabledragon.enumerations.Difficulty;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SaveAreaCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        //ini
        Player player = (Player)sender;
        Difficulty difficulty;

        //Args
        if(args.length < 1){ difficulty = Difficulty.EASY;}
        else if(args[0].equalsIgnoreCase("easy")){
            difficulty = Difficulty.EASY;
        }
        else if(args[0].equalsIgnoreCase("medium")){
            difficulty = Difficulty.MEDIUM;
        }
        else if(args[0].equalsIgnoreCase("hard")){
            difficulty = Difficulty.HARD;
        }
        else{difficulty = Difficulty.EASY;}


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

        //alert
        player.sendMessage(ChatColor.GREEN + Lang.get("saveDone"));

        return true;

    }
}
