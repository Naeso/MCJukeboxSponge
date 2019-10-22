package net.mcjukebox.plugin.sponge.utils;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import net.mcjukebox.plugin.sponge.sockets.api.KeyClass;

import java.io.*;
import java.nio.file.Path;

public class DataUtils {

    private Gson gson;
    private KeyClass apiKey;

    public DataUtils() {
         gson = new Gson();
    }

    public void saveObjectToPath(Object objectToSave, Path pathToSaveTo){
        try (FileWriter fileWriter = new FileWriter(pathToSaveTo.toFile())) {
            gson.toJson(objectToSave, fileWriter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String loadAPIKey(Path pathToLoadFrom){
        apiKey = this.loadAPIClass(pathToLoadFrom);
        if (apiKey != null) {
            return apiKey.getKeyValue();
        }
        return null;
    }

    private KeyClass loadAPIClass(Path pathToLoadFrom){
        try {
            JsonReader reader = new JsonReader(new FileReader(pathToLoadFrom.toFile()));
            return gson.fromJson(reader, KeyClass.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
