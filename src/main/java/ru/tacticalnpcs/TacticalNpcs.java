package ru.tacticalnpcs;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tacticalnpcs.command.DebugCommands;
import ru.tacticalnpcs.entity.ModEntityTypes;

public final class TacticalNpcs implements ModInitializer {
    public static final String MOD_ID = "tactical_npcs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModEntityTypes.initialize();
        DebugCommands.register();
        LOGGER.info("Tactical NPC entity registered.");
    }
}
