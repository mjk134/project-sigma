package me.mjk134.sigma.server.commands;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mjk134.sigma.server.ConfigManager;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class GetLivesCommand {

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

                Gson gson1 = new Gson();
                FileReader reader1 = new FileReader("project-sigma.json");
                JsonObject json1 = gson1.fromJson(reader1, JsonObject.class);
                JsonArray playerLivesArray1 = json1.getAsJsonArray("players");

                String playerName1 = player.getEntityName();
                for (int i = 0; i < playerLivesArray1.size(); i++) {
                    JsonObject playerData = playerLivesArray1.get(i).getAsJsonObject();
                    if (Objects.equals(playerData.get("name").getAsString(), playerName1)) {

                        int numLives = playerData.get("numLives").getAsInt();
                        context.getSource().sendFeedback(new LiteralText(player.getEntityName() + " has " + numLives + (numLives == 1 ?" life left.":" lives left.")), false);


                        break;
                    }
                }



           } catch (CommandSyntaxException | IOException e) {
                e.printStackTrace();
            }
        }
        return 1;
    }

}
