package ru.tacticalnpcs.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import ru.tacticalnpcs.TacticalNpcs;

public final class ModEntityTypes {
    public static final EntityType<TacticalNpcEntity> TEAMMATE_ASSAULT = register("teammate_assault");
    public static final EntityType<TacticalNpcEntity> TEAMMATE_MACHINE_GUNNER = register("teammate_machine_gunner");
    public static final EntityType<TacticalNpcEntity> TEAMMATE_SNIPER = register("teammate_sniper");
    public static final EntityType<TacticalNpcEntity> ENEMY_ASSAULT = register("enemy_assault");
    public static final EntityType<TacticalNpcEntity> ENEMY_MACHINE_GUNNER = register("enemy_machine_gunner");
    public static final EntityType<TacticalNpcEntity> ENEMY_SNIPER = register("enemy_sniper");

    /** Backwards-compatible default type for development commands. */
    public static final EntityType<TacticalNpcEntity> TACTICAL_NPC = TEAMMATE_ASSAULT;

    private ModEntityTypes() {}

    private static EntityType<TacticalNpcEntity> register(String path) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(
            Registries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(TacticalNpcs.MOD_ID, path)
        );
        EntityType<TacticalNpcEntity> type = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            key,
            EntityType.Builder.<TacticalNpcEntity>of(TacticalNpcEntity::new, MobCategory.CREATURE)
                .sized(0.6f, 1.95f)
                .build(key)
        );
        FabricDefaultAttributeRegistry.register(type, TacticalNpcEntity.createAttributes());
        return type;
    }

    public static void initialize() {}
}
