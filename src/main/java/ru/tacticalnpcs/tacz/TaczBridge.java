package ru.tacticalnpcs.tacz;

import net.fabricmc.loader.api.FabricLoader;

/** Keeps optional TaCZ presence checks isolated from the NPC runtime. */
public final class TaczBridge {
    private TaczBridge() {}

    public static boolean isAvailable() {
        return FabricLoader.getInstance().isModLoaded("tacz");
    }
}
