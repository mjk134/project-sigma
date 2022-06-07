package me.mjk134.sigma.server.commands;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mjk134.sigma.server.ConfigManager;
import me.mjk134.sigma.server.LivesManager;
import me.mjk134.sigma.server.Player;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class SetLivesCommand {
    public static int run(CommandContext<ServerCommandSource> context) {
        if (!ConfigManager.STARTED || !ConfigManager.ENABLED) {
            try {
                context.getSource().getPlayer().sendMessage(new LiteralText("The walls have not been activated yet!"), false);
            } catch (CommandSyntaxException exception) {
                return 1;
            }
        } else {
            try {
                ServerPlayerEntity player = (ServerPlayerEntity) EntityArgumentType.getEntity(context, "player");
                Integer newLives = context.getArgument("lives", Integer.class);
                Gson gson = new Gson();
                FileReader reader = new FileReader("project-sigma.json");
                JsonObject json = new Gson().fromJson(reader, JsonObject.class);
                JsonArray playerLivesArray = json.getAsJsonArray("players");
                for (int i = 0; i < playerLivesArray.size(); i++) {
                    JsonObject playerData = playerLivesArray.get(i).getAsJsonObject();
                    if (Objects.equals(playerData.get("name").getAsString(), player.getEntityName())) {
                        playerData.addProperty("name", player.getEntityName());
                        playerData.addProperty("numLives", newLives);
                        Player actualPlayer = LivesManager.playerLives.get(player.getEntityName());
                        actualPlayer.setNumLives(newLives);
                        LivesManager.playerLives.replace(player.getEntityName(), actualPlayer);
                        context.getSource().sendFeedback(new LiteralText(player.getEntityName() + " now has " + newLives + (newLives == 1 ? " life." : " lives.")), true);
                        FileWriter writer = new FileWriter("project-sigma.json");
                        gson.toJson(json, writer);
                        writer.close();
                        break;
                    }
                }
            } catch (IOException | CommandSyntaxException e) {
                return 0;
            }
        }
        return 1;
    }

}
