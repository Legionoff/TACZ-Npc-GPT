# TaCZ 0.8.5-beta1 API inspection

Inspected the exact user-provided file:
`TACZ-Refabricated-26.3-0.8.5-beta1+fabric26.3.jar`

## Verified metadata

- Mod id: `tacz`
- Mod version: `0.8.5-beta1+fabric26.3`
- Minecraft dependency: `~26.3`
- Fabric Loader minimum: `0.19.5`
- Fabric API minimum: `0.161.0`
- Java minimum: `25`
- The jar contains the public API classes used below.

## Verified category model

The gun index records contain a `type` field. The bundled default gun pack has 54 gun index records:

| TaCZ type | Count | Tactical NPC use |
|---|---:|---|
| `smg` | 5 | Assault |
| `rifle` | 17 | Assault |
| `mg` | 4 | Machine gunner |
| `sniper` | 6 | Sniper |
| `shotgun` | 6 | Not included in the requested assault pool |
| `pistol` | 14 | Not used |
| `rpg` | 2 | Not used |

The bundled default pack's IDs by requested pool:

- Assault: `smg` + `rifle`
- Machine gunner: `mg`
- Sniper: `sniper`

This must be queried from TaCZ's live common index at runtime, rather than hardcoding the bundled gun IDs, so compatible datapacks can add weapons.

## Verified public API signatures

The jar contains:

- `com.tacz.guns.api.TimelessAPI`
  - `getAllCommonGunIndex()`
  - `getCommonGunIndex(Identifier)`
- `com.tacz.guns.resource.index.CommonGunIndex`
  - `getType()`
  - `getGunData()`
  - `getPojo()`
- `com.tacz.guns.api.item.builder.GunItemBuilder`
  - `create()`
  - `setId(Identifier)`
  - `setAmmoCount(int)`
  - `build(HolderLookup.Provider)`
  - `forceBuild(HolderLookup.Provider)`
- `com.tacz.guns.api.item.IGun`
  - `getCurrentAmmoCount(ItemStack)` / `setCurrentAmmoCount(ItemStack, int)`
  - `getGunId(ItemStack)`
  - `hasInventoryAmmo(LivingEntity, ItemStack, boolean)`
- `com.tacz.guns.api.item.gun.AbstractGunItem`
  - `shoot(ShooterDataHolder, ItemStack, Supplier<Float>, Supplier<Float>, LivingEntity)`
  - `startReload(ShooterDataHolder, ItemStack, LivingEntity)`
  - `tickReload(ShooterDataHolder, ItemStack, LivingEntity)`
  - `interruptReload(ShooterDataHolder, ItemStack, LivingEntity)`
- `com.tacz.guns.entity.shooter.ShooterDataHolder`
- `com.tacz.guns.entity.shooter.LivingEntityShoot`
- `com.tacz.guns.entity.shooter.LivingEntityReload`
- `com.tacz.guns.api.entity.ReloadState` and `ReloadState.StateType`

These are real symbols from the supplied jar, not guessed names. The NPC runtime still needs to wire the shooter data holder and invoke the API on the logical server; that is not implemented by this inspection report.

## Ammo and reload design

The requested infinite reserve / finite magazine behavior should be implemented by leaving the gun's `currentAmmoCount` finite and supplying the correct TaCZ ammo item(s) in the NPC's inventory handler. When the magazine is empty or low, the NPC must use TaCZ's normal reload lifecycle, not simply reset the magazine count. That preserves the reload animation/timing and bolt behavior.

The inspected `AbstractGunItem` exposes `startReload` and `tickReload`; the API also has reload state types for empty and tactical reload. The NPC must keep its reserve ammo replenished, while allowing the actual magazine to empty and reload normally.

## Remaining implementation work

1. Add TaCZ as a compile-time API dependency / optional runtime dependency without bundling its jar.
2. Implement live category enumeration from `TimelessAPI.getAllCommonGunIndex()`.
3. Build gun stacks from selected live gun IDs using `GunItemBuilder`.
4. Resolve the ammo item ID from `CommonGunIndex.getGunData().getAmmoId()` and supply infinite reserve through the NPC inventory integration.
5. Wire `ShooterDataHolder`, `LivingEntityShoot`, and `LivingEntityReload` to the NPC's server-side combat tick.
6. Register the NPC entities, AI goals, orders, spawn eggs and renderers.
7. Build with JDK 25 and test in an actual Fabric 26.3 client/server.

## Important limitation

This inspection confirms API symbols and category values only. It does not claim that a playable NPC implementation or a successful mod build exists yet.


## Implemented in source milestone 0.1.2

- Gradle now declares TaCZ as `modCompileOnly` using the Modrinth Maven coordinate, so the TaCZ jar is not bundled.
- TaCZ presence check uses the verified mod id `tacz` (not an assumed port-specific id).
- `TaczWeaponFactory` enumerates `TimelessAPI.getAllCommonGunIndex()` and selects a random index entry by the verified `CommonGunIndex.getType()` values:
  - assault: `smg` or `rifle`
  - machine gunner: `mg`
  - sniper: `sniper`
- It creates the selected stack with the verified `GunItemBuilder` API and fills the finite magazine to `GunData.getAmmoAmount()`.

This is a concrete gun-selection/stack-creation adapter. It does not yet make the NPC shoot or reload; those operations require the actual LivingEntity NPC, TaCZ shooter lifecycle, and ammo handler integration.
