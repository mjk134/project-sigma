package me.mjk134.sigma.server.commands;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mjk134.sigma.server.ConfigManager;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.Objects;

public class GetLivesCommand {

    public static int run(CommandContext<ServerCommandSource> context, Entity PlayerName) throws FileNotFoundException {
        if (!ConfigManager.STARTED || !ConfigManager.ENABLED) {
            try {
                context.getSource().getPlayer().sendMessage(new LiteralText("The walls have not been activated yet!"), false);
            } catch (CommandSyntaxException exception) {
                return 1;
            }
        } else {

            String playerName = '"' + PlayerName.getEntityName() + '"';
            FileReader reader;
            try {
                reader = new FileReader("project-sigma.json");
            } catch (FileNotFoundException e) {
                return 0;
            }
            JsonObject json = new Gson().fromJson(reader, JsonObject.class);
            JsonArray playerLivesArray = json.getAsJsonArray("players");
            boolean playerExists = false;
            for (int i = 0; i < playerLivesArray.size(); i++) {
                JsonObject playerData = playerLivesArray.get(i).getAsJsonObject();
                if (Objects.equals(playerData.get("name").toString(), playerName)) {
                    playerExists = true;
                    context.getSource().sendFeedback(new LiteralText(PlayerName.getEntityName()+ " has " + playerData.get("numLives").getAsInt() + (playerData.get("numLives").getAsInt() == 1 ?" life left.":" lives left.")), false);
                    break;
                }
            }
            if (!playerExists) {
                context.getSource().sendError(new LiteralText("This player does not exist!"));
            }
        }
        return 1;
    }

}
