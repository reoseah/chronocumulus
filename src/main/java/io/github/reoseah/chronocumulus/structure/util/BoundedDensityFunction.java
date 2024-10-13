package io.github.reoseah.chronocumulus.structure.util;

import net.minecraft.nbt.NbtCompound;

public interface BoundedDensityFunction extends BoxBounded {
    double getDensity(double x, double y, double z);

    NbtCompound toNbt();

    static BoundedDensityFunction fromNbt(NbtCompound data) {
        return switch (data.getString("type")) {
            case "cylinder_with_falloff" -> CylinderWithFalloff.fromNbt(data);
            case "simple_kernel" -> SimpleKernel.fromNbt(data);
            default -> throw new IllegalArgumentException("Unknown density function type: " + data);
        };
    }
}
