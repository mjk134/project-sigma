package me.mjk134.sigma.server.commands;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mjk134.sigma.server.ConfigManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class DonationPreferenceCommand {
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
            Gson gson = new Gson();
            FileReader reader = new FileReader("project-sigma.json");
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            JsonArray playerLivesArray = json.getAsJsonArray("players");
            String playerName = context.getSource().getPlayer().getEntityName();
            for (int i = 0; i < playerLivesArray.size(); i++) {
                JsonObject playerData = playerLivesArray.get(i).getAsJsonObject();
                if (Objects.equals(playerData.get("name").getAsString(), playerName)) {
                    playerData.addProperty("name", playerName);
                    playerData.addProperty("allowsDonations", !playerData.get("allowsDonations").getAsBoolean());

                    FileWriter writer = new FileWriter("project-sigma.json");
                    gson.toJson(json, writer);
                    writer.close();

                    context.getSource().sendFeedback(new LiteralText(playerData.get("allowsDonations").getAsBoolean() ? "You are now accepting life donations!" : "You are now no longer accepting lives donations!"), false);

                    break;
                }
            }
            reader.close();
            } catch (CommandSyntaxException | IOException e) {
            return 0;
        }

        return 1;
    }
}
