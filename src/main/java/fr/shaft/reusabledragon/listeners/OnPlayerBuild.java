package fr.shaft.reusabledragon.listeners;

import fr.shaft.reusabledragon.RdManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class OnPlayerBuild implements Listener {

    World world = RdManager.getWorld();
    Location roots = RdManager.getNoBuildLandRoots();
    Location end = RdManager.getNoBuildLandEnd();
    Location roots2 = RdManager.getBattleArenaRoots();
    Location end2 = RdManager.getBattleArenaEnd();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){

        //Ini
        Player player = event.getPlayer();

        //postcond
        if(player.isOp() || player.getWorld() != world){
            return;
        }

        //Check
        if(RdManager.inArea(roots, end, event.getBlock().getLocation())){

            if(RdManager.inArea(roots2, end2, event.getBlock().getLocation())){

                if(RdManager.getFightStatue()){
                    return;
                }else{
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "[Re Dragon] Hey, Vous ne pouvez pas casser de blocs ici, il n'y a pas de boss !!");
                }

            }
            else{
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "[Re Dragon] Hey, Vous ne pouvez pas casser de blocs ici !!");
            }
        }


    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){

        //Ini
        Player player = event.getPlayer();

        //postcond
        if(player.isOp() || player.getWorld() != world){
            return;
        }

        //Check
        if(RdManager.inArea(roots, end, event.getBlock().getLocation())){

            if(RdManager.inArea(roots2, end2, event.getBlock().getLocation())){

                if(RdManager.getFightStatue()){

                    if(event.getBlock().getType() == Material.END_CRYSTAL || event.getBlock().getType() == Material.TNT){
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.RED + "[Re Dragon] Hey, Vous ne pouvez pas poser ce type de blocs ici !!");
                    }

                }else{
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "[Re Dragon] Hey, Vous ne pouvez pas poser de blocs ici, il n'y a pas de boss !!");
                }

            }
            else{
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "[Re Dragon] Hey, Vous ne pouvez pas poser de blocs ici !!");
            }
        }


    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        //INI
        World world = RdManager.getWorld();
        Location roots = new Location(world, -10, 0, -10);
        Location end = new Location(world, 10, 200, 10);

        if (event.getMaterial() == Material.END_CRYSTAL && event.getAction() == Action.RIGHT_CLICK_BLOCK && !event.getPlayer().isOp() && event.getPlayer().getWorld() == RdManager.getWorld()) {

            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "[Re Dragon] Hey, utilisez /dragon pour faire spawn le dragon !!!");

        }
    }
}
