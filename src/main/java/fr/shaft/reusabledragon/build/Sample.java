package fr.shaft.reusabledragon.build;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;

public class Sample {

    /*---------------
        Elements
    ---------------*/

    //roots
    private Location roots;
    public Location getRoots() {
        return roots;
    }

    //size
    private int sizeX;
    public int getSizeX() {
        return sizeX;
    }

    private int sizeY;
    public int getSizeY() {
        return sizeY;
    }

    private int sizeZ;
    public int getSizeZ() {
        return sizeZ;
    }

    //Blocs --> Loop Δx(Δz(Δy))
    private ArrayList<Material> blocs = new ArrayList<>();
    public ArrayList<Material> getBlocs() {
        return blocs;
    }
    public void setBlocs(ArrayList<Material> blocs) {
        this.blocs = blocs;
    }
    public void addBlocs(Material material){

        this.blocs.add(material);
    }

    /*---------------
         Builder
    ---------------*/

    public Sample(Location r, int sx, int sy, int sz){

        this.roots = r;
        this.sizeX = sx;
        this.sizeZ = sz;
        this.sizeY = sy;

    }

}
