package ru.tacticalnpcs.ai;

import ru.tacticalnpcs.common.Faction;

/**
 * Pure faction policy. Entity-specific hostility (vanilla monsters, players and
 * target predicates) is wired into the registered NPC entity in the next milestone.
 */
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
