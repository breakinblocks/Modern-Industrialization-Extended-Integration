package com.breakinblocks.miei.mixin.tesseract;

import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition.Context;
import net.swedz.tesseract.neoforge.compat.mi.component.craft.AbstractModularCrafterComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AbstractModularCrafterComponent.class, remap = false)
public interface ModularCrafterComponentAccessor {
    @Accessor("conditionContext")
    Context miei$getConditionContext();
}
