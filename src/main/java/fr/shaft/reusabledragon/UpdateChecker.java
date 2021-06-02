package fr.shaft.reusabledragon;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Consumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class UpdateChecker {

    private URL checkURL;
    private String newVersion;
    private JavaPlugin plugin;

    public UpdateChecker(JavaPlugin plugin, int projectID) {
        this.plugin = plugin;
        this.newVersion = plugin.getDescription().getVersion();
        try {
            this.checkURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + projectID);
        } catch (MalformedURLException ignored) {
        }
    }

    public boolean checkForUpdates(){
        URLConnection con = null;
        try {
            con = checkURL.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.newVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return !plugin.getDescription().getVersion().equals(newVersion);
    }

}

