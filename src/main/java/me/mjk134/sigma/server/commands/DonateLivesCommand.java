package me.mjk134.sigma.server.commands;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mjk134.sigma.server.ConfigManager;
import me.mjk134.sigma.server.LivesManager;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class DonateLivesCommand {
    public static int run(CommandContext<ServerCommandSource> context) {
        if (!ConfigManager.STARTED || !ConfigManager.ENABLED) {
            try {
                context.getSource().getPlayer().sendMessage(new LiteralText("The walls have not been activated yet!"), false);
            } catch (CommandSyntaxException exception) {
                return 1;
            }
            return 1;
        }
        try {
            ServerPlayerEntity recipientPlayer = (ServerPlayerEntity) EntityArgumentType.getEntity(context, "player");
            ServerPlayerEntity donatingPlayer = context.getSource().getPlayer();
            Scoreboard scoreboard = context.getSource().getServer().getScoreboard();

            // if player trying to donate is rogue, don't let them
            if (!Objects.equals(scoreboard.getPlayerTeam(context.getSource().getPlayer().getEntityName()), scoreboard.getTeam(ConfigManager.teamAName)) && !Objects.equals(scoreboard.getPlayerTeam(context.getSource().getPlayer().getEntityName()), scoreboard.getTeam(ConfigManager.teamBName))) {
                context.getSource().sendError(new LiteralText("You can't donate lives to a player if you are rogue!"));
                return 0;
            }
            // if player is trying to donate to someone on the other team, don't let them
            if (!Objects.equals(scoreboard.getPlayerTeam(recipientPlayer.getEntityName()), scoreboard.getPlayerTeam(context.getSource().getPlayer().getEntityName()))) {
                context.getSource().sendError(new LiteralText("You can't donate lives to a player on the other team!"));
                return 0;
            }

            if (!LivesManager.playerLives.get(donatingPlayer.getEntityName()).isAllowsDonations()) {
                context.getSource().sendError(new LiteralText("This player isn't accepting donations!"));
                return 0;
            }

            Gson gson = new Gson();
            FileReader reader;
            try {
                reader = new FileReader("project-sigma.json");
            } catch (FileNotFoundException e) {
                return 0;
            }
            JsonObject json = new Gson().fromJson(reader, JsonObject.class);
            JsonArray playerLivesArray = json.getAsJsonArray("players");
            for (int i = 0; i < playerLivesArray.size(); i++) {
                JsonObject receivingPlayerData = playerLivesArray.get(i).getAsJsonObject();
                if (Objects.equals(receivingPlayerData.get("name").getAsString(), recipientPlayer.getEntityName())) {
                    for (int z = 0; z < playerLivesArray.size(); z++) {
                        JsonObject donatingPlayerData = playerLivesArray.get(z).getAsJsonObject();
                        if (Objects.equals(donatingPlayerData.get("name").getAsString(), donatingPlayer.getEntityName())) {
                            if (Integer.parseInt(donatingPlayerData.get("numLives").toString()) < 2) {
                                context.getSource().sendError(new LiteralText("You don't have enough lives to donate!"));
                                return 0;
                            }
                            receivingPlayerData.addProperty("name", recipientPlayer.getEntityName());
                            receivingPlayerData.addProperty("numLives", Integer.parseInt(receivingPlayerData.get("numLives").toString()) + 1);
                            donatingPlayerData.addProperty("name", context.getSource().getPlayer().getEntityName());
                            donatingPlayerData.addProperty("numLives", Integer.parseInt(donatingPlayerData.get("numLives").toString()) - 1);
                            context.getSource().sendFeedback(new LiteralText( donatingPlayer.getEntityName() + " has donated 1 life to " + recipientPlayer.getEntityName()), true);
                            recipientPlayer.sendMessage(new LiteralText(donatingPlayer.getEntityName() + " donated 1 life to you"), false);
                            FileWriter writer = new FileWriter("project-sigma.json");
                            gson.toJson(json, writer);
                            writer.close();
                            return 1;
                        }
                    }
                }
            }
        } catch (CommandSyntaxException | IOException e) {
            e.printStackTrace();
        }
        context.getSource().sendError(new LiteralText("That player does not exist"));
        return 1;
    }
}
