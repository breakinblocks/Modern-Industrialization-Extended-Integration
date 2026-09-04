package com.breakinblocks.miei.chronon.tempad;

import java.util.function.BooleanSupplier;

import earth.terrarium.tempad.api.ActionType;
import earth.terrarium.tempad.api.tva_device.ChrononHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;

public final class MetronomeLink {
    public static final int RADIUS = 8;
    public static final int FIRST_INTERVAL = 20;
    public static final int MAX_INTERVAL = 200;

    private static final ResourceLocation METRONOME_ID = ResourceLocation.fromNamespaceAndPath("tempad", "metronome");
    private static Block metronome;

    private final BooleanSupplier valid;
    private BlockCapabilityCache<ChrononHandler, Direction> cache;
    private long nextScan;
    private int interval = FIRST_INTERVAL;

    public MetronomeLink(BooleanSupplier valid) {
        this.valid = valid;
    }

    public BlockPos sourcePos() {
        return cache == null ? null : cache.pos();
    }

    public int ticksUntilScan(long gameTime) {
        return (int) Math.max(0, nextScan - gameTime);
    }

    public ChrononHandler validate() {
        if (cache == null) {
            return null;
        }
        ChrononHandler handler = cache.getCapability();
        if (handler == null) {
            unlink();
        }
        return handler;
    }

    public void unlink() {
        cache = null;
        interval = FIRST_INTERVAL;
        nextScan = 0;
    }

    public void draw(ServerLevel level, BlockPos center, ChrononBuffer buffer, int rate) {
        ChrononHandler handler = validate();
        if (handler == null) {
            long now = level.getGameTime();
            if (now < nextScan) {
                return;
            }
            handler = scan(level, center);
            if (handler == null) {
                nextScan = now + interval;
                interval = Math.min(interval * 2, MAX_INTERVAL);
                return;
            }
            interval = FIRST_INTERVAL;
        }
        int room = buffer.room();
        if (room <= 0) {
            return;
        }
        int taken = handler.extract(Math.min(room, rate), ActionType.Execute);
        if (taken > 0) {
            buffer.add(taken);
        }
    }

    private ChrononHandler scan(ServerLevel level, BlockPos center) {
        Block target = metronome();
        BlockCapability<ChrononHandler, Direction> capability = ChrononHandler.Capabilities.getBlock();
        int minChunkX = (center.getX() - RADIUS) >> 4;
        int maxChunkX = (center.getX() + RADIUS) >> 4;
        int minChunkZ = (center.getZ() - RADIUS) >> 4;
        int maxChunkZ = (center.getZ() + RADIUS) >> 4;
        BlockPos best = null;
        ChrononHandler bestHandler = null;
        int bestDistance = Integer.MAX_VALUE;
        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(chunkX, chunkZ);
                if (chunk == null) {
                    continue;
                }
                for (BlockEntity candidate : chunk.getBlockEntities().values()) {
                    if (candidate.isRemoved() || !candidate.getBlockState().is(target)) {
                        continue;
                    }
                    BlockPos pos = candidate.getBlockPos();
                    int dx = pos.getX() - center.getX();
                    int dy = pos.getY() - center.getY();
                    int dz = pos.getZ() - center.getZ();
                    if (Math.abs(dx) > RADIUS || Math.abs(dy) > RADIUS || Math.abs(dz) > RADIUS) {
                        continue;
                    }
                    int distance = dx * dx + dy * dy + dz * dz;
                    if (distance >= bestDistance) {
                        continue;
                    }
                    ChrononHandler handler = level.getCapability(capability, pos, candidate.getBlockState(), candidate, Direction.UP);
                    if (handler != null) {
                        best = pos;
                        bestHandler = handler;
                        bestDistance = distance;
                    }
                }
            }
        }
        if (best == null) {
            return null;
        }
        cache = BlockCapabilityCache.create(capability, level, best.immutable(), Direction.UP, valid, () -> {
        });
        return bestHandler;
    }

    private static Block metronome() {
        if (metronome == null) {
            metronome = BuiltInRegistries.BLOCK.get(METRONOME_ID);
        }
        return metronome;
    }
}
