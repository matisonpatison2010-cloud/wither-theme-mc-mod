package com.example.withertheme;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class WitherThemeClient implements ClientModInitializer {

    public static final Identifier WITHER_THEME_ID =
            Identifier.of("withertheme", "wither_theme");

    public static SoundEvent WITHER_THEME;
    private static boolean playing = false;
    private static SoundInstance instance;

    @Override
    public void onInitializeClient() {

        WITHER_THEME = Registry.register(
                Registries.SOUND_EVENT,
                WITHER_THEME_ID,
                SoundEvent.of(WITHER_THEME_ID)
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null || client.player == null) return;

            boolean witherNearby = !client.world.getEntitiesByClass(
                    WitherEntity.class,
                    client.player.getBoundingBox().expand(256),
                    entity -> true
            ).isEmpty();

            if (witherNearby && !playing) {
                startMusic(client);
            }

            if (!witherNearby && playing) {
                stopMusic(client);
            }
        });
    }

    private void startMusic(MinecraftClient client) {
        client.getMusicTracker().stop();

        instance = PositionedSoundInstance.music(WITHER_THEME);
        client.getSoundManager().play(instance);
        playing = true;
    }

    private void stopMusic(MinecraftClient client) {
        if (instance != null) {
            client.getSoundManager().stop(instance);
        }
        playing = false;
    }
}
