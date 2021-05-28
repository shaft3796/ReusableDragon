package fr.shaft.reusabledragon.enumerations;

import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;

public enum Difficulty {

    EASY (200.0, 15.0, "Easy Dragon", BarColor.PINK, ChatColor.LIGHT_PURPLE, "easy"),
    MEDIUM (400.0, 20.0, "Medium Dragon", BarColor.PURPLE, ChatColor.DARK_PURPLE, "medium"),
    HARD (800.0, 30.0, "Hard Dragon", BarColor.RED, ChatColor.DARK_RED, "hard");

    private double life;
    private double damage;
    private String name;
    private BarColor barColor;
    private ChatColor nameColor;
    private String stringValue;

    public double getLife() {
        return life;
    }
    public double getDamage() {
        return damage;
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

    private static Difficulty difficulty;
    public static Difficulty getDifficulty() {
        return difficulty;
    }
    public static void setCurrentDifficulty(Difficulty dif){

        difficulty = dif;
    }


    Difficulty(Double life, Double damage, String name, BarColor barColor, ChatColor chatColor, String stringValue){

        this.life = life;
        this.barColor = barColor;
        this.name = name;
        this.nameColor = chatColor;
        this.damage = damage;
        this.stringValue = stringValue;

    }


}
