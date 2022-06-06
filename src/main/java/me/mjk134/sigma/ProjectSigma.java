package me.mjk134.sigma;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.mjk134.sigma.server.CommandsHandler;
import me.mjk134.sigma.server.ConfigManager;
import me.mjk134.sigma.server.LivesManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;


public class ProjectSigma implements ModInitializer {

	// TODO: Implement usage of nether variables

	public static final Logger LOGGER = LoggerFactory.getLogger("project-sigma");
	public static ConfigManager configManager = new ConfigManager();
	public static LivesManager livesManager = new LivesManager();
	public static MinecraftServer server;
	public static ServerWorld teamANether;
	public static ServerWorld teamBNether;

	@Override
	public void onInitialize() {

		if (!configManager.configExists()) {
			try {
				configManager.createConfig();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				configManager.loadConfig();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			ProjectSigma.server = server;
		});

		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			if (ConfigManager.ENABLED_NETHER) {
				server.getWorldRegistryKeys().forEach(r -> {
					ServerWorld world = server.getWorld(r);
					if (Objects.equals(r.getValue(), new Identifier("project-sigma", ConfigManager.teamAName.toLowerCase() + "_nether"))) {
						ProjectSigma.teamANether = world;
					} else if (Objects.equals(r.getValue(), new Identifier("project-sigma", ConfigManager.teamBName.toLowerCase() + "_nether"))) {
						ProjectSigma.teamBNether = world;
					}
				});
			}
		});

		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			Scoreboard scoreboard = newPlayer.getScoreboard();

			Gson gson1 = new Gson();
			FileReader reader1;
			try {
				reader1 = new FileReader("project-sigma.json");
			} catch (FileNotFoundException e) {
				return;
			}
			JsonObject json1 = gson1.fromJson(reader1, JsonObject.class);
			JsonArray playerLivesArray1 = json1.getAsJsonArray("players");

			String playerName1 = newPlayer.getEntityName();
			JsonObject playerData = null;
			for (int i = 0; i < playerLivesArray1.size(); i++) {
				playerData = playerLivesArray1.get(i).getAsJsonObject();
				if (Objects.equals(playerData.get("name").getAsString(), playerName1)) {
					
					break;
				}
			}
			
			if (!Objects.equals(scoreboard.getPlayerTeam(newPlayer.getEntityName()), scoreboard.getTeam(ConfigManager.teamRogueName)))
				newPlayer.networkHandler.sendPacket(new TitleS2CPacket(new LiteralText(LivesManager.playerLives.get(newPlayer.getEntityName()).getNumLives() == 0 ? "You've Been Eliminated!" : "You died!").setStyle(Style.EMPTY.withColor(Formatting.RED))));
			else {
				newPlayer.networkHandler.sendPacket(new TitleS2CPacket(new LiteralText("You've Gone Rogue!").setStyle(Style.EMPTY.withColor(Formatting.RED))));
			}
			newPlayer.networkHandler.sendPacket(new SubtitleS2CPacket(new LiteralText(LivesManager.playerLives.get(newPlayer.getEntityName()).getNumLives() == 0 ? "You're All Out Of Lives" : "You now have " + playerData.get("numLives").getAsInt() + " lives remaining!").setStyle(Style.EMPTY.withColor(Formatting.RED))));
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			Scoreboard scoreboard = server.getScoreboard();
			if (ConfigManager.STARTED && ConfigManager.ENABLED) {
				Team teamA = scoreboard.getTeam(ConfigManager.teamAName);
				Team teamB = scoreboard.getTeam(ConfigManager.teamBName);
				Team teamRogue = scoreboard.getTeam(ConfigManager.teamRogueName);
				assert teamA != null;
				assert teamB != null;
				assert teamRogue != null;
				if (!teamA.getPlayerList().contains(handler.getPlayer().getEntityName()) && !teamB.getPlayerList().contains(handler.getPlayer().getEntityName())) {
					handler.getPlayer().networkHandler.sendPacket(new TitleS2CPacket(new LiteralText("Welcome to the server!").setStyle(Style.EMPTY.withColor(Formatting.YELLOW))));
					handler.getPlayer().networkHandler.sendPacket(new SubtitleS2CPacket(new LiteralText("We're just setting you up, you will be teleported soon!").setStyle(Style.EMPTY.withColor(Formatting.YELLOW))));
					handler.getPlayer().teleport(11, 317, 0);
					if (teamA.getPlayerList().size() > teamB.getPlayerList().size()) {
						try {
							configManager.addPlayer(handler.getPlayer(), teamB, scoreboard, ConfigManager.teamBName, handler.getPlayer().getWorld());
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else if (teamA.getPlayerList().size() <= teamB.getPlayerList().size()) {
						try {
							configManager.addPlayer(handler.getPlayer(), teamA, scoreboard, ConfigManager.teamAName, handler.getPlayer().getWorld());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			LOGGER.info("Command dispatcher has been initialized!");
			CommandsHandler.registerCommands(dispatcher);
		});
	}

}
