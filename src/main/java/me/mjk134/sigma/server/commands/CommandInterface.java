package me.mjk134.sigma.server.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public interface CommandInterface {

    static int run(CommandContext<ServerCommandSource> context) {
        return 0;
    }

}
