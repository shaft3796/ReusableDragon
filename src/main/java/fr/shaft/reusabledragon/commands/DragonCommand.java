package fr.shaft.reusabledragon.commands;

import fr.shaft.reusabledragon.RdManager;
import fr.shaft.reusabledragon.build.BuildManager;
import fr.shaft.reusabledragon.build.RdEntity;
import fr.shaft.reusabledragon.build.Sample;
import fr.shaft.reusabledragon.task.DragonFight;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class DragonCommand implements CommandExecutor {

    private static int taskid;
    public static int getTaskid() {
        return taskid;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        //Ini
        World end = RdManager.getWorld();
        Player player = (Player)commandSender;
        Inventory inventory = player.getInventory();
        Map<Material, Integer> materials = new HashMap<>();
        for(Map.Entry<Material, Integer> entry : RdManager.getRequiredMaterials().entrySet()){
            materials.put(entry.getKey(), entry.getValue());
        }

        //Check if a battle is currently active
        RdManager.actualiseFightStatue();
        if(RdManager.getFightStatue()){

            player.sendMessage(ChatColor.RED + " [Re Dragon] Un dragon est deja en vie !");
            return true;
        }

        //Check if player can spawn the dragon
        boolean spawnable = true;
        for(Map.Entry<Material, Integer> entry : materials.entrySet()){

            int quantity = entry.getValue();
            Material material = entry.getKey();

            for(ItemStack item : inventory){

                if(quantity <= 0){break;}

                if(item != null){

                    if(item.getType() == material){

                        quantity -= item.getAmount();
                    }

                }
            }

            materials.remove(material);
            materials.put(material, quantity);
        }
        for(Map.Entry<Material, Integer> entry : materials.entrySet()){

            if (!(entry.getValue() <= 0)) {

                spawnable = false;
                break;
            }
        }
        if(!spawnable){
            player.sendMessage(ChatColor.RED + " [Re Dragon] Il vous manque des choses pour faire spawn le dragon !");
            return true;
        }

        //build arena
        BuildManager.generateSamples();
        for(Sample sample : BuildManager.getSamples()){
            BuildManager.loadSamples(sample, RdManager.getBattleArenaRoots());
        }
        for(RdEntity entity : BuildManager.getEntities()){
            BuildManager.loadEntity(entity, RdManager.getWorld());
        }

        //Respawning
        EnderDragon dragon = (EnderDragon) end.spawnEntity(new Location(RdManager.getWorld(), 0, 74, 0), EntityType.ENDER_DRAGON);
        dragon.setAI(true);
        dragon.setPhase(EnderDragon.Phase.CIRCLING);
        dragon.setHealth(200);

        //Starting fight and bar actualisation task
        DragonFight task = new DragonFight();
        taskid = Bukkit.getScheduler().scheduleSyncRepeatingTask(RdManager.getPlugin(),task, 0, 1);

        player.sendMessage(ChatColor.GREEN + " [Re Dragon] Lancement du dragon !");

        //Remove items from player inv
        for(Map.Entry<Material, Integer> entry : RdManager.getRequiredMaterials().entrySet()){
            materials.put(entry.getKey(), entry.getValue());
        }
        for(Map.Entry<Material, Integer> entry : materials.entrySet()){

            int quantity = entry.getValue();
            Material material = entry.getKey();

            for(ItemStack item : inventory){

                if(quantity <= 0){break;}

                if(item != null){

                    if(item.getType() == material){
                        if(item.getAmount() > quantity){

                            item.setAmount(item.getAmount() - quantity);
                            quantity -= item.getAmount();
                        }
                        else{
                            quantity -= item.getAmount();
                            item.setAmount(0);
                        }

                    }

                }
            }

            materials.remove(material);
            materials.put(material, quantity);
        }

        RdManager.actualiseFightStatue();

        return true;
    }

}
