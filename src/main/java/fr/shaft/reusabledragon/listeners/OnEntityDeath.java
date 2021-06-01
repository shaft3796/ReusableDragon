package fr.shaft.reusabledragon.listeners;

import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class OnEntityDeath implements Listener {

    @EventHandler
    public void onEntityDeath (EntityDeathEvent event) {

        if(event.getEntity() instanceof EnderDragon){

            event.setDroppedExp(0);

        }

    }


}
