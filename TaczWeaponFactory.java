package ru.tacticalnpcs.tacz;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.builder.GunItemBuilder;
import com.tacz.guns.resource.index.CommonGunIndex;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import ru.tacticalnpcs.common.NpcClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Creates a randomly selected TaCZ gun from the live common index.
 * This includes datapack-added guns because it does not hardcode gun IDs.
 */
public final class TaczWeaponFactory {
    private TaczWeaponFactory() {}

    public static ItemStack createRandomGun(NpcClass npcClass,
                                             HolderLookup.Provider registries,
                                             RandomSource random) {
        if (!TaczBridge.isTaczPresent()) {
            return ItemStack.EMPTY;
        }

        List<Identifier> candidates = new ArrayList<>();
        for (Map.Entry<Identifier, CommonGunIndex> entry : TimelessAPI.getAllCommonGunIndex()) {
            String type = entry.getValue().getType();
            boolean accepted = switch (npcClass) {
                case ASSAULT -> "smg".equals(type) || "rifle".equals(type);
                case MACHINE_GUNNER -> "mg".equals(type);
                case SNIPER -> "sniper".equals(type);
            };
            if (accepted) {
                candidates.add(entry.getKey());
            }
        }

        if (candidates.isEmpty()) {
            return ItemStack.EMPTY;
        }

        Identifier selectedGun = candidates.get(random.nextInt(candidates.size()));
        CommonGunIndex index = TimelessAPI.getCommonGunIndex(selectedGun).orElse(null);
        if (index == null || index.getGunData() == null) {
            return ItemStack.EMPTY;
        }

        int magazineCapacity = Math.max(1, index.getGunData().getAmmoAmount());
        return GunItemBuilder.create()
            .setId(selectedGun)
            .setAmmoCount(magazineCapacity)
            .setAmmoInBarrel(true)
            .build(registries);
    }
}
