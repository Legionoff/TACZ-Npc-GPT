package ru.tacticalnpcs.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import ru.tacticalnpcs.entity.ModEntityTypes;

public final class TacticalNpcsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRenderers.register(ModEntityTypes.TEAMMATE_ASSAULT, TacticalNpcRenderer::new);
        EntityRenderers.register(ModEntityTypes.TEAMMATE_MACHINE_GUNNER, TacticalNpcRenderer::new);
        EntityRenderers.register(ModEntityTypes.TEAMMATE_SNIPER, TacticalNpcRenderer::new);
        EntityRenderers.register(ModEntityTypes.ENEMY_ASSAULT, TacticalNpcRenderer::new);
        EntityRenderers.register(ModEntityTypes.ENEMY_MACHINE_GUNNER, TacticalNpcRenderer::new);
        EntityRenderers.register(ModEntityTypes.ENEMY_SNIPER, TacticalNpcRenderer::new);
    }
}
