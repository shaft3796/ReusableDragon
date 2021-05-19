package fr.shaft.reusabledragon.build;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class RdEntity {

    //Attributes
    private EntityType entityType;
    public EntityType getEntityType() {
        return entityType;
    }

    private Location pos;
    public Location getPos() {
        return pos;
    }

    //Builder
    public RdEntity(EntityType type, Location loc){

        entityType = type;
        pos = loc;
    }
}
