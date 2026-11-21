# Tactical NPCs — development foundation

Target: Minecraft Java 26.3 / Fabric / Java 25  
Requested TaCZ build: `0.8.5-beta1+fabric26.3` (Modrinth version ID `3BP2Kc1r`)

## Included in this first source milestone

- Fabric 26.3 Gradle/Loom project skeleton.
- Core enums for teammate/enemy factions, three classes, teammate orders and weapon categories.
- Class weapon-pool and tactical-range definitions.
- Basic faction targeting policy.
- TaCZ compatibility boundary that deliberately refuses to call unverified APIs.
- Six supplied 64x64 player skins copied unchanged into `assets/tactical_npcs/textures/entity/skins/`.
- Registered `tactical_npcs:tactical_npc` living entity with synchronized/persistent faction, class, and teammate order.
- Teammates support Follow, Hold Position, and Wander; right-click cycles the order.
- Client humanoid renderer maps all six supplied skins to faction/class combinations.
- `/tnpc status` development command.

## TaCZ API inspection

See [`docs/TAcz-0.8.5-beta1-inspection.md`](docs/TAcz-0.8.5-beta1-inspection.md). The uploaded 0.8.5-beta1 JAR was inspected directly: live gun categories are `smg`, `rifle`, `mg`, and `sniper`; the adapter uses the verified `TimelessAPI` and `GunItemBuilder` APIs. NPC firing/reloading is not wired yet.

## Implemented TaCZ weapon factory

`TaczWeaponFactory` now selects a random registered TaCZ gun from the live index and builds a finite loaded magazine for each class. TaCZ is a compile-only dependency and must be installed separately. This does not yet wire NPC firing or reload behavior.

## Not implemented yet

This is **not a finished playable mod**. The current source builds in GitHub Actions, but has not been smoke-tested in Minecraft. Remaining runtime work:
1. Add six faction/class spawn eggs and a convenient spawn command.
2. Add faction-aware target selection and class-specific combat goals.
3. Wire TaCZ shooting, reload lifecycle, and infinite reserve ammo into the NPC entity.
4. Add class-specific movement/tactics and finish order handling.
5. Run client and dedicated-server smoke tests in Minecraft 26.3 with TaCZ installed.

The TaCZ port's Modrinth page lists Minecraft 26.3 and Fabric support, but the project page does not publish its source code. The adapter is therefore kept isolated rather than using invented API symbols.

## Skin paths

- `textures/entity/skins/teammate_assault.png`
- `textures/entity/skins/teammate_machine_gunner.png`
- `textures/entity/skins/teammate_sniper.png`
- `textures/entity/skins/enemy_assault.png`
- `textures/entity/skins/enemy_machine_gunner.png`
- `textures/entity/skins/enemy_sniper.png`

## Build requirements

Use JDK 25 and Gradle 9.8.0 (the version configured in CI). From this directory run:

```bash
gradle build
```

The build output will be under `build/libs/`.

## Important TaCZ note

Install TaCZ separately on both client and server. It is not bundled or redistributed in this project.


## GitHub Actions build (Java 25)

A workflow is included at `.github/workflows/build.yml`. After pushing this project to a GitHub repository:
1. Open the repository's **Actions** tab.
2. Select **Build Tactical NPCs**.
3. Run it with **Run workflow**, or push a commit.
4. Download the `tactical-npcs-build` artifact from a successful run.

The workflow uses Eclipse Temurin Java 25 and Gradle 9.8.0. It runs `gradle clean build` and uploads any JARs and Gradle reports. CI confirms compilation and packaging; an in-game client/server smoke test is still required before calling the mod playable.
