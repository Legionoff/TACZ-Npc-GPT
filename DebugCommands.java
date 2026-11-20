package ru.tacticalnpcs.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public final class DebugCommands {
    private DebugCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tnpc")
            .then(Commands.literal("status")
                .executes(context -> {
                    context.getSource().sendSuccess(
                        () -> net.minecraft.network.chat.Component.literal(
                            "Tactical NPCs 0.1.0: core loaded; NPC entity registration is in progress."
                        ),
                        false
                    );
                    return 1;
                })
            )
        );
    }
}
