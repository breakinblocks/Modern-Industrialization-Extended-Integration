package com.breakinblocks.miei.stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

public final class PlayerStages {
    private static final Map<UUID, Set<String>> OFFLINE = new ConcurrentHashMap<>();

    private PlayerStages() {}

    public static void register(IEventBus gameBus) {
        gameBus.addListener(PlayerStages::onLoggedOut);
        gameBus.addListener(PlayerStages::onServerStopped);
    }

    public static boolean has(ServerLevel level, UUID owner, String stage) {
        if (owner == null) {
            return false;
        }
        MinecraftServer server = level.getServer();
        ServerPlayer player = server.getPlayerList().getPlayer(owner);
        if (player != null) {
            return player.getTags().contains(stage);
        }
        return OFFLINE.computeIfAbsent(owner, id -> load(server, id)).contains(stage);
    }

    private static Set<String> load(MinecraftServer server, UUID id) {
        Path file = server.getWorldPath(LevelResource.PLAYER_DATA_DIR).resolve(id + ".dat");
        if (!Files.isRegularFile(file)) {
            return Set.of();
        }
        try {
            CompoundTag data = NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap());
            ListTag tags = data.getList("Tags", Tag.TAG_STRING);
            Set<String> stages = new HashSet<>();
            for (int i = 0; i < tags.size(); i++) {
                stages.add(tags.getString(i));
            }
            return Set.copyOf(stages);
        } catch (IOException e) {
            return Set.of();
        }
    }

    private static void onLoggedOut(PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            OFFLINE.put(player.getUUID(), Set.copyOf(player.getTags()));
        }
    }

    private static void onServerStopped(ServerStoppedEvent event) {
        OFFLINE.clear();
    }
}
