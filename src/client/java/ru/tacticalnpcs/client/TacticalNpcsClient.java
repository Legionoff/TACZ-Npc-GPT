package ru.tacticalnpcs.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import ru.tacticalnpcs.entity.ModEntityTypes;

public final class TacticalNpcsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRenderers.register(ModEntityTypes.TACTICAL_NPC, TacticalNpcRenderer::new);
    }
}
