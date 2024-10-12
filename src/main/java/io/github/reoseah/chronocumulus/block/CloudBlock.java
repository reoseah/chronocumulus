package io.github.reoseah.chronocumulus.block;

import io.github.reoseah.chronocumulus.Chronocumulus;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class CloudBlock extends Block {
    protected static final VoxelShape COLLISION_SHAPE = Block.createCuboidShape(0, 0, 0, 16.0, 14.0, 16.0);
    public static final AbstractBlock.Settings CLOUD_SETTINGS = AbstractBlock.Settings.create().nonOpaque().mapColor(MapColor.WHITE).strength(0.1F, 0.1F).sounds(BlockSoundGroup.SNOW);

    public static final String CLOUD_ID = "cloud";
    public static final Block CLOUD_BLOCK = new CloudBlock(CLOUD_SETTINGS);
    public static final Item CLOUD_ITEM = new BlockItem(CLOUD_BLOCK, new Item.Settings());

    public static final String SMOOTH_ID = "smooth_cloud";
    public static final Block SMOOTH_BLOCK = new CloudBlock(CLOUD_SETTINGS);
    public static final Item SMOOTH_ITEM = new BlockItem(SMOOTH_BLOCK, new Item.Settings());

    public static final String BRICKS_ID = "cloud_bricks";
    public static final Block BRICKS_BLOCK = new CloudBlock(CLOUD_SETTINGS);
    public static final Item BRICKS_ITEM = new BlockItem(BRICKS_BLOCK, new Item.Settings());

    public static final TagKey<Block> SOLID_CLOUDS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(Chronocumulus.MOD_ID, "solid_clouds"));

    public CloudBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    protected float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1F;
    }

    @Override
    protected boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPE;
    }

    protected VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.fullCube();
    }

    protected VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }
}
