package io.github.reoseah.chronocumulus.structure;

import io.github.reoseah.chronocumulus.block.CloudBlock;
import io.github.reoseah.chronocumulus.structure.util.BoxAabbTree;
import io.github.reoseah.chronocumulus.structure.util.CylinderWithFalloff;
import io.github.reoseah.chronocumulus.structure.util.DensityFunction;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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

    protected final int seed;

    public TestCloudPiece(int generation, BlockPos pos, Random random) {
        super(TYPE, generation, createDensityFunctions(pos, random));
        this.seed = random.nextInt();
    }

    private static BoxAabbTree<DensityFunction> createDensityFunctions(BlockPos pos, Random random) {
        var cylinder = new CylinderWithFalloff(Vec3d.ofCenter(pos), .5, 8, 2, 4, 3);
        var tree = new BoxAabbTree<DensityFunction>(cylinder);
        for (int i = 0; i < 5; i++) {
            var offset = new BlockPos(random.nextInt(17) - 8, random.nextInt(5) - 2, random.nextInt(17) - 8);
            var cylinder2 = new CylinderWithFalloff(Vec3d.ofCenter(pos.add(offset)), .5, 4, 1, 2, 2);
            tree.add(cylinder2);
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
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    double density = 0;
                    for (var function : functions) {
                        double value = function.getWeight(x, y, z);
                        density = (density + value) / (1 + Math.abs(density * value));
                    }
                    if (density <= 0.5) {
                        continue;
                    }
                    double erodedWeight = density - SimplexNoise.noise(x * .15f, y * .15f, z * .15f) * .05f;
                    if (erodedWeight > .5f) {
                        this.addBlock(world, CLOUD, x, y, z, chunkBox);
                    }
                }
            }
        }
    }
}
