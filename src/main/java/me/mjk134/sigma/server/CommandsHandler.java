package me.mjk134.sigma.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.mjk134.sigma.ProjectSigma;
import me.mjk134.sigma.server.commands.StartWallsCommand;
import net.minecraft.server.command.ServerCommandSource;

import java.io.FileNotFoundException;
import java.io.IOException;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandsHandler {


    /**
    * All commands are registered here
    * @author mjk134
    */
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("walls")
                        .then(literal("start")
                                .then(argument("size", IntegerArgumentType.integer()).executes(StartWallsCommand::run))
                        )
                        .then(literal("enable")
                                .executes(context -> {
                                    try {
                                        ProjectSigma.configManager.enable();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    return 1;
                                })
                        )
                        .requires((source) -> source.hasPermissionLevel(2))
        );

    }

}
