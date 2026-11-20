package ru.tacticalnpcs.tacz;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.builder.GunItemBuilder;
import com.tacz.guns.resource.index.CommonGunIndex;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import ru.tacticalnpcs.common.NpcClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

/** Creates loaded TaCZ weapon stacks from the live common-gun index. */
public final class TaczWeaponFactory {
    private TaczWeaponFactory() {}

    public static ItemStack createRandomGun(NpcClass npcClass, HolderLookup.Provider registries) {
        if (!TaczBridge.isAvailable()) return ItemStack.EMPTY;

        List<CommonGunIndex> candidates = new ArrayList<>();
        TimelessAPI.getAllCommonGunIndex().forEach((id, index) -> {
            String type = String.valueOf(index.getType()).toLowerCase(Locale.ROOT);
            boolean accepted = switch (npcClass) {
                case ASSAULT -> type.equals("smg") || type.equals("rifle");
                case MACHINE_GUNNER -> type.equals("mg");
                case SNIPER -> type.equals("sniper");
            };
            if (accepted) candidates.add(index);
        });

        if (candidates.isEmpty()) return ItemStack.EMPTY;
        CommonGunIndex selected = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
        ResourceLocation gunId = selected.getPojo().getId();
        int magazineSize = selected.getGunData().getAmmoAmount();
        return GunItemBuilder.create().setId(gunId).setAmmoCount(magazineSize)
            .setAmmoInBarrel(true).build(registries);
    }
}
