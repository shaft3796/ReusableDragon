package fr.shaft.reusabledragon;

import fr.shaft.reusabledragon.build.BuildManager;
import fr.shaft.reusabledragon.commands.DragonCommand;
import fr.shaft.reusabledragon.commands.SaveAreaCommand;
import fr.shaft.reusabledragon.listeners.OnPlayerBuild;
import fr.shaft.reusabledragon.task.DragonFight;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.boss.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

    //Lang
    private static String lang;
    public static String getLang() {
        return lang;
    }

    /*---------------
          Build
    ---------------*/

    public RdManager(JavaPlugin pl){

        //Ini
        plugin = pl;

        //Lang registration
        langRegistration();

        //materials
        registerMaterials();

        //rewards
        rewardsRegistration();

        //Data registration
        worldRegistration();

        //Locations registration
        locationsRegistration();

        //Listeners
        registerListeners();

        //commands
        registerCommands();

        //File loading
        BuildManager.generateSamples();
        BuildManager.generateEntities(world);

        //Fight Statue ini
        fightStatue = actualiseFightStatue();

        //Dragon checking
        dragonChecking();

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

        //NO BUILD LAND ROOTS & END
        String str = plugin.getConfig().getString("endProtectionRoots");
        String[] splited = str.split(" ");

        int inirootX = Integer.parseInt(splited[0]);
        int inirootY = Integer.parseInt(splited[1]);
        int inirootZ = Integer.parseInt(splited[2]);

        str = plugin.getConfig().getString("endProtectionEnd");
        splited = str.split(" ");

        int iniendX = Integer.parseInt(splited[0]);
        int iniendY = Integer.parseInt(splited[1]);
        int iniendZ = Integer.parseInt(splited[2]);

        //Conversion
        int rootX = Math.min(inirootX, iniendX);
        int rootY = Math.min(inirootY, iniendY);
        int rootZ = Math.min(inirootZ, iniendZ);

        int endX = Math.max(inirootX, iniendX);
        int endY = Math.max(inirootY, iniendY);
        int endZ = Math.max(inirootZ, iniendZ);

        noBuildLandRoots = new Location(world, rootX, rootY, rootZ);
        noBuildLandEnd = new Location(world, endX, endY, endZ);

        //BATTLE ARENA ROOTS & END
        str = plugin.getConfig().getString("battleArenaRoots");
        splited = str.split(" ");

        inirootX = Integer.parseInt(splited[0]);
        inirootY = Integer.parseInt(splited[1]);
        inirootZ = Integer.parseInt(splited[2]);

        str = plugin.getConfig().getString("battleArenaEnd");
        splited = str.split(" ");

        iniendX = Integer.parseInt(splited[0]);
        iniendY = Integer.parseInt(splited[1]);
        iniendZ = Integer.parseInt(splited[2]);

        //Conversion
        rootX = Math.min(inirootX, iniendX);
        rootY = Math.min(inirootY, iniendY);
        rootZ = Math.min(inirootZ, iniendZ);

        endX = Math.max(inirootX, iniendX);
        endY = Math.max(inirootY, iniendY);
        endZ = Math.max(inirootZ, iniendZ);

        //Converted values
        battleArenaRoots = new Location(world, rootX, rootY, rootZ);
        battleArenaEnd = new Location(world, endX, endY, endZ);

    }

    //Lang registration
    private static void langRegistration(){

        lang = plugin.getConfig().getString("language");

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

    //Dragon Checking
    private static void dragonChecking(){

        for(Entity entity : world.getEntities()){

            if(entity instanceof EnderDragon || entity instanceof EnderCrystal){

                entity.remove();

            }
        }
    }

    //Git test
    private static void gitTest(){
        System.out.println("This is a test");
    }





}
