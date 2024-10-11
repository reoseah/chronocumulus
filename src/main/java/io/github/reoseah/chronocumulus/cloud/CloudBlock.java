package io.github.reoseah.chronocumulus.cloud;

import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;

public class CloudBlock extends Block {
    public static final String ID = "cloud";
    public static final Block INSTANCE = new CloudBlock(Block.Settings.create().nonOpaque().mapColor(MapColor.WHITE).strength(0.1F, 0.1F).sounds(BlockSoundGroup.SNOW));
    public static final Item ITEM = new BlockItem(INSTANCE, new Item.Settings());

    public CloudBlock(Block.Settings settings) {
        super(settings);
    }
}
