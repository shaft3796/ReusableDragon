package fr.shaft.reusabledragon.listeners;

import fr.shaft.reusabledragon.StatsManager;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class OnEntityDamagedByEntity implements Listener {

    @EventHandler
    public void onEntityDamagedEntityEvent(EntityDamageByEntityEvent event) {

        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();

        if(damaged instanceof Player && damager instanceof EnderDragon){

            StatsManager.updateStats(damaged.getName(), -1, -1, -1, -1, (int) event.getDamage());

        }
        else if(damaged instanceof EnderDragon && damager instanceof Player){

            StatsManager.updateStats(damager.getName(), -1, -1, -1, (int) event.getDamage(), -1);

        }




    }
}
