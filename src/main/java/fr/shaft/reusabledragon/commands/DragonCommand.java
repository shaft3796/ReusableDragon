package fr.shaft.reusabledragon.commands;

import fr.shaft.reusabledragon.Lang;
import fr.shaft.reusabledragon.RdManager;
import fr.shaft.reusabledragon.build.BuildManager;
import fr.shaft.reusabledragon.build.RdEntity;
import fr.shaft.reusabledragon.build.Sample;
import fr.shaft.reusabledragon.enumerations.Difficulty;
import fr.shaft.reusabledragon.task.DragonFight;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.boss.BarColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class DragonCommand implements CommandExecutor {

    private static int taskid;
    public static int getTaskid() {
        return taskid;
    }

    private static int taskid2;
    public static int getTaskid2() {
        return taskid2;
    }

    private static int currentEntity;
    public static int getCurrentEntity() {
        return currentEntity;
    }
    public static void setCurrentEntity(int currentEntity) {
        DragonCommand.currentEntity = currentEntity;
    }

    private static Player player;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        //Ini
        World end = RdManager.getWorld();
        player = (Player)commandSender;
        Inventory inventory = player.getInventory();
        Map<Material, Integer> materials = new HashMap<>();
        for(Map.Entry<Material, Integer> entry : RdManager.getRequiredMaterials().entrySet()){
            materials.put(entry.getKey(), entry.getValue());
        }

        //Difficulty
        if(strings.length < 1){

            Difficulty.setCurrentDifficulty(Difficulty.EASY);

        }else if(strings[0].equalsIgnoreCase(Difficulty.EASY.getStringValue())){

            Difficulty.setCurrentDifficulty(Difficulty.EASY);

        }else if(strings[0].equalsIgnoreCase(Difficulty.MEDIUM.getStringValue())){

            Difficulty.setCurrentDifficulty(Difficulty.MEDIUM);

        }else if(strings[0].equalsIgnoreCase(Difficulty.HARD.getStringValue())){

            Difficulty.setCurrentDifficulty(Difficulty.HARD);

        }

        //Check if a battle is currently active
        RdManager.actualiseFightStatue();
        if(RdManager.getFightStatue()){

            player.sendMessage(ChatColor.RED + Lang.get("dragonStillAlive"));
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
            player.sendMessage(ChatColor.RED + Lang.get("missingMaterials"));
            return true;
        }

        //build arena
        BuildManager.generateSamples();
        for(Sample sample : BuildManager.getSamples()){
            BuildManager.loadSamples(sample, RdManager.getBattleArenaRoots());
        }

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

        currentEntity = 0;
        taskid2 = Bukkit.getScheduler().scheduleSyncRepeatingTask(RdManager.getPlugin(), new Runnable() {
            @Override
            public void run() {

                if(currentEntity >= BuildManager.getEntities().size()){
                    Bukkit.getScheduler().cancelTask(getTaskid2());
                    finish();
                }
                else{

                    BuildManager.loadEntity(BuildManager.getEntities().get(getCurrentEntity()), RdManager.getWorld());
                    setCurrentEntity(getCurrentEntity()+1);
                }


            }
        }, 0, 15);

        return true;
    }

    private static void finish(){

        //Ini
        World end = RdManager.getWorld();
        double life = Difficulty.getDifficulty().getLife();
        BarColor color = Difficulty.getDifficulty().getBarColor();
        String name = Difficulty.getDifficulty().getNameColor() + Difficulty.getDifficulty().getName();

        //Particles
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(200, 0, 0), 5);

        Location from = new Location(end, 0.5, 66, 0.5);
        Location to = new Location(end, 0.5, 70, 0.5);
        double space = 0.1;
        double distance = from.distance(to);

        Vector start = from.toVector();
        Vector finish = to.toVector();

        Vector vector = finish.clone().subtract(start).normalize().multiply(space);

        double covered = 0;

        for (; covered < distance; start.add(vector)) {

            end.spawnParticle(Particle.REDSTONE, start.getX(), start.getY(), start.getZ(), 10, dustOptions);

            covered += space;
        }

        end.playSound(new Location(RdManager.getWorld(), 0, 70, 0), Sound.ENTITY_GENERIC_EXPLODE, 10, 29);

        //Respawning

        EnderDragon dragon = (EnderDragon) end.spawnEntity(new Location(RdManager.getWorld(), 0, 74, 0), EntityType.ENDER_DRAGON);
        dragon.setAI(true);
        dragon.setPhase(EnderDragon.Phase.CIRCLING);
        dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(life);
        dragon.setHealth(life);

        //Starting fight and bar actualisation task
        RdManager.createBar(name, color);
        DragonFight task = new DragonFight();
        taskid = Bukkit.getScheduler().scheduleSyncRepeatingTask(RdManager.getPlugin(),task, 0, 1);

        player.sendMessage(ChatColor.GREEN + Lang.get("fightStarting"));

        RdManager.actualiseFightStatue();
    }

}
