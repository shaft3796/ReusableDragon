package fr.shaft.reusabledragon.listeners;

import fr.shaft.reusabledragon.StatsManager;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class OnPlayerDie implements Listener {

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event){

        Entity killer = event.getEntity().getKiller();
        if(killer instanceof EnderDragon){
            StatsManager.updateStats(event.getEntity().getName(), -1, -1, 1, -1, -1);
        }

    }
}
