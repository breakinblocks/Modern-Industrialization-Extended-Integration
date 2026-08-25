package com.breakinblocks.miei.stress.create;

import java.util.function.Supplier;

import com.breakinblocks.miei.MIEI;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CreateSupport {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MIEI.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MIEI.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MIEI.MOD_ID);

    public static final DeferredBlock<KineticCouplerBlock> COUPLER = BLOCKS.register("kinetic_coupler",
        () -> new KineticCouplerBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .strength(2.0f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.METAL)));

    public static final DeferredItem<BlockItem> COUPLER_ITEM = ITEMS.register("kinetic_coupler",
        () -> new BlockItem(COUPLER.get(), new Item.Properties()));

    public static final Supplier<BlockEntityType<KineticCouplerBlockEntity>> COUPLER_BE =
        BLOCK_ENTITIES.register("kinetic_coupler",
            () -> BlockEntityType.Builder.of(KineticCouplerBlockEntity::new, COUPLER.get()).build(null));

    private CreateSupport() {
    }

    public static void init(IEventBus modBus) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        modBus.addListener(CreateSupport::buildCreativeTab);
    }

    private static void buildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(new ItemStack(COUPLER_ITEM.get()));
        }
    }
}
