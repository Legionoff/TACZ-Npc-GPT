package ru.tacticalnpcs.entity;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.item.ItemStack;
import ru.tacticalnpcs.ai.ClassTactics;
import ru.tacticalnpcs.tacz.TaczBridge;
import ru.tacticalnpcs.tacz.TaczWeaponFactory;
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
    private boolean taczGunDrawn;

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
        builder.define(FACTION, defaultFaction().name());
        builder.define(NPC_CLASS, defaultClass().name());
        builder.define(ORDER, TeammateOrder.WANDER.name());
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(7, new OrderAwareWanderGoal());
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new NearestAttackableTargetGoal<TacticalNpcEntity>(
            this, TacticalNpcEntity.class, 10, true, false,
            (candidate, serverLevel) -> candidate instanceof TacticalNpcEntity npc && npc.getFaction() != getFaction()
        ));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<Player>(
            this, Player.class, 10, true, false,
            (candidate, serverLevel) -> getFaction() == Faction.ENEMY
        ));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<Monster>(
            this, Monster.class, 10, true, false, (candidate, serverLevel) -> true
        ));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) return;

        ensureTaczEquipment();
        tickCombat();

        if (getFaction() != Faction.TEAMMATE) return;

        if (getOrder() == TeammateOrder.HOLD_POSITION) {
            getNavigation().stop();
            return;
        }
        if (getOrder() == TeammateOrder.FOLLOW) {
            Player closest = null;
            double closestDistance = 48.0 * 48.0;
            for (Player player : level().players()) {
                if (player.isSpectator() || !player.isAlive()) continue;
                double distance = distanceToSqr(player);
                if (distance < closestDistance) {
                    closest = player;
                    closestDistance = distance;
                }
            }
            if (closest != null && closestDistance > 3.0 * 3.0) {
                getNavigation().moveTo(closest, 1.1);
            } else {
                getNavigation().stop();
            }
        }
    }

    private void ensureTaczEquipment() {
        if (!TaczBridge.isAvailable()) return;

        if (!(getMainHandItem().getItem() instanceof IGun)) {
            ItemStack gun = TaczWeaponFactory.createRandomGun(getNpcClass(), level().registryAccess());
            if (!gun.isEmpty()) {
                setItemSlot(EquipmentSlot.MAINHAND, gun);
                taczGunDrawn = false;
            }
        }

        ItemStack gun = getMainHandItem();
        if (!(gun.getItem() instanceof IGun)) return;

        if (!taczGunDrawn) {
            IGunOperator.fromLivingEntity(this).draw(this::getMainHandItem);
            taczGunDrawn = true;
        }

        ItemStack reserve = getOffhandItem();
        if (reserve.isEmpty() || reserve.getCount() < 16) {
            ItemStack replenished = TaczWeaponFactory.createReserveAmmo(gun, 64);
            if (!replenished.isEmpty()) {
                setItemSlot(EquipmentSlot.OFFHAND, replenished);
            }
        }
    }

    private void tickCombat() {
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive() || target == this) return;
        if (target instanceof TacticalNpcEntity other && other.getFaction() == getFaction()) {
            setTarget(null);
            return;
        }
        if (!(getMainHandItem().getItem() instanceof IGun gunItem)) return;

        ClassTactics.CombatProfile profile = ClassTactics.combatProfile(getNpcClass());
        double distance = distanceToSqr(target);
        double engageRange = profile.maneuverWhileFiring()
            ? Math.max(8.0, profile.preferredMinRange() + 2.0)
            : profile.preferredMaxRange();
        if (distance > engageRange * engageRange) {
            getNavigation().moveTo(target, getNpcClass() == NpcClass.ASSAULT ? 1.15 : 0.9);
            return;
        }
        if (!profile.maneuverWhileFiring() && distance < profile.preferredMinRange() * profile.preferredMinRange()) {
            var retreat = position().subtract(target.position()).normalize();
            getNavigation().moveTo(getX() + retreat.x * 8.0, getY(), getZ() + retreat.z * 8.0, 0.9);
            return;
        }
        getNavigation().stop();
        getLookControl().setLookAt(target, 30.0f, 30.0f);

        ItemStack gun = getMainHandItem();
        if (gunItem.getCurrentAmmoCount(gun) <= 0) {
            IGunOperator.fromLivingEntity(this).reload();
            return;
        }

        double dx = target.getX() - getX();
        double dy = target.getEyeY() - getEyeY();
        double dz = target.getZ() - getZ();
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, horizontal));
        float spread = switch (getNpcClass()) {
            case ASSAULT -> 1.8f;
            case MACHINE_GUNNER -> 4.5f;
            case SNIPER -> 0.25f;
        };
        float yawOffset = (random.nextFloat() * 2.0f - 1.0f) * spread;
        float pitchOffset = (random.nextFloat() * 2.0f - 1.0f) * spread;
        IGunOperator.fromLivingEntity(this).shoot(
            () -> pitch + pitchOffset,
            () -> yaw + yawOffset,
            System.currentTimeMillis()
        );
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND || getFaction() != Faction.TEAMMATE) {
            return InteractionResult.PASS;
        }
        TeammateOrder next = switch (getOrder()) {
            case FOLLOW -> TeammateOrder.HOLD_POSITION;
            case HOLD_POSITION -> TeammateOrder.WANDER;
            case WANDER -> TeammateOrder.FOLLOW;
        };
        setOrder(next);
        player.sendSystemMessage(Component.literal("Teammate order: " + next.name().toLowerCase().replace('_', ' ')));
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putString("NpcFaction", getFaction().name());
        output.putString("NpcClass", getNpcClass().name());
        output.putString("NpcOrder", getOrder().name());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        setFaction(parseEnum(Faction.class, input.getStringOr("NpcFaction", Faction.TEAMMATE.name()), Faction.TEAMMATE));
        setNpcClass(parseEnum(NpcClass.class, input.getStringOr("NpcClass", NpcClass.ASSAULT.name()), NpcClass.ASSAULT));
        setOrder(parseEnum(TeammateOrder.class, input.getStringOr("NpcOrder", TeammateOrder.WANDER.name()), TeammateOrder.WANDER));
    }

    private static <T extends Enum<T>> T parseEnum(Class<T> type, String value, T fallback) {
        try {
            return Enum.valueOf(type, value);
        } catch (IllegalArgumentException | NullPointerException ignored) {
            return fallback;
        }
    }

    private Faction defaultFaction() {
        EntityType<?> type = getType();
        return type == ModEntityTypes.ENEMY_ASSAULT
            || type == ModEntityTypes.ENEMY_MACHINE_GUNNER
            || type == ModEntityTypes.ENEMY_SNIPER ? Faction.ENEMY : Faction.TEAMMATE;
    }

    private NpcClass defaultClass() {
        EntityType<?> type = getType();
        if (type == ModEntityTypes.TEAMMATE_MACHINE_GUNNER || type == ModEntityTypes.ENEMY_MACHINE_GUNNER) {
            return NpcClass.MACHINE_GUNNER;
        }
        if (type == ModEntityTypes.TEAMMATE_SNIPER || type == ModEntityTypes.ENEMY_SNIPER) {
            return NpcClass.SNIPER;
        }
        return NpcClass.ASSAULT;
    }

    public Faction getFaction() { return parseEnum(Faction.class, entityData.get(FACTION), Faction.TEAMMATE); }
    public void setFaction(Faction faction) { entityData.set(FACTION, faction.name()); }
    public NpcClass getNpcClass() { return parseEnum(NpcClass.class, entityData.get(NPC_CLASS), NpcClass.ASSAULT); }
    public void setNpcClass(NpcClass npcClass) { entityData.set(NPC_CLASS, npcClass.name()); }
    public TeammateOrder getOrder() { return parseEnum(TeammateOrder.class, entityData.get(ORDER), TeammateOrder.WANDER); }
    public void setOrder(TeammateOrder order) { entityData.set(ORDER, order.name()); }

    private final class OrderAwareWanderGoal extends RandomStrollGoal {
        private OrderAwareWanderGoal() {
            super(TacticalNpcEntity.this, 0.8);
        }

        @Override
        public boolean canUse() {
            return getFaction() == Faction.TEAMMATE
                && getOrder() == TeammateOrder.WANDER
                && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return getFaction() == Faction.TEAMMATE
                && getOrder() == TeammateOrder.WANDER
                && super.canContinueToUse();
        }
    }
}
