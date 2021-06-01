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
import org.bukkit.entity.Zoglin;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DragonCommand implements CommandExecutor {

    //ATTRIBUTS
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

    private static World end = RdManager.getWorld();

    private static Map<Material, Integer> materials;

    private static boolean DISABLE = false;

    //HANDLERS
    private static void difficultyHandler(String[] args){

        if(args.length < 1){

            Difficulty.setCurrentDifficulty(Difficulty.EASY);

        }else if(args[0].equalsIgnoreCase(Difficulty.EASY.getStringValue())){

            Difficulty.setCurrentDifficulty(Difficulty.EASY);

        }else if(args[0].equalsIgnoreCase(Difficulty.MEDIUM.getStringValue())){

            Difficulty.setCurrentDifficulty(Difficulty.MEDIUM);

        }else if(args[0].equalsIgnoreCase(Difficulty.HARD.getStringValue())){

            Difficulty.setCurrentDifficulty(Difficulty.HARD);

        }else{
            Difficulty.setCurrentDifficulty(Difficulty.EASY);
        }

    }
    private static boolean postCondHandler(){

        //Ini
        boolean spawnable = true;
        Inventory inventory = player.getInventory();

        //Fight statue
        RdManager.actualiseFightStatue();
        if(RdManager.getFightStatue()){

            player.sendMessage(ChatColor.RED + Lang.get("dragonStillAlive"));
            return false;
        }

        //Material check
        Map<Material, Integer> checkedMaterials = new HashMap<>();
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

            checkedMaterials.put(material, quantity);
        }
        for(Map.Entry<Material, Integer> entry : checkedMaterials.entrySet()){

            if (entry.getValue() > 0) {

                spawnable = false;
                break;
            }
        }
        if(!spawnable){
            player.sendMessage(ChatColor.RED + Lang.get("missingMaterials"));
            DISABLE = false;
            return false;
        }
        return true;

    }
    private static void inventoryHandler(){

        Inventory inventory = player.getInventory();
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

        }

    }
    private static void endCrystalHandler(){

        currentEntity = 0;
        taskid2 = Bukkit.getScheduler().scheduleSyncRepeatingTask(RdManager.getPlugin(), new Runnable() {
            @Override
            public void run() {

                if(currentEntity >= Difficulty.getDifficulty().getEntities().size()){
                    Bukkit.getScheduler().cancelTask(getTaskid2());
                    finish();
                }
                else{

                    BuildManager.loadEntity(Difficulty.getDifficulty().getEntities().get(getCurrentEntity()), RdManager.getWorld());
                    setCurrentEntity(getCurrentEntity()+1);
                }


            }
        }, 0, 15);

    }
    private static void dragonSpawnParticlesHandler(){

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

    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        //multi command disabler
        if(DISABLE){
            return true;
        }

        DISABLE = true;

        //Difficulty
        difficultyHandler(strings);

        //Ini
        player = (Player)commandSender;
        materials = RdManager.getRequiredMaterials().get(Difficulty.getDifficulty());

        //PostCond
        if(!postCondHandler()){
            return true;
        }

        //build arena
        BuildManager.generateSamples(Difficulty.getDifficulty());
        for(Sample sample : Difficulty.getDifficulty().getSamples()){
            BuildManager.loadSamples(sample, RdManager.getBattleArenaRoots());
        }

        //Remove Items
        inventoryHandler();

        //End Crystals
        endCrystalHandler();

        return true;
    }

    private static void finish(){

        //Ini
        double life = Difficulty.getDifficulty().getLife();
        BarColor color = Difficulty.getDifficulty().getBarColor();
        String name = Difficulty.getDifficulty().getNameColor() + Difficulty.getDifficulty().getName();

        //Particles
        dragonSpawnParticlesHandler();

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

        DISABLE = false;
    }

}
