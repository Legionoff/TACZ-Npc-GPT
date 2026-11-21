package ru.tacticalnpcs.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.Identifier;
import ru.tacticalnpcs.TacticalNpcs;
import ru.tacticalnpcs.entity.TacticalNpcEntity;

public final class TacticalNpcRenderer extends MobRenderer<TacticalNpcEntity, TacticalNpcRenderState, HumanoidModel<TacticalNpcRenderState>> {
    private static final Identifier TEAM_ASSAULT = skin("teammate_assault");
    private static final Identifier TEAM_MG = skin("teammate_machine_gunner");
    private static final Identifier TEAM_SNIPER = skin("teammate_sniper");
    private static final Identifier ENEMY_ASSAULT = skin("enemy_assault");
    private static final Identifier ENEMY_MG = skin("enemy_machine_gunner");
    private static final Identifier ENEMY_SNIPER = skin("enemy_sniper");

    public TacticalNpcRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
        addLayer(new ItemInHandLayer<>(this));
    }

    private static Identifier skin(String name) {
        return Identifier.fromNamespaceAndPath(TacticalNpcs.MOD_ID, "textures/entity/skins/" + name + ".png");
    }

    @Override
    public TacticalNpcRenderState createRenderState() {
        return new TacticalNpcRenderState();
    }

    @Override
    public void extractRenderState(TacticalNpcEntity entity, TacticalNpcRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.faction = entity.getFaction().name();
        state.npcClass = entity.getNpcClass().name();
    }

    @Override
    public Identifier getTextureLocation(TacticalNpcRenderState state) {
        boolean enemy = state.faction.equals("ENEMY");
        return switch (state.npcClass) {
            case "MACHINE_GUNNER" -> enemy ? ENEMY_MG : TEAM_MG;
            case "SNIPER" -> enemy ? ENEMY_SNIPER : TEAM_SNIPER;
            default -> enemy ? ENEMY_ASSAULT : TEAM_ASSAULT;
        };
    }
}
