package io.github.reoseah.chronocumulus.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class PuffBlock extends Block {
    public static final String ID = "cloud_puff";
    public static final AbstractBlock.Settings SETTINGS = AbstractBlock.Settings.create().noCollision().nonOpaque().breakInstantly().offset(OffsetType.XYZ).replaceable();
    public static final VoxelShape SHAPE = Block.createCuboidShape(1, 1, 1, 15, 15, 15);
    public static final Block INSTANCE = new PuffBlock(SETTINGS);
    public static final Item ITEM = new BlockItem(INSTANCE, new Item.Settings());

    public PuffBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    protected float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1F;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Vec3d vec3d = state.getModelOffset(world, pos);
        return SHAPE.offset(vec3d.x, vec3d.y, vec3d.z);
    }

    @Override
    protected boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }
}
