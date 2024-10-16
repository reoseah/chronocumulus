package io.github.reoseah.chronocumulus;

import io.github.reoseah.chronocumulus.block.CloudBlock;
import io.github.reoseah.chronocumulus.block.ProtrusionBlock;
import io.github.reoseah.chronocumulus.block.PuffBlock;
import io.github.reoseah.chronocumulus.block.WallProtrusionBlock;
import io.github.reoseah.chronocumulus.structure.ChronocumulusStructure;
import io.github.reoseah.chronocumulus.structure.GenericChronocloudPiece;
import io.github.reoseah.chronocumulus.structure.WitcheryChronocloudPiece;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Chronocumulus implements ModInitializer {
    public static final String MOD_ID = "chronocumulus";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initiating time stabilization protocol...");

        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, CloudBlock.CLOUD_ID), CloudBlock.CLOUD_BLOCK);
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, CloudBlock.SMOOTH_ID), CloudBlock.SMOOTH_BLOCK);
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, CloudBlock.BRICKS_ID), CloudBlock.BRICKS_BLOCK);
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, ProtrusionBlock.ID), ProtrusionBlock.INSTANCE);
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, WallProtrusionBlock.ID), WallProtrusionBlock.INSTANCE);
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, PuffBlock.ID), PuffBlock.INSTANCE);

        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, CloudBlock.CLOUD_ID), CloudBlock.CLOUD_ITEM);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, CloudBlock.SMOOTH_ID), CloudBlock.SMOOTH_ITEM);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, CloudBlock.BRICKS_ID), CloudBlock.BRICKS_ITEM);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, ProtrusionBlock.ID), ProtrusionBlock.ITEM);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, PuffBlock.ID), PuffBlock.ITEM);

        Registry.register(Registries.STRUCTURE_TYPE, Identifier.of(MOD_ID, "chronocumulus"), ChronocumulusStructure.TYPE);

        Registry.register(Registries.STRUCTURE_PIECE, Identifier.of(MOD_ID, "generic_chronocloud"), GenericChronocloudPiece.TYPE);
        Registry.register(Registries.STRUCTURE_PIECE, Identifier.of(MOD_ID, "witchery_chronocloud"), WitcheryChronocloudPiece.TYPE);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.add(CloudBlock.CLOUD_ITEM);
            entries.add(CloudBlock.SMOOTH_ITEM);
            entries.add(CloudBlock.BRICKS_ITEM);
            entries.add(ProtrusionBlock.ITEM);
            entries.add(PuffBlock.ITEM);
        });

        LOGGER.info("Time-space phenomena detected at extreme altitudes, further investigation requested");
    }
}