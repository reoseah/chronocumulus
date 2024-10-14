package io.github.reoseah.chronocumulus.structure;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Optional;

public class ChronocumulusStructure extends Structure {
    public static final MapCodec<ChronocumulusStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> //
            instance.group(configCodecBuilder(instance)) //
                    .apply(instance, ChronocumulusStructure::new));

    public static final StructureType<ChronocumulusStructure> TYPE = () -> CODEC;

    public ChronocumulusStructure(Structure.Config settings) {
        super(settings);
    }

    @Override
    public StructureType<?> getType() {
        return TYPE;
    }

    @Override
    protected Optional<StructurePosition> getStructurePosition(Context context) {
        ChunkPos chunk = context.chunkPos();
        ChunkRandom random = context.random();
        BlockPos pos = chunk.getBlockPos(0, context.chunkGenerator().getMinimumY() + context.chunkGenerator().getWorldHeight() - 20, 0);

        return Optional.of(new StructurePosition(pos, Either.left(builder -> {
            StructurePiece start = new WitcheryChronocloudPiece(0, pos, random.split());
            builder.addPiece(start);
            start.fillOpenings(start, builder, random.split());
        })));
    }
}
