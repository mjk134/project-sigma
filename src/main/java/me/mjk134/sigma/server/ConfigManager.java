package me.mjk134.sigma.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {

    public static boolean ENABLED = false;
    public static boolean STARTED = false;
    public static int numLives = 3;
    public static String teamAName = "TeamA";
    public static String teamBName = "TeamB";
    private final Gson gson = new Gson();

    public boolean configExists() {
        File f = new File("project-sigma.json");
        return f.exists();
    }

    public void createConfig() throws IOException {
        FileWriter writer;
        writer = new FileWriter("project-sigma.json");
        JsonWriter jsonWriter = gson.newJsonWriter(writer);
        jsonWriter.beginObject();
        jsonWriter.name("players");
        jsonWriter.beginArray();
        jsonWriter.endArray();
        jsonWriter.endObject();
        jsonWriter.flush();
        jsonWriter.close();
        FileReader reader = new FileReader("project-sigma.json");
        JsonObject json = gson.fromJson(reader, JsonObject.class);
        json.addProperty("enabled", ENABLED);
        json.addProperty("started", STARTED);
        json.addProperty("numLives", numLives);
        json.addProperty("teamAName", teamAName);
        json.addProperty("teamBName", teamBName);
        reader.close();
        writer = new FileWriter("project-sigma.json");
        gson.toJson(json, writer);
        writer.close();
    }

    public void addPlayer(ServerPlayerEntity player, Team team) {

    }

}
