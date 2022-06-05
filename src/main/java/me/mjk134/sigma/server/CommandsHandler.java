package me.mjk134.sigma.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.mjk134.sigma.ProjectSigma;
import me.mjk134.sigma.server.commands.*;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

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
                                .then(literal("nether")
                                        .executes(context -> {
                                            try {
                                                ProjectSigma.configManager.enableNether(context.getSource().getServer());
                                                context.getSource().getPlayer().sendMessage(new LiteralText("Enabled nether!"), false);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("swap").then(argument("player", EntityArgumentType.entities()).executes(SwapTeamsCommand::run)))
                        .requires((source) -> source.hasPermissionLevel(2))
        );
        dispatcher.register(
                literal("lives").then(literal("get").then(argument("player", EntityArgumentType.entities()).executes(GetLivesCommand::run)))
                .then(literal("set")
                        .then(argument("PlayerName", EntityArgumentType.entities()).then(argument("Lives", IntegerArgumentType.integer()).executes(context -> {
                            try {
                                return SetLivesCommand.run(context, EntityArgumentType.getEntity(context, "PlayerName"));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })))
                ).requires((source) -> source.hasPermissionLevel(2))
                .then(literal("donate").then(argument("player", EntityArgumentType.entities()).executes(DonateLivesCommand::run)))
        );

        dispatcher.register(literal("dimension")
                .then(literal("nether").executes(NetherCommand::run))
                .then(literal("overworld").executes(OverworldCommand::run))
        );


    }

}
