package io.github.reoseah.chronocumulus.structure;

import io.github.reoseah.chronocumulus.block.CloudBlock;
import io.github.reoseah.chronocumulus.block.ProtrusionBlock;
import io.github.reoseah.chronocumulus.block.PuffBlock;
import io.github.reoseah.chronocumulus.structure.util.BoxAabbTree;
import io.github.reoseah.chronocumulus.structure.util.CylinderWithFalloff;
import io.github.reoseah.chronocumulus.structure.util.BoundedDensityFunction;
import io.github.reoseah.chronocumulus.structure.util.SimpleKernel;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.joml.SimplexNoise;

public class TestCloudPiece extends DensityHandlingPiece {
    public static final StructurePieceType TYPE = TestCloudPiece::new;

    private static final BlockState CLOUD = CloudBlock.CLOUD_BLOCK.getDefaultState();
    private static final BlockState PUFF = PuffBlock.INSTANCE.getDefaultState();
    private static final BlockState PROTRUSION = ProtrusionBlock.INSTANCE.getDefaultState();

    protected final int seed;

    public TestCloudPiece(int generation, BlockPos pos, Random random) {
        super(TYPE, generation, createDensityFunctions(pos, random));
        this.seed = random.nextInt();
    }

    protected static final double GOLDEN_ANGLE = 2 * Math.PI * (1 - 0.618_033_988_749_894);

    private static BoxAabbTree<BoundedDensityFunction> createDensityFunctions(BlockPos pos, Random random) {
        var main = new CylinderWithFalloff(Vec3d.ofCenter(pos), .5, 2, 1, 4, 1);
        var tree = new BoxAabbTree<BoundedDensityFunction>(main);

        for (int i = 1; i < 20; i++) {
            double angle = i * GOLDEN_ANGLE;
            double distance = Math.sqrt(i) * 4;

            double x = pos.getX() + distance * Math.cos(angle);
            double y = pos.getY() + random.nextDouble() * 2 - 1;
            double z = pos.getZ() + distance * Math.sin(angle);

            double radius = 1 + random.nextDouble() * 2;
            double radiusFallof = 2 + random.nextDouble() * 4;
            double halfHeight = random.nextDouble();
            double heightFallof = 1 + random.nextDouble();

            var function = new CylinderWithFalloff(new Vec3d(x, y, z), .5, radius, halfHeight, radiusFallof, heightFallof);
            tree.add(function);
        }

        return tree;
    }

    public TestCloudPiece(StructureContext context, NbtCompound data) {
        super(TYPE, context, data);
        this.seed = data.getInt("seed");
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound data) {
        super.writeNbt(context, data);
        data.putInt("seed", this.seed);
    }

    @Override
    public void generate(StructureWorldAccess world, //
                         StructureAccessor structures, //
                         ChunkGenerator chunkGenerator, //
                         Random chunkRandom, //
                         BlockBox chunkBox, //
                         ChunkPos chunkPos, //
                         BlockPos pivot) {
        assert this.getRotation() == BlockRotation.NONE;

        int minX = Math.max(this.boundingBox.getMinX(), chunkBox.getMinX());
        int maxX = Math.min(this.boundingBox.getMaxX(), chunkBox.getMaxX());
        int minY = Math.max(this.boundingBox.getMinY(), chunkBox.getMinY());
        int maxY = Math.min(this.boundingBox.getMaxY(), chunkBox.getMaxY());
        int minZ = Math.max(this.boundingBox.getMinZ(), chunkBox.getMinZ());
        int maxZ = Math.min(this.boundingBox.getMaxZ(), chunkBox.getMaxZ());

        var functions = this.densityFunctions.intersectingObjects(Box.from(chunkBox));

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                boolean isInside = false;
                for (int y = maxY; y >= minY; y--) {
                    double density = 0;
                    for (var function : functions) {
                        double value = function.getDensity(x + .5, y + .5, z + .5);
                        density = (density + value) / (1 + Math.abs(density * value));
                    }
                    if (density <= .35) {
                        continue;
                    }
                    double erodedWeight = density - SimplexNoise.noise(x * .15f, y * .15f, z * .15f) * .05f;
                    if (erodedWeight > 0.4 && chunkRandom.nextFloat() > .05) {
                        this.addBlock(world, CLOUD, x, y, z, chunkBox);
                        if (!isInside && chunkRandom.nextBoolean()) {
                            this.addBlock(world, PROTRUSION, x, y + 1, z, chunkBox);
                        }
                        isInside = true;
                    } else {
                        this.addBlock(world, PUFF, x, y, z, chunkBox);
                    }
                }
            }
        }
    }
}
