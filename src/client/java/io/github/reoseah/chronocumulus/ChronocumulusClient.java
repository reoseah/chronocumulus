package io.github.reoseah.chronocumulus;

import io.github.reoseah.chronocumulus.block.PuffBlock;
import io.github.reoseah.chronocumulus.block.WallProtrusionBlock;
import io.github.reoseah.chronocumulus.block.ProtrusionBlock;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class ChronocumulusClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                ProtrusionBlock.INSTANCE, WallProtrusionBlock.INSTANCE, PuffBlock.INSTANCE);
    }



}