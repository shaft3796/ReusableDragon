package fr.shaft.reusabledragon.listeners;

import fr.shaft.reusabledragon.RdManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class OnEntityDeath implements Listener {

    @EventHandler
    public void onEntityDeath (EntityDeathEvent event) {

        if(event.getEntity() instanceof EnderDragon){

            Bukkit.getScheduler().scheduleSyncDelayedTask(RdManager.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    event.getEntity().remove();
                }
            }, 60L);


        }

    }


}
