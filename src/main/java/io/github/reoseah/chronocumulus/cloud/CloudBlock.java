package io.github.reoseah.chronocumulus.cloud;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class CloudBlock extends Block {
    public static final AbstractBlock.Settings CLOUD_SETTINGS = AbstractBlock.Settings.create().nonOpaque().mapColor(MapColor.WHITE).strength(0.1F, 0.1F).sounds(BlockSoundGroup.SNOW);

    public static final String CLOUD_ID = "cloud";
    public static final Block CLOUD_BLOCK = new CloudBlock(CLOUD_SETTINGS);
    public static final Item CLOUD_ITEM = new BlockItem(CLOUD_BLOCK, new Item.Settings());

    public static final String BRICKS_ID = "cloud_bricks";
    public static final Block BRICKS_BLOCK = new CloudBlock(CLOUD_SETTINGS);
    public static final Item BRICKS_ITEM = new BlockItem(BRICKS_BLOCK, new Item.Settings());

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
}
