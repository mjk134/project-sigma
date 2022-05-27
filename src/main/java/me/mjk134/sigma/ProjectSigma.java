package me.mjk134.sigma;

import me.mjk134.sigma.server.CommandsHandler;
import me.mjk134.sigma.server.ConfigManager;
import me.mjk134.sigma.server.LivesManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;


public class ProjectSigma implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("project-sigma");
	public static ConfigManager configManager = new ConfigManager();
	public static LivesManager livesManager = new LivesManager();

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

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			Scoreboard scoreboard = server.getScoreboard();
			if (ConfigManager.STARTED && ConfigManager.ENABLED) {
				Team teamA =  scoreboard.getTeam(ConfigManager.teamAName);
				Team teamB =  scoreboard.getTeam(ConfigManager.teamBName);
				assert teamA != null;
				assert teamB != null;
				handler.getPlayer().networkHandler.sendPacket(new TitleS2CPacket(new LiteralText("Welcome to the server!").setStyle(Style.EMPTY.withColor(Formatting.YELLOW))));
				handler.getPlayer().networkHandler.sendPacket(new SubtitleS2CPacket(new LiteralText("We're just setting you up, you will be teleported soon!").setStyle(Style.EMPTY.withColor(Formatting.YELLOW))));
				if (!teamA.getPlayerList().contains(handler.getPlayer().getEntityName()) && !teamB.getPlayerList().contains(handler.getPlayer().getEntityName())) {
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
