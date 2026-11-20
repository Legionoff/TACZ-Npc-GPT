package ru.tacticalnpcs;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tacticalnpcs.command.DebugCommands;

public final class TacticalNpcs implements ModInitializer {
    public static final String MOD_ID = "tactical_npcs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) ->
                DebugCommands.register(dispatcher)
        );
        LOGGER.info("Tactical NPCs foundation loaded. Entity registration and TaCZ bridge are next.");
    }
}
