package ru.tacticalnpcs;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tacticalnpcs.command.DebugCommands;
import ru.tacticalnpcs.entity.ModEntityTypes;
import ru.tacticalnpcs.item.ModItems;

public final class TacticalNpcs implements ModInitializer {
    public static final String MOD_ID = "tactical_npcs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModEntityTypes.initialize();
        ModItems.initialize();
        DebugCommands.register();
        LOGGER.info("Tactical NPC entities and spawn eggs registered.");
    }
}
