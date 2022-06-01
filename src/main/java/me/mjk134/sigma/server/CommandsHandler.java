package me.mjk134.sigma.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mjk134.sigma.ProjectSigma;

import me.mjk134.sigma.server.commands.GetLivesCommand;
import me.mjk134.sigma.server.commands.SetLivesCommand;
import net.minecraft.server.command.ServerCommandSource;
import me.mjk134.sigma.server.commands.NetherCommand;
import me.mjk134.sigma.server.commands.StartWallsCommand;
import me.mjk134.sigma.server.commands.SwapTeamsCommand;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;

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
                        .then(literal("swap")
                                .then(argument("player", EntityArgumentType.entities())
                                        .executes(
                                                context -> {
                                                    try {
                                                        return SwapTeamsCommand.run(context);
                                                    } catch (CommandSyntaxException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                }
                                        )))
                        .requires((source) -> source.hasPermissionLevel(2))
        );
        dispatcher.register(
                literal("getLives")
                        .then(argument("PlayerName", EntityArgumentType.entities())
                                .executes(context -> {
                                    try {
                                        return GetLivesCommand.run(context, EntityArgumentType.getEntity(context, "PlayerName"));
                                    } catch (FileNotFoundException e) {
                                        throw new RuntimeException(e);
                                    }
                                }))

        );
        dispatcher.register(
                literal("setLives")
                        .then(argument("PlayerName", EntityArgumentType.entities()).then(argument("Lives", IntegerArgumentType.integer()).executes(context -> {
                            try {
                                return SetLivesCommand.run(context, EntityArgumentType.getEntity(context, "PlayerName"));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })))
                        .requires((source) -> source.hasPermissionLevel(2)));
          
        dispatcher.register(literal("nether")
                .executes(NetherCommand::run)
        );

    }

}
