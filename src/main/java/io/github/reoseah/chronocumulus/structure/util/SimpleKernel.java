package io.github.reoseah.chronocumulus.structure.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class SimpleKernel implements BoundedDensityFunction {
    public final Vec3d origin, normal;
    public final double radius, amplitude;

    public SimpleKernel(BlockPos origin, Vec3d normal, double radius, double amplitude) {
        this(Vec3d.ofCenter(origin), normal, radius, amplitude);
    }

    public SimpleKernel(Vec3d origin, Vec3d normal, double radius, double amplitude) {
        this.origin = origin;
        this.normal = normal.normalize();
        this.radius = radius;
        this.amplitude = amplitude;
    }

    @Override
    public double getDensity(double x, double y, double z) {
        Vec3d point = new Vec3d(x, y, z).subtract(this.origin);

        double distance = point.length();
        if (distance >= this.radius) {
            return 0;
        }
        double distanceToPlane = point.dotProduct(this.normal);

        // distance to origin between 0 and 1
        double sphereFactor = (this.radius - distance) / this.radius;
        // distance to plane between -1 and 1
        double planeFactor = distanceToPlane / this.radius;

        // factor of 4 to scale from (-.25, .25) to (-1, 1)
        return this.amplitude * 4 * planeFactor * sphereFactor;
    }

    @Override
    public Box getBounds() {
        return new Box(this.origin.subtract(this.radius, this.radius, this.radius), //
                this.origin.add(this.radius, this.radius, this.radius));
    }

    @Override
    public NbtCompound toNbt() {
        NbtCompound tag = new NbtCompound();

        tag.putString("type", "simple_kernel");

        tag.putDouble("ox", this.origin.x);
        tag.putDouble("oy", this.origin.y);
        tag.putDouble("oz", this.origin.z);
        tag.putDouble("nx", this.normal.x);
        tag.putDouble("ny", this.normal.y);
        tag.putDouble("nz", this.normal.z);
        tag.putDouble("r", this.radius);
        tag.putDouble("a", this.amplitude);

        return tag;
    }

    public static SimpleKernel fromNbt(NbtCompound tag) {
        Vec3d origin = new Vec3d(tag.getDouble("ox"), tag.getDouble("oy"), tag.getDouble("oz"));
        Vec3d normal = new Vec3d(tag.getDouble("nx"), tag.getDouble("ny"), tag.getDouble("nz"));
        double radius = tag.getDouble("r");
        double amplitude = tag.getDouble("a");

        return new SimpleKernel(origin, normal, radius, amplitude);
    }
}
