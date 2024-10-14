package io.github.reoseah.chronocumulus.structure;

import io.github.reoseah.chronocumulus.block.CloudBlock;
import io.github.reoseah.chronocumulus.structure.util.BoundedDensityFunction;
import io.github.reoseah.chronocumulus.structure.util.BoxAabbTree;
import io.github.reoseah.chronocumulus.structure.util.CylinderWithFalloff;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class WitcheryChronocloudPiece extends GenericChronocloudPiece {
    public static final StructurePieceType TYPE = WitcheryChronocloudPiece::new;

    public WitcheryChronocloudPiece(int genDepth, BlockPos pos, Random random) {
        super(TYPE, genDepth, createDensityFunctions(pos, random));
    }

    protected static BoxAabbTree<BoundedDensityFunction> createDensityFunctions(BlockPos pos, Random random) {
        var tree = GenericChronocloudPiece.createDensityFunctions(pos, random, 20);

        var ground = new CylinderWithFalloff(Vec3d.of(pos), 1, 4, 2, 2, 0);
        tree.add(ground);

        var air = new CylinderWithFalloff(Vec3d.of(pos).add(0, 4, 0), -1, 4, 2, 2, 0);
        tree.add(air);

        return tree;
    }

    public WitcheryChronocloudPiece(StructureContext context, NbtCompound nbt) {
        super(TYPE, context, nbt);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structures, ChunkGenerator chunkGenerator, Random chunkRandom, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        super.generate(world, structures, chunkGenerator, chunkRandom, chunkBox, chunkPos, pivot);

        BlockPos ground = this.findSolidBlock(this.getCenter().up(50), Direction.DOWN);
        if (ground != null) {
            for (int x = 0; x < 3; x++) {
                for (int z = 0; z < 2; z++) {
                    this.addBlock(world, CloudBlock.SMOOTH_BLOCK.getDefaultState(), ground.getX() + x, ground.getY() + 1, ground.getZ() + z, chunkBox);
                }
            }
        }
    }
}
