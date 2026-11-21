package ru.tacticalnpcs.command;

import com.mojang.brigadier.Command;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public final class DebugCommands {
    private DebugCommands() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(Commands.literal("tnpc")
                .then(Commands.literal("status").executes(context -> {
                    context.getSource().sendSuccess(
                        () -> Component.literal("NPC entity and client skins are registered. Teammate orders are basic; faction combat, TaCZ firing/reloading, and spawn eggs remain unfinished."),
                        false
                    );
                    return Command.SINGLE_SUCCESS;
                }))
            )
        );
    }
}
