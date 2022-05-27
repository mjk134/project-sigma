package me.mjk134.sigma.server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.mjk134.sigma.mixin.ServerPlayerEntityMixin;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class LivesManager {

    public static HashMap<String, Integer> playerLives = new HashMap<>();
    private final Gson gson = new Gson();

    public static void setPlayerLives(HashMap<String, Integer> playerLives) {
        LivesManager.playerLives = playerLives;
    }

    public void addPlayer(ServerPlayerEntity player) throws IOException {
        playerLives.put(player.getEntityName(), ConfigManager.numLives);
        FileReader reader = new FileReader("project-sigma.json");
        JsonObject json = gson.fromJson(reader, JsonObject.class);
        JsonArray playerLivesArray = json.getAsJsonArray("players");
        JsonObject playerJson = new JsonObject();
        playerJson.addProperty("name", player.getEntityName());
        playerJson.addProperty("numLives", ConfigManager.numLives);
        playerLivesArray.add(playerJson);
        FileWriter writer = new FileWriter("project-sigma.json");
        gson.toJson(json, writer);
        writer.close();
    }

    public void onDeath(ServerPlayerEntity player) throws IOException {
        int newLives = playerLives.get(player.getEntityName()) - 1;
        if (newLives <= 0) {
            // TODO: implement death logic
        } else {
            FileReader reader = new FileReader("project-sigma.json");
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            JsonArray playerLivesArray = json.getAsJsonArray("players");
            for (int i = 0; i < playerLivesArray.size(); i++) {
                JsonObject playerData = playerLivesArray.getAsJsonObject();
                if (Objects.equals(playerData.getAsJsonObject("name").getAsString(), player.getEntityName())) {
                    playerData.addProperty("name", player.getEntityName());
                    playerData.addProperty("numLives", newLives);
                    FileWriter writer = new FileWriter("project-sigma.json");
                    gson.toJson(json, writer);
                    writer.close();
                    break;
                }
            }
        }
    }
}
