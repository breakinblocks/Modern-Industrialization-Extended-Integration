package com.breakinblocks.miei.mixin.tesseract;

import net.swedz.tesseract.neoforge.compat.mi.component.craft.multiplied.MultipliedCrafterComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = MultipliedCrafterComponent.class, remap = false)
public interface MultipliedCrafterComponentAccessor {
    @Accessor("tryRecipeMultiplier")
    int miei$getTryRecipeMultiplier();
}
