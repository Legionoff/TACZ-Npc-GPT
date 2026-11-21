package ru.tacticalnpcs.tacz;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.builder.GunItemBuilder;
import com.tacz.guns.resource.index.CommonGunIndex;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import ru.tacticalnpcs.common.NpcClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class TaczWeaponFactory {
    private TaczWeaponFactory() {}

    public static ItemStack createRandomGun(NpcClass npcClass, HolderLookup.Provider registries) {
        if (!TaczBridge.isAvailable()) return ItemStack.EMPTY;

        List<Map.Entry<Identifier, CommonGunIndex>> candidates = new ArrayList<>();
        for (Map.Entry<Identifier, CommonGunIndex> entry : TimelessAPI.getAllCommonGunIndex()) {
            String type = entry.getValue().getType().toLowerCase(Locale.ROOT);
            boolean accepted = switch (npcClass) {
                case ASSAULT -> type.equals("smg") || type.equals("rifle");
                case MACHINE_GUNNER -> type.equals("mg");
                case SNIPER -> type.equals("sniper");
            };
            if (accepted) candidates.add(entry);
        }

        if (candidates.isEmpty()) return ItemStack.EMPTY;

        Map.Entry<Identifier, CommonGunIndex> selected =
            candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
        int magazineSize = selected.getValue().getGunData().getAmmoAmount();

        return GunItemBuilder.create()
            .setId(selected.getKey())
            .setAmmoCount(magazineSize)
            .setAmmoInBarrel(true)
            .build(registries);
    }
    public static ItemStack createReserveAmmo(ItemStack gun, int amount) {
        if (!TaczBridge.isAvailable() || gun.isEmpty()) return ItemStack.EMPTY;
        IGun gunItem = IGun.getIGunOrNull(gun);
        if (gunItem == null) return ItemStack.EMPTY;
        return TimelessAPI.getCommonGunIndex(gunItem.getGunId(gun))
            .flatMap(index -> BuiltInRegistries.ITEM.get(index.getGunData().getAmmoId()))
            .map(ammo -> ammo.value() == Items.AIR ? ItemStack.EMPTY : new ItemStack(ammo, amount))
            .orElse(ItemStack.EMPTY);
    }

}
