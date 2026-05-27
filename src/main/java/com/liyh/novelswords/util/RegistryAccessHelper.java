package com.liyh.AncientWarcraft.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Optional;

public final class RegistryAccessHelper {
    private RegistryAccessHelper() {}

    public static Optional<RegistryAccess> resolve() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            RegistryAccess access = server.registryAccess();
            if (hasEnchantmentRegistry(access)) {
                return Optional.of(access);
            }
        }

        if (FMLEnvironment.dist == Dist.CLIENT) {
            return resolveClient();
        }

        return resolveBuiltinFallback();
    }

    private static Optional<RegistryAccess> resolveClient() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            RegistryAccess access = minecraft.level.registryAccess();
            if (hasEnchantmentRegistry(access)) {
                return Optional.of(access);
            }
        }
        if (minecraft.getConnection() != null) {
            RegistryAccess access = minecraft.getConnection().registryAccess();
            if (hasEnchantmentRegistry(access)) {
                return Optional.of(access);
            }
        }
        return Optional.empty();
    }

    private static Optional<RegistryAccess> resolveBuiltinFallback() {
        RegistryAccess builtin = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        if (hasEnchantmentRegistry(builtin)) {
            return Optional.of(builtin);
        }
        return Optional.empty();
    }

    public static boolean hasEnchantmentRegistry(RegistryAccess access) {
        return access.registry(Registries.ENCHANTMENT).isPresent();
    }
}
