package me.mjk134.sigma;

import me.mjk134.sigma.server.CommandsHandler;
import me.mjk134.sigma.server.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProjectSigma implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("project-sigma");
	public static ConfigManager configManager = new ConfigManager();

	@Override
	public void onInitialize() {

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			Scoreboard scoreboard = server.getScoreboard();
			if (ConfigManager.STARTED) {
				Team teamA =  scoreboard.getTeam(ConfigManager.teamAName);
				Team teamB =  scoreboard.getTeam(ConfigManager.teamBName);
				assert teamA != null;
				assert teamB != null;
				if (!teamA.getPlayerList().contains(handler.getPlayer().getEntityName()) && !teamB.getPlayerList().contains(handler.getPlayer().getEntityName())) {
					if (teamA.getPlayerList().size() > teamB.getPlayerList().size()) {
						configManager.addPlayer(handler.getPlayer(), teamB);
					} else if (teamA.getPlayerList().size() <= teamB.getPlayerList().size()) {
						configManager.addPlayer(handler.getPlayer(), teamA);
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
