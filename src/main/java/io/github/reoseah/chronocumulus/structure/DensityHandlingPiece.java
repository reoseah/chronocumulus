package io.github.reoseah.chronocumulus.structure;

import io.github.reoseah.chronocumulus.structure.util.BoundedDensityFunction;
import io.github.reoseah.chronocumulus.structure.util.BoxAabbTree;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.jetbrains.annotations.Nullable;
import org.joml.SimplexNoise;

import java.util.Collection;

public abstract class DensityHandlingPiece extends StructurePiece {
    protected final BoxAabbTree<BoundedDensityFunction> densityFunctions;

    protected DensityHandlingPiece(StructurePieceType type, int genDepth, BoxAabbTree<BoundedDensityFunction> densityFunctions) {
        super(type, genDepth, densityFunctions.getBoundingBlockBox());
        this.setOrientation(null);
        this.densityFunctions = densityFunctions;
    }

    protected DensityHandlingPiece(StructurePieceType type, StructureContext context, NbtCompound data) {
        super(type, data);
        this.densityFunctions = readDensityFunctions(data.getList("density_functions", NbtElement.COMPOUND_TYPE));
    }

    private static BoxAabbTree<BoundedDensityFunction> readDensityFunctions(NbtList data) {
        var densityFunctions = new BoxAabbTree<>(BoundedDensityFunction.fromNbt(data.getCompound(0)));
        if (data.size() == 1) {
            return densityFunctions;
        }
        for (int i = 1; i < data.size(); i++) {
            densityFunctions.add(BoundedDensityFunction.fromNbt(data.getCompound(i)));
        }
        return densityFunctions;
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        nbt.put("density_functions", writeDensityFunctions(this.densityFunctions));
    }

    private static NbtList writeDensityFunctions(BoxAabbTree<BoundedDensityFunction> densityFunctions) {
        var data = new NbtList();
        for (var element : densityFunctions.getObjects()) {
            data.add(element.toNbt());
        }
        return data;
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
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
                        double value = function.getDensity(x, y, z);
                        density = (density + value) / (1 + Math.abs(density * value));
                    }
                    if (density > .5) {
                        this.addBlock(world, Blocks.BEDROCK.getDefaultState(), x, y, z, chunkBox);
                    }
                }
            }
        }

        throw new Error("Example implementation, should be overridden");
    }

    public @Nullable BlockPos findSolidBlock(BlockPos from, Direction direction) {
        BlockPos to = from.offset(direction, 100);
        Box box = Box.enclosing(from, to);
        Collection<BoundedDensityFunction> relevantWeights = this.densityFunctions.intersectingObjects(box);

        for (int i = 0; i < 100; i++) {
            BlockPos pos = from.offset(direction, i);
            double density = 0;
            for (BoundedDensityFunction func : relevantWeights) {
                double layer = func.getDensity(pos.getX(), pos.getY(), pos.getZ());
                density = (density + layer) / (1 + Math.abs(density * layer));
            }
            if (density > .5f) {
                density -= SimplexNoise.noise(pos.getX() * .15f, pos.getY() * .15f, pos.getZ() * .15f) * .05f;
                if (density > .5f) {
                    return pos;
                }
            }
        }
        return null;
    }
}
