package fr.shaft.reusabledragon.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;

public class SaveModule
{
    //ATTRIBUTS
    final private static Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
    final private JavaPlugin plugin;
    final private File repository;
    private File dataFile;
    Object object;
    String json;

    //CONSTRUCTORS
    public SaveModule(JavaPlugin main) {
            this.plugin = main;
            this.repository = new File(main.getDataFolder() + "/DATA/");
            this.repository.mkdirs();
        }
    public SaveModule(JavaPlugin main, String rep) {
            this.plugin = main;
            this.repository = new File(rep);
            this.repository.mkdirs();
    }
    public SaveModule(JavaPlugin main, File rep) {
            this.plugin = main;
            this.repository = rep;
            this.repository.mkdirs();
    }

    //CREATE
    public void createDataFile(String name) {
        File f = new File(repository, name);

        if(!f.exists())
        {

            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        this.dataFile = f;

    }

    //WRITING
    public static void writeFile(File file, String content) {

        try
        {
            PrintStream fileStream = new PrintStream(file);
            fileStream.println(content);
            fileStream.flush();
            fileStream.close();

        }    catch (IOException e){e.printStackTrace();}

    }

    public static void writeFile(File file, ArrayList<String> content) {
        final FileWriter fw;

        try
        {
            PrintStream fileStream = new PrintStream(file);
            for(String line : content){
               fileStream.println(line);
           }

        }    catch (IOException e){e.printStackTrace();}

    }

    //READING
    public static String readFile(File file) {
        if (file.exists()) {
            try {

                final BufferedReader bf = new BufferedReader(new FileReader(file));

                final StringBuilder content = new StringBuilder();

                String line;

                while ((line = bf.readLine()) != null) {
                    content.append(line);
                }
                bf.close();
                return content.toString();

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        return null;
    }

    //SERIALIZE
    private String serialize(Object obj)
    {
        return gson.toJson(obj);
    }

    //DESERIALIZE
    private Object deserialize(String json, Object obj)
    {
        return gson.fromJson(json, obj.getClass());
    }

    //GET DATA FILE
    public File getDataFile()
    {
        return this.dataFile;
    }

    //SAVE
    public void save(Object obj, String name) {
        createDataFile(name);
        this.json = serialize(obj);
        writeFile(this.dataFile, this.json);
    }

    //LOAD
    public Object load(File file, Object object)
    {
        this.json = readFile(file);
        return deserialize(this.json, object);
    }
}
