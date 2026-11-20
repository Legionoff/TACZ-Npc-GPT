package ru.tacticalnpcs.common;

public enum Faction {
    TEAMMATE,
    ENEMY;

    public boolean isFriendlyTo(Faction other) {
        return this == other;
    }
}
