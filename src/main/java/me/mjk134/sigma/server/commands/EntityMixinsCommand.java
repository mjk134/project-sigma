package me.mjk134.sigma.server.commands;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mjk134.sigma.ProjectSigma;
import me.mjk134.sigma.server.ConfigManager;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class EntityMixinsCommand {

    public static int run(CommandContext<ServerCommandSource> context) {

        if (!ConfigManager.STARTED || !ConfigManager.ENABLED) {
            try {
                context.getSource().getPlayer().sendMessage(new LiteralText("The walls have not been activated yet!"), false);
            } catch (CommandSyntaxException exception) {
                return 0;
            }
        }
        try {
            ProjectSigma.configManager.toggleEntityMixin();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FileReader reader = null;
        try {
            reader = new FileReader("project-sigma.json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Gson gson = new Gson();
        JsonObject json = gson.fromJson(reader, JsonObject.class);

        context.getSource().sendFeedback(new LiteralText("LivingEntityMixin functionality is now set to " + json.get("livingEntityMixin").getAsBoolean()), true);
        return 1;
    }
}
