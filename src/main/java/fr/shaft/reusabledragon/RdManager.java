package fr.shaft.reusabledragon;

import fr.shaft.reusabledragon.build.BuildManager;
import fr.shaft.reusabledragon.build.Sample;
import fr.shaft.reusabledragon.commands.DragonCommand;
import fr.shaft.reusabledragon.commands.SaveAreaCommand;
import fr.shaft.reusabledragon.enumerations.Difficulty;
import fr.shaft.reusabledragon.listeners.*;
import fr.shaft.reusabledragon.task.ChestSpawn;
import org.bukkit.*;
import org.bukkit.boss.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RdManager {

    /*---------------
        Elements
    ---------------*/

    //plugin
    private static JavaPlugin plugin;
    public static JavaPlugin getPlugin() {
        return plugin;
    }

    //required materials
    private static final Map<Difficulty, Map<Material, Integer>> requiredMaterials = new HashMap<>();
    public static Map<Difficulty, Map<Material, Integer>> getRequiredMaterials() {
        return requiredMaterials;
    }

    //rewards
    private static final Map<Difficulty, ArrayList<String[]>> rewards = new HashMap<>();
    public static Map<Difficulty, ArrayList<String[]>> getRewards() {
        return rewards;
    }

    //End
    private static World world;
    public static World getWorld() {
        return world;
    }

    //boss bar
    private static BossBar bar;
    public static BossBar getBar() {
        return bar;
    }

    //Fight Statue
    private static boolean fightStatue;
    public static boolean getFightStatue() {
        return fightStatue;
    }
    public static boolean actualiseFightStatue() {

        boolean check = false;
        assert world != null;
        for (Entity entity : world.getEntities()) {

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

    //Spawn method
    private static int spawnMethod;

    /*---------------
         Methods
     ---------------*/

    //Core
    public static void setUp(JavaPlugin pl){

        //Ini
        plugin = pl;

        //config
        pl.saveDefaultConfig();

        //Server alert
        System.out.println("--------------------------------------------------------------------");
        System.out.println(" Reusable Dragon is starting now !! ");
        System.out.println(" ");
        UpdateChecker updater = new UpdateChecker(pl, 92504);
        if (updater.checkForUpdates()) {
            System.out.println("There is a new update available !");
        }
        else{
            System.out.println("You have the latest version of the plugin !");
        }
        System.out.println(" ");
        System.out.println("If you just updated the plugin to a new version don't forget to look");
        System.out.println("at the CONFIGLINES file and PATCHNOTES file located in github, find the link in the Reusable Dragon spigot page");
        System.out.println(" ");


        //Load data from config file
        loadConfig();

        //Listeners
        registerListeners();

        //commands
        registerCommands();

        //Fight Statue ini
        fightStatue = actualiseFightStatue();

        //Dragon checking
        dragonChecking();

        //Save Area if there is no files
        saveArea();

        //Load stats
        StatsManager.loadStats();

        //Server alert
        System.out.println("Reusable Dragon successfully started ! have fun : )");
        System.out.println("--------------------------------------------------------------------");

    }

    //Save Area
    private static void saveArea(){

        //Save area if there is no file, to prevent error
        for(Difficulty difficulty : Difficulty.values()){

            File area = new File(plugin.getDataFolder() + "/DATA/" + difficulty.getFileName().split("/")[0]);
            File entities = new File(plugin.getDataFolder() + "/DATA/" + difficulty.getFileName().split("/")[1]);
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

    }

    //Load config
    private static void loadConfig(){

        FileConfiguration config = plugin.getConfig();
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        config.options().copyDefaults(true);
        configFile.delete();
        plugin.saveDefaultConfig();
        plugin.reloadConfig();





        //Dragon Attributes
        {

            //EASY
            String[] words = config.getString("difficulties.easy").split("; ");

            double life = Double.parseDouble(words[0]);
            if (life < 1.0) {
                life = 1.0;
            }
            double damage = Double.parseDouble(words[1]);
            if (damage < 1.0) {
                damage = 1.0;
            }

            Difficulty.EASY.setLife(life);
            Difficulty.EASY.setDamage(damage);
            Difficulty.EASY.setExp(config.getInt("exp.easy"));

            //MEDIUM
            words = config.getString("difficulties.medium").split("; ");

            life = Double.parseDouble(words[0]);
            if (life < 1.0) {
                life = 1.0;
            }
            damage = Double.parseDouble(words[1]);
            if (damage < 1.0) {
                damage = 1.0;
            }

            Difficulty.MEDIUM.setLife(life);
            Difficulty.MEDIUM.setDamage(damage);
            Difficulty.MEDIUM.setExp(config.getInt("exp.medium"));


            //hard
            words = config.getString("difficulties.hard").split("; ");

            life = Double.parseDouble(words[0]);
            if (life < 1.0) {
                life = 1.0;
            }
            damage = Double.parseDouble(words[1]);
            if (damage < 1.0) {
                damage = 1.0;
            }

            Difficulty.HARD.setLife(life);
            Difficulty.HARD.setDamage(damage);
            Difficulty.HARD.setExp(config.getInt("exp.hard"));
        }

        //Lang
        lang = plugin.getConfig().getString("language");

        //required materials
        {

            for (Difficulty difficulty : Difficulty.values()) {

                Map<Material, Integer> materials = new HashMap<>();
                for (String string : config.getStringList("required." + difficulty.getStringValue())) {

                    String[] words = string.split(" ");
                    Material material = Material.getMaterial(words[0]);
                    if (material != null) {

                        int quantity = Integer.parseInt(words[1]);
                        if (quantity > 0) {

                            materials.put(material, quantity);

                        }

                    }

                }

                requiredMaterials.put(difficulty, materials);
            }
        }

        //rewards
        {

            for (Difficulty difficulty : Difficulty.values()) {

                ArrayList<String[]> reward = new ArrayList<>();

                for (String string : config.getStringList("rewards." + difficulty.getStringValue())) {

                    String[] words = string.split("; ");

                    reward.add(words);
                }

                rewards.put(difficulty, reward);
            }
        }

        //world
        world = plugin.getServer().getWorld(plugin.getConfig().getString("world"));

        //Locations
        {
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

        //Spawn Method
        {
            String[] line = plugin.getConfig().getString("spawnmethod").split(";");
            spawnMethod = Integer.parseInt(line[0]);
            if(spawnMethod != 1 && spawnMethod != 2){
                spawnMethod = 1;
            }
            if(spawnMethod == 2){

                Location location = new Location(world, Integer.parseInt(line[1]), Integer.parseInt(line[2]), Integer.parseInt(line[3]));
                location.getBlock().setType(Material.CHEST);

                Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new ChestSpawn(location), 0L, 30L);

            }
        }

    }

    //Commands
    private static void registerCommands() {

        if(spawnMethod == 1){
            plugin.getCommand("dragon").setExecutor(new DragonCommand());
        }
        plugin.getCommand("rdsave").setExecutor(new SaveAreaCommand());

    }

    //Listeners
    private static void registerListeners() {


        plugin.getServer().getPluginManager().registerEvents(new OnPlayerBuild(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new OnDamage(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new OnEntityDeath(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new OnEntityDamagedByEntity(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new OnPlayerDie(), plugin);
    }

    //Player in area
    public static boolean inArea(Location roots, Location end, Location pos) {

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
    private static void dragonChecking() {

        for (Entity entity : world.getEntities()) {

            if (entity instanceof EnderDragon || entity instanceof EnderCrystal) {

                entity.remove();

            }
        }
    }

    //BossBar
    public static void createBar(String name, BarColor barColor) {

        bar = Bukkit.createBossBar(name, barColor, BarStyle.SOLID, BarFlag.CREATE_FOG, BarFlag.DARKEN_SKY, BarFlag.PLAY_BOSS_MUSIC);

    }

}
