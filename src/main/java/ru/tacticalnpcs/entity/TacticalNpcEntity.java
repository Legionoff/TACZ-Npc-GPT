package ru.tacticalnpcs.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
    private static final EntityDataAccessor<String> FACTION =
        SynchedEntityData.defineId(TacticalNpcEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> NPC_CLASS =
        SynchedEntityData.defineId(TacticalNpcEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> ORDER =
        SynchedEntityData.defineId(TacticalNpcEntity.class, EntityDataSerializers.STRING);

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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FACTION, Faction.TEAMMATE.name());
        builder.define(NPC_CLASS, NpcClass.ASSAULT.name());
        builder.define(ORDER, TeammateOrder.WANDER.name());
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(7, new RandomStrollGoal(this, 0.8));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("NpcFaction", getFaction().name());
        tag.putString("NpcClass", getNpcClass().name());
        tag.putString("NpcOrder", getOrder().name());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setFaction(parseEnum(Faction.class, tag.getString("NpcFaction"), Faction.TEAMMATE));
        setNpcClass(parseEnum(NpcClass.class, tag.getString("NpcClass"), NpcClass.ASSAULT));
        setOrder(parseEnum(TeammateOrder.class, tag.getString("NpcOrder"), TeammateOrder.WANDER));
    }

    private static <T extends Enum<T>> T parseEnum(Class<T> type, String value, T fallback) {
        try {
            return Enum.valueOf(type, value);
        } catch (IllegalArgumentException | NullPointerException ignored) {
            return fallback;
        }
    }

    public Faction getFaction() { return parseEnum(Faction.class, entityData.get(FACTION), Faction.TEAMMATE); }
    public void setFaction(Faction faction) { entityData.set(FACTION, faction.name()); }
    public NpcClass getNpcClass() { return parseEnum(NpcClass.class, entityData.get(NPC_CLASS), NpcClass.ASSAULT); }
    public void setNpcClass(NpcClass npcClass) { entityData.set(NPC_CLASS, npcClass.name()); }
    public TeammateOrder getOrder() { return parseEnum(TeammateOrder.class, entityData.get(ORDER), TeammateOrder.WANDER); }
    public void setOrder(TeammateOrder order) { entityData.set(ORDER, order.name()); }
}
