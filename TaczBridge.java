package ru.tacticalnpcs.tacz;

import net.fabricmc.loader.api.FabricLoader;
import ru.tacticalnpcs.common.NpcClass;
import ru.tacticalnpcs.common.WeaponCategory;
import java.util.Set;

/** Runtime boundary for the optional TaCZ Refabricated dependency. */
public final class TaczBridge {
    private TaczBridge() {}

    public static Set<WeaponCategory> categoriesFor(NpcClass npcClass) {
        return switch (npcClass) {
            case ASSAULT -> Set.of(WeaponCategory.SUBMACHINE_GUN, WeaponCategory.ASSAULT_RIFLE);
            case MACHINE_GUNNER -> Set.of(WeaponCategory.MACHINE_GUN);
            case SNIPER -> Set.of(WeaponCategory.SNIPER_RIFLE);
        };
    }

    /** The uploaded TaCZ jar declares the mod id as exactly "tacz". */
    public static boolean isTaczPresent() {
        return FabricLoader.getInstance().isModLoaded("tacz");
    }
}
