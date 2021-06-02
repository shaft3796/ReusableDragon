package fr.shaft.reusabledragon.build;

import fr.shaft.reusabledragon.RdManager;
import fr.shaft.reusabledragon.enumerations.Difficulty;
import fr.shaft.reusabledragon.save.SaveModule;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class BuildManager {

    /*---------------
         Methods
    ---------------*/

    //Sampling
    public static void sampleRegion(Location roots, Location end, Difficulty difficulty) {

        //Ini
        int inirootX = roots.getBlockX();
        int inirootY = roots.getBlockY();
        int inirootZ = roots.getBlockZ();

        int iniendX = end.getBlockX();
        int iniendY = end.getBlockY();
        int iniendZ = end.getBlockZ();

        //Converted values

        int rootX = Math.min(inirootX, iniendX);
        int rootY = Math.min(inirootY, iniendY);
        int rootZ = Math.min(inirootZ, iniendZ);

        int endX = Math.max(inirootX, iniendX);
        int endY = Math.max(inirootY, iniendY);
        int endZ = Math.max(inirootZ, iniendZ);

        int sizeX = endX - rootX;
        int sizeY = endY - rootY;
        int sizeZ = endZ - rootZ;

        World world = roots.getWorld();

        difficulty.setSamples(new ArrayList<>());

        //Loop
        for(int y = rootY; y<=rootY+sizeY; y++){

            Location loc = new Location(world, rootX, y, rootZ);
            Sample sample = new Sample(loc, sizeX, 1, sizeZ);
            difficulty.getSamples().add(sample);

        }

    }

    public static void registerSamples(Sample sample){

        //Ini
        Location roots = sample.getRoots();
        int sx = sample.getSizeX();
        int sz = sample.getSizeZ();

        for(int x = roots.getBlockX(); x<= roots.getBlockX()+sx; x++){
            for(int z = roots.getBlockZ(); z<= roots.getBlockZ()+sz; z++){

                Location loc = new Location(sample.getRoots().getWorld(), x, roots.getBlockY(), z);
                sample.addBlocs(loc.getBlock().getType());

            }
        }

    }

    public static void convertSamples(Location roots, Location end, Difficulty difficulty){

        //Ini
        int inirootX = roots.getBlockX();
        int inirootY = roots.getBlockY();
        int inirootZ = roots.getBlockZ();

        int iniendX = end.getBlockX();
        int iniendY = end.getBlockY();
        int iniendZ = end.getBlockZ();

        //Converted values

        int rootX = Math.min(inirootX, iniendX);
        int rootY = Math.min(inirootY, iniendY);
        int rootZ = Math.min(inirootZ, iniendZ);

        for(Sample sample : difficulty.getSamples()){

            sample.getRoots().setX(sample.getRoots().getBlockX() - rootX);
            sample.getRoots().setY(sample.getRoots().getBlockY() - rootY);
            sample.getRoots().setZ(sample.getRoots().getBlockZ() - rootZ);

        }

    }

    public static void loadSamples(Sample sample, Location roots){


            //Ini
            int sx = sample.getSizeX();
            int sz = sample.getSizeZ();


            int currentBlock = 0;

            for(int x = roots.getBlockX(); x<= roots.getBlockX()+sx; x++){
                for(int z = roots.getBlockZ(); z<= roots.getBlockZ()+sz; z++){

                    Location loc = new Location(roots.getWorld(), x + sample.getRoots().getBlockX(), roots.getBlockY() + sample.getRoots().getBlockY(), z + sample.getRoots().getBlockZ());
                    loc.getBlock().setType(sample.getBlocs().get(currentBlock));
                    currentBlock++;
            }

        }

    }

    public static void saveSamples(Difficulty difficulty){

        SaveModule sv = new SaveModule(RdManager.getPlugin());
        sv.createDataFile(difficulty.getFileName().split("/")[0]);
        StringBuilder content = new StringBuilder();
        for(Sample sample : difficulty.getSamples()){

            for(Material mtr : sample.getBlocs()){
                content.append(mtr.name()).append(";");
            }
            content.append("/");
            content.append(sample.getRoots().getBlockX()).append(";");
            content.append(sample.getRoots().getBlockY()).append(";");
            content.append(sample.getRoots().getBlockZ()).append(";");
            content.append(sample.getSizeX()).append(";");
            content.append(sample.getSizeY()).append(";");
            content.append(sample.getSizeZ()).append(";");
            content.append("#");
        }
        SaveModule.writeFile(sv.getDataFile(), content.toString());
    }

    public static void generateSamples(Difficulty difficulty){

        //Ini
        SaveModule sv = new SaveModule(RdManager.getPlugin());
        sv.createDataFile(difficulty.getFileName().split("/")[0]);
        difficulty.setSamples(new ArrayList<>());

        String content = SaveModule.readFile(sv.getDataFile());

        //First Split
        String[] firstSplit = content.split("#");

        //sample per sample
        for(String sampleData : firstSplit){

            String[] secondSplit = sampleData.split("/");
            String[] blocs = secondSplit[0].split(";");
            String[] data = secondSplit[1].split(";");

            Sample sample = new Sample(new Location(RdManager.getWorld(), Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2])), Integer.parseInt(data[3]), Integer.parseInt(data[4]), Integer.parseInt(data[5]));
            for(String bloc : blocs){
                sample.addBlocs(Material.getMaterial(bloc));
            }

            difficulty.getSamples().add(sample);
        }

    }

    //Entities
    public static void registerEntities(World world, Difficulty difficulty){

        difficulty.setEntities(new ArrayList<>());
        for(Entity entity : world.getEntities()){

            if(entity.getType() == EntityType.ENDER_CRYSTAL){

                RdEntity ent = new RdEntity(EntityType.ENDER_CRYSTAL, entity.getLocation());
                difficulty.getEntities().add(ent);
            }
        }
    }

    public static void loadEntity(RdEntity entity, World world){

        //particle configuration
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(204, 0, 255), 2);

        Location from = new Location(world, 0.5, 66, 0.5);
        Location to = entity.getPos();
        double space = 0.1;
        double distance = from.distance(to);

        Vector start = from.toVector();
        Vector end = to.toVector();

        Vector vector = end.clone().subtract(start).normalize().multiply(space);

        double covered = 0;

        for (; covered < distance; start.add(vector)) {

            world.spawnParticle(Particle.REDSTONE, start.getX(), start.getY(), start.getZ(), 10, dustOptions);

            covered += space;
        }

        world.playSound(new Location(RdManager.getWorld(), 0, 70, 0), Sound.BLOCK_GLASS_BREAK, 10, 29);


        world.spawnEntity(entity.getPos(), entity.getEntityType());
    }

    public static void saveEntities(Difficulty difficulty){

        SaveModule sv = new SaveModule(RdManager.getPlugin());
        sv.createDataFile(difficulty.getFileName().split("/")[1]);
        StringBuilder content = new StringBuilder();
        for(RdEntity entity : difficulty.getEntities()){

            content.append(entity.getEntityType().name()).append(";");
            content.append(entity.getPos().getX()).append(";");
            content.append(entity.getPos().getY()).append(";");
            content.append(entity.getPos().getZ()).append(";");
            content.append("#");
        }
        SaveModule.writeFile(sv.getDataFile(), content.toString());

    }

    public static void generateEntities(World world, Difficulty difficulty){

        //Ini
        SaveModule sv = new SaveModule(RdManager.getPlugin());
        sv.createDataFile(difficulty.getFileName().split("/")[1]);
        difficulty.setEntities(new ArrayList<>());

        String content = SaveModule.readFile(sv.getDataFile());

        //First Split
        String[] firstSplit = content.split("#");

        //entity per entity
        for(String entityData : firstSplit){

            String[] data = entityData.split(";");

            //PostCond
            if(!data[0].equals("") && !data[1].equals("") && !data[2].equals("") && !data[3].equals("") ){
                RdEntity ent = new RdEntity(EntityType.valueOf(data[0]), new Location(world, Double.parseDouble(data[1]), Double.parseDouble(data[2]), Double.parseDouble(data[3])));
                difficulty.getEntities().add(ent);
            }

        }

    }

}
