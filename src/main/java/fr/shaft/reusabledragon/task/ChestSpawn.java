package fr.shaft.reusabledragon.task;

import fr.shaft.reusabledragon.Lang;
import fr.shaft.reusabledragon.RdManager;
import fr.shaft.reusabledragon.build.BuildManager;
import fr.shaft.reusabledragon.build.Sample;
import fr.shaft.reusabledragon.commands.DragonCommand;
import fr.shaft.reusabledragon.enumerations.Difficulty;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class ChestSpawn implements Runnable{

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
        ChestSpawn.currentEntity = currentEntity;
    }

    private Location location;

    public ChestSpawn(Location location){

        this.location = location;
    }

    @Override
    public void run() {

        //Ini
        location.getBlock().setType(Material.CHEST);
        org.bukkit.block.Chest chest = (org.bukkit.block.Chest) location.getBlock().getState();
        Inventory inventory = chest.getBlockInventory();

        //POST COND
        RdManager.actualiseFightStatue();
        if(!RdManager.getFightStatue()){

            Difficulty[] values = Difficulty.values();
            for(int i = values.length - 1; i >= 0; i--){
                Difficulty difficulty =values[i];

                Map<Material, Integer> materials = RdManager.getRequiredMaterials().get(difficulty);
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

                boolean spawnable = true;
                for(Map.Entry<Material, Integer> entry : checkedMaterials.entrySet()){

                    if (entry.getValue() > 0) {

                        spawnable = false;
                        break;
                    }
                }

                if(spawnable){

                    //clear chest
                    chest.getBlockInventory().clear();

                    //update difficulty
                    Difficulty.setCurrentDifficulty(difficulty);

                    //build arena
                    BuildManager.generateSamples(Difficulty.getDifficulty());
                    for(Sample sample : Difficulty.getDifficulty().getSamples()){
                        BuildManager.loadSamples(sample, RdManager.getBattleArenaRoots());
                    }

                    //End crystals
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
            }
        }
    }

    private static void finish(){

        //Ini
        double life = Difficulty.getDifficulty().getLife();
        BarColor color = Difficulty.getDifficulty().getBarColor();
        String name = Difficulty.getDifficulty().getNameColor() + Difficulty.getDifficulty().getName();
        World end = RdManager.getWorld();

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
        dragon.setCustomName(Difficulty.getDifficulty().getNameColor() + Difficulty.getDifficulty().getName());
        dragon.setCustomNameVisible(true);

        //Starting fight and bar actualisation task
        RdManager.createBar(name, color);
        DragonFight task = new DragonFight();
        DragonCommand.setTaskid(Bukkit.getScheduler().scheduleSyncRepeatingTask(RdManager.getPlugin(),task, 0, 1));

        //player.sendMessage(ChatColor.GREEN + Lang.get("fightStarting"));

        RdManager.actualiseFightStatue();

    }
}
