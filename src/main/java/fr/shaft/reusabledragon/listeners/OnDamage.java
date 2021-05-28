package fr.shaft.reusabledragon.listeners;

import fr.shaft.reusabledragon.enumerations.Difficulty;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class OnDamage implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){

        Entity damager = event.getDamager();

        if(damager instanceof EnderDragon){

            event.setDamage(event.getDamage() + Difficulty.getDifficulty().getDamage());
        }
    }

}
