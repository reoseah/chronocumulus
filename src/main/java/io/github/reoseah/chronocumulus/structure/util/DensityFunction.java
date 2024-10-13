package io.github.reoseah.chronocumulus.structure.util;

import net.minecraft.nbt.NbtCompound;

public interface DensityFunction extends BoxBounded {
    double getWeight(int x, int y, int z);

    NbtCompound toNbt();

    static DensityFunction fromNbt(NbtCompound data) {
        return switch (data.getString("type")) {
            case "cylinder_with_falloff" -> CylinderWithFalloff.fromNbt(data);
            default -> throw new IllegalArgumentException("Unknown density function type: " + data);
        };
    }
}
