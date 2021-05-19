package fr.shaft.reusabledragon;

public class Lang {

    public static String get(String path){

        return RdManager.getPlugin().getConfig().getString(RdManager.getLang() + "." + path);

    }
}
