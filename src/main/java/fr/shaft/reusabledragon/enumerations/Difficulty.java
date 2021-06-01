package fr.shaft.reusabledragon.enumerations;

import fr.shaft.reusabledragon.build.RdEntity;
import fr.shaft.reusabledragon.build.Sample;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;

import java.util.ArrayList;

public enum Difficulty {

    EASY (200.0, 15.0, "Easy Dragon", BarColor.PINK, ChatColor.LIGHT_PURPLE, "easy", 500, "RdAreaEasy.rd/RdEntitiesEasy.rd"),
    MEDIUM (400.0, 20.0, "Medium Dragon", BarColor.PURPLE, ChatColor.DARK_PURPLE, "medium", 750, "RdAreaMedium.rd/RdEntitiesMedium.rd"),
    HARD (800.0, 30.0, "Hard Dragon", BarColor.RED, ChatColor.DARK_RED, "hard", 1000, "RdAreaHard.rd/RdEntitiesHard.rd");

    private double life;
    private double damage;
    private final String name;
    private final BarColor barColor;
    private final ChatColor nameColor;
    private final String stringValue;
    private int exp;
    private final String fileName;
    private ArrayList<RdEntity> entities;
    private ArrayList<Sample> samples;

    public double getLife() {
        return life;
    }
    public String getFileName() {
        return fileName;
    }
    public void setLife(Double life) {this.life = life;}
    public double getDamage() {
        return damage;
    }
    public void setDamage(double damage) {
        this.damage = damage;
    }
    public ChatColor getNameColor() {
        return nameColor;
    }
    public BarColor getBarColor() {
        return barColor;
    }
    public String getName() {
        return name;
    }
    public String getStringValue() { return stringValue; }
    public int getExp() {
        return exp;
    }
    public void setExp(int exp){
        this.exp = exp;
    }
    public ArrayList<RdEntity> getEntities() {
        return entities;
    }
    public ArrayList<Sample> getSamples() {
        return samples;
    }
    public void setEntities(ArrayList<RdEntity> entities){this.entities = entities; }
    public void setSamples(ArrayList<Sample> samples){this.samples = samples; }

    private static Difficulty difficulty = Difficulty.EASY;
    public static Difficulty getDifficulty() {
        return difficulty;
    }
    public static void setCurrentDifficulty(Difficulty dif){

        difficulty = dif;
    }


    Difficulty(Double life, Double damage, String name, BarColor barColor, ChatColor chatColor, String stringValue, int exp, String fileName){

        this.life = life;
        this.barColor = barColor;
        this.name = name;
        this.nameColor = chatColor;
        this.damage = damage;
        this.stringValue = stringValue;
        this.exp = exp;
        this.fileName = fileName;


    }


}
