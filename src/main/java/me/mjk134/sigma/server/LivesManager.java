package me.mjk134.sigma.server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class LivesManager {

    public static HashMap<String, Player> playerLives = new HashMap<>();
    private final Gson gson = new Gson();

    public static void setPlayerLives(HashMap<String, Player> playerLives) {
        LivesManager.playerLives = playerLives;
    }

    public void addPlayer(ServerPlayerEntity player) throws IOException {

        playerLives.put(player.getEntityName(), new Player(ConfigManager.numLives, false));
        FileReader reader = new FileReader("project-sigma.json");
        JsonObject json = gson.fromJson(reader, JsonObject.class);
        JsonArray playerLivesArray = json.getAsJsonArray("players");
        JsonObject playerJson = new JsonObject();
        playerJson.addProperty("name", player.getEntityName());
        playerJson.addProperty("numLives", ConfigManager.numLives);
        playerJson.addProperty("allowsDonations", false);
        playerLivesArray.add(playerJson);
        FileWriter writer = new FileWriter("project-sigma.json");
        gson.toJson(json, writer);
        writer.close();
    }

    public void onDeath(ServerPlayerEntity playerEntity) throws IOException {
        int newLives = playerLives.get(playerEntity.getEntityName()).getNumLives() - 1;

        FileReader reader = new FileReader("project-sigma.json");
        JsonObject json = gson.fromJson(reader, JsonObject.class);
        JsonArray playerLivesArray = json.getAsJsonArray("players");

        if (newLives == 0) {
            Scoreboard scoreboard = MinecraftClient.getInstance().getServer().getScoreboard();

            for (int i = 0; i < playerLivesArray.size(); i++) {
                JsonObject playerData = playerLivesArray.get(i).getAsJsonObject();
                if (Objects.equals(playerData.get("name").getAsString(), playerEntity.getEntityName())) {
                    if (!Objects.equals(scoreboard.getPlayerTeam(playerEntity.getEntityName()), scoreboard.getTeam(ConfigManager.teamRogueName))) {
                        //if player isn't rogue, give them an extra life and make them rogue

                        scoreboard.removePlayerFromTeam(playerEntity.getEntityName(), scoreboard.getPlayerTeam(playerEntity.getEntityName()));
                        scoreboard.addPlayerToTeam(playerEntity.getEntityName(), scoreboard.getTeam(ConfigManager.teamRogueName));
                        newLives++;
                        playerData.addProperty("name", playerEntity.getEntityName());
                        playerData.addProperty("numLives", newLives);
                        FileWriter writer = new FileWriter("project-sigma.json");
                        gson.toJson(json, writer);
                        writer.close();
                    }
                    else {
                        scoreboard.removePlayerFromTeam(playerEntity.getEntityName(), scoreboard.getTeam(ConfigManager.teamRogueName));
                        playerEntity.changeGameMode(GameMode.SPECTATOR);
                    }
                    break;
                }
            }
        } else {
            for (int i = 0; i < playerLivesArray.size(); i++) {
                JsonObject playerData = playerLivesArray.get(i).getAsJsonObject();
                if (Objects.equals(playerData.get("name").getAsString(), playerEntity.getEntityName())) {
                    playerData.addProperty("name", playerEntity.getEntityName());
                    playerData.addProperty("numLives", newLives);
                    FileWriter writer = new FileWriter("project-sigma.json");
                    gson.toJson(json, writer);
                    writer.close();
                    break;
                }
            }
        }
        Player player = playerLives.get(playerEntity.getEntityName());
        player.setNumLives(newLives);
        playerLives.replace(playerEntity.getEntityName(), player);
    }
}
