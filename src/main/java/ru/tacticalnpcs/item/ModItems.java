package ru.tacticalnpcs.item;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import ru.tacticalnpcs.TacticalNpcs;
import ru.tacticalnpcs.entity.ModEntityTypes;

public final class ModItems {
    public static final Item TEAMMATE_ASSAULT_EGG = spawnEgg("teammate_assault_spawn_egg", ModEntityTypes.TEAMMATE_ASSAULT);
    public static final Item TEAMMATE_MACHINE_GUNNER_EGG = spawnEgg("teammate_machine_gunner_spawn_egg", ModEntityTypes.TEAMMATE_MACHINE_GUNNER);
    public static final Item TEAMMATE_SNIPER_EGG = spawnEgg("teammate_sniper_spawn_egg", ModEntityTypes.TEAMMATE_SNIPER);
    public static final Item ENEMY_ASSAULT_EGG = spawnEgg("enemy_assault_spawn_egg", ModEntityTypes.ENEMY_ASSAULT);
    public static final Item ENEMY_MACHINE_GUNNER_EGG = spawnEgg("enemy_machine_gunner_spawn_egg", ModEntityTypes.ENEMY_MACHINE_GUNNER);
    public static final Item ENEMY_SNIPER_EGG = spawnEgg("enemy_sniper_spawn_egg", ModEntityTypes.ENEMY_SNIPER);

    private ModItems() {}

    private static Item spawnEgg(String path, net.minecraft.world.entity.EntityType<?> entityType) {
        ResourceKey<Item> key = ResourceKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath(TacticalNpcs.MOD_ID, path)
        );
        Item item = new SpawnEggItem(new Item.Properties().setId(key).spawnEgg(entityType));
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register(tab -> {
            tab.accept(TEAMMATE_ASSAULT_EGG);
            tab.accept(TEAMMATE_MACHINE_GUNNER_EGG);
            tab.accept(TEAMMATE_SNIPER_EGG);
            tab.accept(ENEMY_ASSAULT_EGG);
            tab.accept(ENEMY_MACHINE_GUNNER_EGG);
            tab.accept(ENEMY_SNIPER_EGG);
        });
    }
}
