package me.mjk134.sigma.server.commands;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mjk134.sigma.server.ConfigManager;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class SetLivesCommand {
    public static int run(CommandContext<ServerCommandSource> context, Entity PlayerName) throws IOException {
        if (!ConfigManager.STARTED || !ConfigManager.ENABLED) {
            try {
                context.getSource().getPlayer().sendMessage(new LiteralText("The walls have not been activated yet!"), false);
            } catch (CommandSyntaxException exception) {
                return 1;
            }
        } else {
            String playerName = PlayerName.getEntityName();
            Integer newLives = context.getArgument("Lives", Integer.class);

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
                JsonObject playerData = playerLivesArray.get(i).getAsJsonObject();
                if (Objects.equals(playerData.get("name").getAsString(), playerName)) {
                    playerData.addProperty("name", PlayerName.getEntityName());
                    playerData.addProperty("numLives", newLives);
                    context.getSource().sendFeedback(new LiteralText(PlayerName.getEntityName() + " now has " + newLives + (newLives == 1 ? " life." : " lives.")), true);
                    FileWriter writer = new FileWriter("project-sigma.json");
                    gson.toJson(json, writer);
                    writer.close();
                    break;
                }
            }
        }
        return 1;
    }

}
