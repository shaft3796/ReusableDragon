package fr.shaft.reusabledragon.listeners;

import fr.shaft.reusabledragon.Lang;
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
                    if(event.getBlock().getType() == Material.CHEST){
                        event.setCancelled(true);
                    }
                }else{
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + Lang.get("breakInBossArea"));
                }

            }
            else{
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + Lang.get("breakInProtectedArea"));
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
                        event.getPlayer().sendMessage(ChatColor.RED + Lang.get("placeUnAuthorizedBlock"));
                    }

                }else{
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + Lang.get("placeInBossArea"));
                }

            }
            else{
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + Lang.get("placeInProtectedArea"));
            }
        }


    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (event.getMaterial() == Material.END_CRYSTAL && event.getAction() == Action.RIGHT_CLICK_BLOCK && !event.getPlayer().isOp() && event.getPlayer().getWorld() == RdManager.getWorld()) {

            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + Lang.get("howToUse"));

        }
    }
}
