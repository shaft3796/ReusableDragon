package fr.shaft.reusabledragon;

import fr.shaft.reusabledragon.RdManager;
import fr.shaft.reusabledragon.save.SaveModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StatsManager {

    //Attributes
    private static final Map<String, ArrayList<Integer>> stats = new HashMap<>();

    public static Map<String, ArrayList<Integer>> getStats() {
        return stats;
    }

    //SaveStats
    public static void saveStats() {

        //Ini
        SaveModule sm = new SaveModule(RdManager.getPlugin());
        sm.createDataFile("stats.rd");
        StringBuilder builder = new StringBuilder();

        //Content build
        for (Map.Entry<String, ArrayList<Integer>> entry : stats.entrySet()) {

            ArrayList<Integer> stats = entry.getValue();
            builder.append(entry.getKey())
                    .append(";")
                    .append(stats.get(0))
                    .append(";")
                    .append(stats.get(1))
                    .append(";")
                    .append(stats.get(2))
                    .append(";")
                    .append(stats.get(3))
                    .append(";")
                    .append(stats.get(4))
                    .append("/");

            System.out.println(builder.toString());

        }

        //Write
        SaveModule.writeFile(sm.getDataFile(), builder.toString());

    }

    //LoadStats
    public static void loadStats() {

        //Ini
        SaveModule sm = new SaveModule(RdManager.getPlugin());
        sm.createDataFile("stats.rd");

        //Read
        String content = SaveModule.readFile(sm.getDataFile());

        //Players split
        String[] playerSplit = content.split("/");

        if(playerSplit[0].equalsIgnoreCase("")){return;}


        //Content build
        for (String player : playerSplit) {

            String[] dataSplit = player.split(";");
            ArrayList<Integer> playerData = new ArrayList<>();

            playerData.add(Integer.valueOf(dataSplit[1]));
            playerData.add(Integer.valueOf(dataSplit[2]));
            playerData.add(Integer.valueOf(dataSplit[3]));
            playerData.add(Integer.valueOf(dataSplit[4]));
            playerData.add(Integer.valueOf(dataSplit[5]));

            stats.put(dataSplit[0], playerData);

        }


    }

    //UpdateStats
    public static void updateStats(String name, int display, int kill, int death, int damage, int damaged) {

        //POST COND

        //display
        if (display == -1) {
            if (stats.containsKey(name)) {
                display = stats.get(name).get(0);
            } else {
                display = 0;
            }
        }

        //kill
        if (kill == -1) {
            if (stats.containsKey(name)) {
                kill = stats.get(name).get(1);
            } else {
                kill = 0;
            }
        } else {
            if (stats.containsKey(name)) {
                kill = stats.get(name).get(1) + kill;
            }
        }

        //death
        if (death == -1) {
            if (stats.containsKey(name)) {
                death = stats.get(name).get(2);
            } else {
                death = 0;
            }
        } else {
            if (stats.containsKey(name)) {
                death = stats.get(name).get(2) + death;
            }
        }

        //damage
        if (damage == -1) {
            if (stats.containsKey(name)) {
                damage = stats.get(name).get(3);
            } else {
                damage = 0;
            }
        } else {
            if (stats.containsKey(name)) {
                damage = stats.get(name).get(3) + damage;
            }
        }

        //damaged
        if (damaged == -1) {
            if (stats.containsKey(name)) {
                damaged = stats.get(name).get(4);
            } else {
                damaged = 0;
            }
        } else {
            if (stats.containsKey(name)) {
                damaged = stats.get(name).get(4) + damaged;
            }
        }



        stats.remove(name);

        ArrayList<Integer> playerData = new ArrayList<>();

        playerData.add(display);
        playerData.add(kill);
        playerData.add(death);
        playerData.add(damage);
        playerData.add(damaged);

        stats.put(name, playerData);

        System.out.println(stats.toString());




    }
}
