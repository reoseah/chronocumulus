package io.github.reoseah.chronocumulus.structure.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class CylinderWithFalloff implements BoundedDensityFunction {
    public final Vec3d center;
    public final double value, radius, halfHeight, horizontalFalloffDistance, verticalFalloffDistance;

    public CylinderWithFalloff(Vec3d center, double value, double radius, double halfHeight, double horizontalFalloffDistance, double verticalFalloffDistance) {
        this.center = center;
        this.value = value;
        this.radius = radius;
        this.halfHeight = halfHeight;
        this.horizontalFalloffDistance = horizontalFalloffDistance;
        this.verticalFalloffDistance = verticalFalloffDistance;
    }

    @Override
    public double getDensity(double x, double y, double z) {
        var point = new Vec3d(x, y, z).subtract(this.center);
        var radialDistance = point.horizontalLength();
        var verticalDistance = Math.abs(point.y);

        if (radialDistance < this.radius && verticalDistance < this.halfHeight) {
            return this.value;
        }
        if (radialDistance > this.radius + this.horizontalFalloffDistance //
                || verticalDistance > this.halfHeight + this.verticalFalloffDistance) {
            return 0;
        }
        var radialFactor = 1 - (radialDistance - this.radius) / this.horizontalFalloffDistance;
        var verticalFactor = 1 - (verticalDistance - this.halfHeight) / this.verticalFalloffDistance;

        return Math.min(radialFactor, verticalFactor) * this.value;
    }

    @Override
    public Box getBounds() {
        var outerRadius = this.radius + this.horizontalFalloffDistance;
        var outerHalfHeight = this.halfHeight + this.verticalFalloffDistance;
        return new Box(this.center.subtract(outerRadius, outerHalfHeight, outerRadius), //
                this.center.add(outerRadius, outerHalfHeight, outerRadius));
    }

    @Override
    public NbtCompound toNbt() {
        var nbt = new NbtCompound();
        nbt.putString("type", "cylinder_with_falloff");
        nbt.putDouble("x", this.center.x);
        nbt.putDouble("y", this.center.y);
        nbt.putDouble("z", this.center.z);
        nbt.putDouble("value", this.value);
        nbt.putDouble("r", this.radius);
        nbt.putDouble("hh", this.halfHeight);
        nbt.putDouble("hfd", this.horizontalFalloffDistance);
        nbt.putDouble("vfd", this.verticalFalloffDistance);
        return nbt;
    }

    public static CylinderWithFalloff fromNbt(NbtCompound nbt) {
        var center = new Vec3d(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
        var value = nbt.getDouble("value");
        var radius = nbt.getDouble("r");
        var halfHeight = nbt.getDouble("hh");
        var horizontalFalloffDistance = nbt.getDouble("hfd");
        var verticalFalloffDistance = nbt.getDouble("vfd");
        return new CylinderWithFalloff(center, value, radius, halfHeight, horizontalFalloffDistance, verticalFalloffDistance);
    }
}
