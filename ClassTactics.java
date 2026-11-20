package ru.tacticalnpcs.ai;

import ru.tacticalnpcs.common.NpcClass;
import ru.tacticalnpcs.common.WeaponCategory;
import java.util.Set;

public final class ClassTactics {
    private ClassTactics() {}

    public static Set<WeaponCategory> weaponPool(NpcClass npcClass) {
        return switch (npcClass) {
            case ASSAULT -> Set.of(WeaponCategory.SUBMACHINE_GUN, WeaponCategory.ASSAULT_RIFLE);
            case MACHINE_GUNNER -> Set.of(WeaponCategory.MACHINE_GUN);
            case SNIPER -> Set.of(WeaponCategory.SNIPER_RIFLE);
        };
    }

    public static CombatProfile combatProfile(NpcClass npcClass) {
        return switch (npcClass) {
            case ASSAULT -> new CombatProfile(5.0, 15.0, true, true);
            case MACHINE_GUNNER -> new CombatProfile(12.0, 28.0, false, false);
            case SNIPER -> new CombatProfile(28.0, 64.0, false, false);
        };
    }

    public record CombatProfile(
        double preferredMinRange,
        double preferredMaxRange,
        boolean maneuverWhileFiring,
        boolean seekCoverAggressively
    ) {}
}
