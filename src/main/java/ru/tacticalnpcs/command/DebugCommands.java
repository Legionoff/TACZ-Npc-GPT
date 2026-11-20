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
                        () -> Component.literal("Tactical NPCs foundation loaded; NPC entity runtime is not implemented yet."), false);
                    return Command.SINGLE_SUCCESS;
                }))));
    }
}
