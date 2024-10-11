package io.github.reoseah.chronocumulus;

import io.github.reoseah.chronocumulus.cloud.ProtrusionBlock;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class ChronocumulusClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                ProtrusionBlock.INSTANCE);
    }
}