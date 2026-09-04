package com.breakinblocks.miei.chronon.tempad;

import earth.terrarium.tempad.api.ActionType;
import earth.terrarium.tempad.api.tva_device.ChrononHandler;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class ChrononTransfer {
    private ChrononTransfer() {
    }

    public static void register(IEventBus gameBus) {
        gameBus.addListener(ChrononTransfer::onRightClick);
    }

    private static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (!player.isSecondaryUseActive()) {
            return;
        }
        Level level = event.getLevel();
        if (!(level.getBlockEntity(event.getPos()) instanceof ChrononHatchBlockEntity hatch)) {
            return;
        }
        ItemStack stack = event.getItemStack();
        ChrononHandler handler = ChrononHandler.Capabilities.getItem().getCapability(stack, null);
        if (handler == null || !handler.getCanExtract()) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
        if (level.isClientSide) {
            return;
        }
        ChrononBuffer buffer = hatch.buffer();
        int moved = transfer(handler, buffer);
        if (moved > 0) {
            level.playSound(null, event.getPos(), SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 0.5f, 1.6f);
            player.displayClientMessage(Component.translatable("miei.chronon.transferred",
                moved, buffer.stored(), buffer.capacity()), true);
        } else {
            player.displayClientMessage(Component.translatable(
                buffer.room() <= 0 ? "miei.chronon.hatch_full" : "miei.chronon.battery_empty"), true);
        }
    }

    private static int transfer(ChrononHandler from, ChrononBuffer to) {
        int room = to.room();
        if (room <= 0) {
            return 0;
        }
        int taken = from.extract(room, ActionType.Execute);
        if (taken <= 0) {
            return 0;
        }
        int stored = to.add(taken);
        if (stored < taken) {
            from.insert(taken - stored, ActionType.Execute);
        }
        return stored;
    }
}
