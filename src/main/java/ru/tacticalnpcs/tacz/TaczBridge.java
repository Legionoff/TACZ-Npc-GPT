package ru.tacticalnpcs.tacz;
import net.fabricmc.loader.api.FabricLoader;
public final class TaczBridge {
    private TaczBridge() {}
    public static boolean isAvailable() { return FabricLoader.getInstance().isModLoaded("tacz"); }
}
