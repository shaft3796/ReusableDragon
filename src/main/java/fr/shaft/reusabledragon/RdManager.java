package fr.shaft.reusabledragon;

import fr.shaft.reusabledragon.build.BuildManager;
import fr.shaft.reusabledragon.commands.DragonCommand;
import fr.shaft.reusabledragon.commands.SaveAreaCommand;
import fr.shaft.reusabledragon.listeners.OnPlayerBuild;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RdManager {

    /*---------------
        Elements
    ---------------*/

    //plugin
    private static JavaPlugin plugin;
    public static JavaPlugin getPlugin(){
        return plugin;
    }

    //required materials
    private static final Map<Material, Integer> requiredMaterials = new HashMap<>();
    public static Map<Material, Integer> getRequiredMaterials() {
        return requiredMaterials;
    }

    //rewards
    private static final ArrayList<String[]> rewards = new ArrayList<>();
    public static ArrayList<String[]> getRewards() {
        return rewards;
    }

    //End
    private static World world;
    public static World getWorld() {
        return world;
    }

    //boss bar
    private static BossBar bar = Bukkit.createBossBar("EnderDragon", BarColor.PINK, BarStyle.SOLID, BarFlag.CREATE_FOG, BarFlag.DARKEN_SKY, BarFlag.PLAY_BOSS_MUSIC);
    public static BossBar getBar() {
        return bar;
    }

    //Fight Statue
    private static boolean fightStatue;
    public static boolean getFightStatue() {
        return fightStatue;
    }
    public static boolean actualiseFightStatue(){

        boolean check = false;
        assert world != null;
        for(Entity entity : world.getEntities()){

            if (entity instanceof EnderDragon) {
                check = true;
                break;
            }

        }

        fightStatue = check;
        return fightStatue;
    }

    //End protection
    private static Location noBuildLandRoots;
    public static Location getNoBuildLandRoots() {
        return noBuildLandRoots;
    }

    private static Location noBuildLandEnd;
    public static Location getNoBuildLandEnd() {
        return noBuildLandEnd;
    }

    //Battle Arena
    private static Location battleArenaRoots;
    public static Location getBattleArenaRoots() {
        return battleArenaRoots;
    }

    private static Location battleArenaEnd;
    public static Location getBattleArenaEnd() {
        return battleArenaEnd;
    }

    /*---------------
          Build
    ---------------*/

    public RdManager(JavaPlugin pl){

        //Ini
        plugin = pl;

        //commands
        registerCommands();

        //Listeners
        registerListeners();

        //materials
        registerMaterials();

        //rewards
        rewardsRegistration();

        //Data registration
        worldRegistration();

        //Locations registration
        locationsRegistration();

        //File loading
        BuildManager.generateSamples();
        BuildManager.generateEntities(world);

        //Fight Statue ini
        fightStatue = actualiseFightStatue();

    }

    /*---------------
         Methods
     ---------------*/

    //Commands
    private static void registerCommands(){

        plugin.getCommand("dragon").setExecutor(new DragonCommand());
        plugin.getCommand("rdsave").setExecutor((new SaveAreaCommand()));

    }

    //Listeners
    private static void registerListeners(){

        //Player Build ( place / break ) Event
        plugin.getServer().getPluginManager().registerEvents(new OnPlayerBuild(), plugin);
    }

    //register required materials
    private static void registerMaterials(){

        FileConfiguration config = plugin.getConfig();

        for(String string : config.getStringList("required")){

            String[] words = string.split(" ");
            Material material = Material.getMaterial(words[0]);
            if(material != null){

                int quantity = Integer.parseInt(words[1]);
                if(quantity > 0){

                    requiredMaterials.put(material, quantity);

                }

            }

        }
    }

    //world registration
    private static void worldRegistration(){
        world = plugin.getServer().getWorld(plugin.getConfig().getString("world"));
    }

    //reward registration
    private static void rewardsRegistration(){

        //Ini
        FileConfiguration config = plugin.getConfig();

        for(String string : config.getStringList("rewards")){

            String[] words = string.split("; ");

            rewards.add(words);
            }
        }

    //locations registration
    private static void locationsRegistration(){

        String str = plugin.getConfig().getString("endProtectionRoots");
        String[] splited = str.split(" ");
        noBuildLandRoots = new Location(world, Integer.parseInt(splited[0]), Integer.parseInt(splited[1]), Integer.parseInt(splited[2]));

        str = plugin.getConfig().getString("endProtectionEnd");
        splited = str.split(" ");
        noBuildLandEnd = new Location(world, Integer.parseInt(splited[0]), Integer.parseInt(splited[1]), Integer.parseInt(splited[2]));

        str = plugin.getConfig().getString("battleArenaRoots");
        splited = str.split(" ");
        battleArenaRoots = new Location(world, Integer.parseInt(splited[0]), Integer.parseInt(splited[1]), Integer.parseInt(splited[2]));

        str = plugin.getConfig().getString("battleArenaEnd");
        splited = str.split(" ");
        battleArenaEnd = new Location(world, Integer.parseInt(splited[0]), Integer.parseInt(splited[1]), Integer.parseInt(splited[2]));


    }

    //Player in area
    public static boolean inArea(Location roots, Location end, Location pos){

        //Ini
        int inirootX = roots.getBlockX();
        int inirootY = roots.getBlockY();
        int inirootZ = roots.getBlockZ();

        int iniendX = end.getBlockX();
        int iniendY = end.getBlockY();
        int iniendZ = end.getBlockZ();

        //Final values

        int rootX = Math.min(inirootX, iniendX);
        int rootY = Math.min(inirootY, iniendY);
        int rootZ = Math.min(inirootZ, iniendZ);

        int endX = Math.max(inirootX, iniendX);
        int endY = Math.max(inirootY, iniendY);
        int endZ = Math.max(inirootZ, iniendZ);

        int posX = pos.getBlockX();
        int posY = pos.getBlockY();
        int posZ = pos.getBlockZ();

        return posX < endX && posX > rootX && posY < endY && posY > rootY && posZ < endZ && posZ > rootZ;
    }





}