package fr.shaft.reusabledragon.task;

import fr.shaft.reusabledragon.Lang;
import fr.shaft.reusabledragon.RdManager;
import fr.shaft.reusabledragon.build.BuildManager;
import fr.shaft.reusabledragon.build.Sample;
import fr.shaft.reusabledragon.commands.DragonCommand;
import fr.shaft.reusabledragon.enumerations.Difficulty;
import org.bukkit.*;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class DragonFight implements Runnable{

    private static final World end = RdManager.getWorld();
    private static BossBar bar;

    public DragonFight(){

        bar = RdManager.getBar();
    }

    @Override
    public void run() {

        //INI
        double health = 0.0;
        double maxhealth = Difficulty.getDifficulty().getLife();
        boolean alive = false;

        //Update
        for(Entity entity : end.getEntities()) {
            if(entity instanceof EnderDragon) {
                EnderDragon dragon = (EnderDragon)entity;
                health = dragon.getHealth();
                alive = true;
                RdManager.actualiseFightStatue();
            }
        }

        //Main
        for(Player player : Bukkit.getOnlinePlayers()) {

            //Update bar
            if(player.getWorld() == end && alive) {
                bar.addPlayer(player);
            } else {
                bar.removePlayer(player);
            }

            //Update status & stop fight
            if(!alive){
                RdManager.actualiseFightStatue();
                Bukkit.getScheduler().cancelTask(DragonCommand.getTaskid());

                //regen arena
                for(Sample sample : BuildManager.getSamples()){
                    BuildManager.loadSamples(sample, RdManager.getBattleArenaRoots());
                }

                //rewards
                ArrayList<Player> players = new ArrayList<>();

                //preparing player list
                for(Player pl : RdManager.getWorld().getPlayers()){

                    if(RdManager.inArea(RdManager.getBattleArenaRoots(),RdManager.getBattleArenaEnd(), player.getLocation())){

                        player.sendMessage(ChatColor.GOLD + Lang.get("youKilledDragon"));
                        players.add(pl);

                        //XP DISTRIBUTION
                        pl.giveExp(Difficulty.getDifficulty().getExp());
                    }

                }

                //reward
                for(String[] reward : RdManager.getRewards().get(Difficulty.getDifficulty())){

                    if(Boolean.parseBoolean(reward[3])){

                        for(Player pl : players){

                            double probability = 100/Double.parseDouble(reward[2]);
                            if(new Random().nextInt((int)probability)==0){

                                if(pl.getInventory().firstEmpty()<0){

                                    pl.getWorld().dropItem(pl.getLocation(), new ItemStack(Objects.requireNonNull(Material.getMaterial(reward[0])), Integer.parseInt(reward[1])));
                                    pl.sendMessage(ChatColor.GRAY + Lang.get("youGot") + ChatColor.ITALIC + reward[0]  + " x" + reward[1]);
                                }
                                else{
                                    pl.getInventory().addItem(new ItemStack(Objects.requireNonNull(Material.getMaterial(reward[0])), Integer.parseInt(reward[1])));
                                    pl.sendMessage(ChatColor.GRAY + Lang.get("youGot") + ChatColor.ITALIC + reward[0]  + " x" + reward[1]);
                                }
                            }

                        }

                    }
                    else{

                        double probability = 100/Double.parseDouble(reward[2]);
                        if(new Random().nextInt((int)probability)==0){

                            int index = new Random().nextInt(players.size());
                            Player pl = players.get(index);

                            if(pl.getInventory().firstEmpty()<0){

                                pl.getWorld().dropItem(pl.getLocation(), new ItemStack(Objects.requireNonNull(Material.getMaterial(reward[0])), Integer.parseInt(reward[1])));
                                pl.sendMessage(ChatColor.GRAY + Lang.get("youGot") + ChatColor.ITALIC + reward[0]  + " x" + reward[1]);
                            }
                            else{
                                pl.getInventory().addItem(new ItemStack(Objects.requireNonNull(Material.getMaterial(reward[0])), Integer.parseInt(reward[1])));
                                pl.sendMessage(ChatColor.GRAY + Lang.get("youGot") + ChatColor.ITALIC + reward[0]  + " x" + reward[1]);
                            }
                        }

                    }

                }
            }

            //Update bar progress
            bar.setProgress(health / maxhealth);
            if(bar.getProgress() == 0.0) {
                bar.removeAll();
            }
        }
    }
}

