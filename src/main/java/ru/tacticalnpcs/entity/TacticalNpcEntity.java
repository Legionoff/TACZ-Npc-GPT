package ru.tacticalnpcs.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import ru.tacticalnpcs.common.Faction;
import ru.tacticalnpcs.common.NpcClass;
import ru.tacticalnpcs.common.TeammateOrder;

public final class TacticalNpcEntity extends PathfinderMob {
    private Faction faction = Faction.TEAMMATE;
    private NpcClass npcClass = NpcClass.ASSAULT;
    private TeammateOrder order = TeammateOrder.WANDER;

    public TacticalNpcEntity(EntityType<? extends TacticalNpcEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 30.0)
            .add(Attributes.MOVEMENT_SPEED, 0.30)
            .add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(7, new RandomStrollGoal(this, 0.8));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    public Faction getFaction() { return faction; }
    public void setFaction(Faction faction) { this.faction = faction; }
    public NpcClass getNpcClass() { return npcClass; }
    public void setNpcClass(NpcClass npcClass) { this.npcClass = npcClass; }
    public TeammateOrder getOrder() { return order; }
    public void setOrder(TeammateOrder order) { this.order = order; }
}
