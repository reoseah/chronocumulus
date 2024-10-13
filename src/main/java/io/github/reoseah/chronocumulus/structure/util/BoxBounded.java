package io.github.reoseah.chronocumulus.structure.util;

import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

public interface BoxBounded {
    Box getBounds();

    default BlockBox getBoundingBlockBox() {
        Box box = getBounds();
        return new BlockBox(
                MathHelper.floor(box.minX),
                MathHelper.floor(box.minY),
                MathHelper.floor(box.minZ),
                MathHelper.ceil(box.maxX),
                MathHelper.ceil(box.maxY),
                MathHelper.ceil(box.maxZ)
        );
    }
}
