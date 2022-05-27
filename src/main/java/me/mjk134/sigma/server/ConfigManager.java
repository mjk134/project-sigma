package me.mjk134.sigma.server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import me.mjk134.sigma.ProjectSigma;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;

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

    public void loadConfig() throws FileNotFoundException {
        FileReader reader = new FileReader("project-sigma.json");
        JsonObject json = gson.fromJson(reader, JsonObject.class);
        ENABLED = json.get("enabled").getAsBoolean();
        STARTED = json.get("started").getAsBoolean();
        numLives = json.get("numLives").getAsInt();
        teamAName = json.get("teamAName").getAsString();
        teamBName = json.get("teamBName").getAsString();
        JsonArray playerLivesArray = json.getAsJsonArray("players");
        HashMap<String, Integer> playerLives = new HashMap<>();
        for (int i = 0; i < playerLivesArray.size(); i++) {
            JsonObject playerData = playerLivesArray.getAsJsonObject();
            playerLives.put(playerData.get("name").getAsString(), playerData.get("numLives").getAsInt());
        }
        LivesManager.setPlayerLives(playerLives);
    }

    public void addPlayer(ServerPlayerEntity player, Team team, Scoreboard scoreboard, String teamName, World world) throws IOException {
        RegistryKey<World> registryKey = world.getRegistryKey();
        scoreboard.addPlayerToTeam(player.getEntityName(), team);
        ProjectSigma.livesManager.addPlayer(player);
        if (Objects.equals(teamName, teamAName)) {
            player.setSpawnPoint(registryKey, new BlockPos(1, 118, -7), 0.0f, true, false);
            player.teleport(1, 118, -7);
        } else {
            player.setSpawnPoint(registryKey, new BlockPos(0, 113, 18), 0.0f, true, false);
            player.teleport(0, 113,18);
        }
    }

    public void enable() throws IOException {
        ENABLED = true;
        FileReader reader = new FileReader("project-sigma.json");
        JsonObject json = gson.fromJson(reader, JsonObject.class);
        json.addProperty("enabled", true);
        reader.close();
        FileWriter writer = new FileWriter("project-sigma.json");
        gson.toJson(json, writer);
        writer.close();
    }

    public void start() throws IOException {
        STARTED = true;
        FileReader reader = new FileReader("project-sigma.json");
        JsonObject json = gson.fromJson(reader, JsonObject.class);
        json.addProperty("started", true);
        reader.close();
        FileWriter writer = new FileWriter("project-sigma.json");
        gson.toJson(json, writer);
        writer.close();
    }

}
