package io.github.reoseah.chronocumulus;

import io.github.reoseah.chronocumulus.cloud.CloudBlock;
import net.fabricmc.api.ModInitializer;

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
		Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, CloudBlock.BRICKS_ID), CloudBlock.BRICKS_BLOCK);

		Registry.register(Registries.ITEM, Identifier.of(MOD_ID, CloudBlock.CLOUD_ID), CloudBlock.CLOUD_ITEM);
		Registry.register(Registries.ITEM, Identifier.of(MOD_ID, CloudBlock.BRICKS_ID), CloudBlock.BRICKS_ITEM);
	}
}