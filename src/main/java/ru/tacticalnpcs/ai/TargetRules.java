package ru.tacticalnpcs.ai;

import ru.tacticalnpcs.common.Faction;

/** Pure faction policy; entity target predicates are wired in a later milestone. */
public final class TargetRules {
    private TargetRules() {}

    public static boolean mayAttackFaction(Faction attacker, Faction target) {
        return attacker != target;
    }

    public static boolean mayAttackPlayer(Faction attacker) {
        return attacker == Faction.ENEMY;
    }

    public static boolean mayAttackHostileMob() {
        return true;
    }
}
