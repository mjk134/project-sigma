package me.mjk134.sigma.server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import me.mjk134.sigma.ProjectSigma;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import xyz.nucleoid.fantasy.Fantasy;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;

public class ConfigManager {

    // TODO: Save lastNetherCoords and lastOverworldCoords

    public static boolean ENABLED = false;
    public static boolean STARTED = false;
    public static boolean LivingEntityMixin = false;
    public static int numLives = 3;
    public static String teamAName = "TeamA";
    public static String teamBName = "TeamB";
    public static String teamRogueName = "Rogue";
    public static boolean ENABLED_NETHER = false;
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
        json.addProperty("livingEntityMixin", LivingEntityMixin);
        json.addProperty("numLives", numLives);
        json.addProperty("teamAName", teamAName);
        json.addProperty("teamBName", teamBName);
        json.addProperty("teamRogueName", teamRogueName);
        json.addProperty("enabledNether", ENABLED_NETHER);
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
        LivingEntityMixin = json.get("livingEntityMixin").getAsBoolean();
        numLives = json.get("numLives").getAsInt();
        teamAName = json.get("teamAName").getAsString();
        teamBName = json.get("teamBName").getAsString();
        teamRogueName = json.get("teamRogueName").getAsString();
        ENABLED_NETHER = json.get("enabledNether").getAsBoolean();
        JsonArray playerLivesArray = json.getAsJsonArray("players");
        HashMap<String, Player> playerLives = new HashMap<>();
        for (int i = 0; i < playerLivesArray.size(); i++) {
            JsonObject playerData = playerLivesArray.get(i).getAsJsonObject();
            Player player = new Player(playerData.get("numLives").getAsInt(), playerData.get("allowsDonations").getAsBoolean());
            playerLives.put(playerData.get("name").getAsString(), player);
        }
        LivesManager.setPlayerLives(playerLives);
    }

    public void addPlayer(ServerPlayerEntity player, Team team, Scoreboard scoreboard, String teamName, World world) throws IOException {
        ProjectSigma.LOGGER.info("Adding new player to team: " + teamName);
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

    public void enableNether(MinecraftServer server) throws IOException {
        Fantasy fantasy = Fantasy.get(server);
        ENABLED_NETHER = true;
        FileReader reader = new FileReader("project-sigma.json");
        JsonObject json = gson.fromJson(reader, JsonObject.class);
        json.addProperty("enabledNether", true);
        reader.close();
        FileWriter writer = new FileWriter("project-sigma.json");
        gson.toJson(json, writer);
        writer.close();
        ChunkGeneratorSettings settings = server.getRegistryManager().get(Registry.CHUNK_GENERATOR_SETTINGS_KEY).getEntry(ChunkGeneratorSettings.NETHER).get().value();
        RuntimeWorldConfig config = new RuntimeWorldConfig();
        config.setDimensionType(DimensionType.THE_NETHER_REGISTRY_KEY);
        // config.setSeed(171717L);
        // config.setGenerator(GeneratorOptions.createGenerator(server.getRegistryManager(),config.getSeed(), server.getRegistryManager().get(Registry.CHUNK_GENERATOR_SETTINGS_KEY).getKey(settings).get()));
        config.setGenerator(Objects.requireNonNull(server.getWorld(World.NETHER)).getChunkManager().getChunkGenerator());
        config.setDifficulty(Difficulty.HARD);
        fantasy.getOrOpenPersistentWorld(new Identifier("project-sigma",teamAName.toLowerCase() + "_nether"), config);
        fantasy.getOrOpenPersistentWorld(new Identifier("project-sigma",teamBName.toLowerCase() + "_nether"), config);
    }

    public void toggleEntityMixin() throws IOException {
        LivingEntityMixin = !LivingEntityMixin;
        FileReader reader = new FileReader("project-sigma.json");
        JsonObject json = gson.fromJson(reader, JsonObject.class);
        json.addProperty("livingEntityMixin", LivingEntityMixin);
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

    public void swap(ServerPlayerEntity player, Team team, Scoreboard scoreboard, String teamName, World world) {
        scoreboard.removePlayerFromTeam(player.getEntityName(), team);
        RegistryKey<World> registryKey = world.getRegistryKey();
        if (Objects.equals(teamName, teamRogueName)) return;
        if (Objects.equals(teamName, teamAName)) {
            Team teamB = scoreboard.getTeam(teamBName);
            scoreboard.addPlayerToTeam(player.getEntityName(), teamB);
            player.setSpawnPoint(registryKey, new BlockPos(0, 113, 18), 0.0f, true, false);
            player.teleport(0, 113,18);
        } else {
            Team teamA = scoreboard.getTeam(teamAName);
            scoreboard.addPlayerToTeam(player.getEntityName(), teamA);
            player.setSpawnPoint(registryKey, new BlockPos(1, 118, -7), 0.0f, true, false);
            player.teleport(1, 118, -7);
        }
    }

}
