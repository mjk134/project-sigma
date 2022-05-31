package me.mjk134.sigma.server.commands;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mjk134.sigma.server.ConfigManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Objects;

public class SetLivesCommand {
//TODO: set lives command
    public static int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException, FileNotFoundException {
        if (!ConfigManager.STARTED || !ConfigManager.ENABLED) {
            try {
                context.getSource().getPlayer().sendMessage(new LiteralText("The walls have not been activated yet!"), false);
            } catch (CommandSyntaxException exception) {
                return 1;
            }
        } else {

        }
        return 1;
    }

}
