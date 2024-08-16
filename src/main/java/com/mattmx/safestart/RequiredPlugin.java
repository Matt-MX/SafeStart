package com.mattmx.safestart;

import net.kyori.adventure.key.Key;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class RequiredPlugin {
    private @NotNull String pluginId;
    private @NotNull Key handlerKey;

    public RequiredPlugin(@NotNull String pluginId, @NotNull Key handlerKey) {
        this.pluginId = pluginId;
        this.handlerKey = handlerKey;
    }

    public @NotNull String getPluginId() {
        return pluginId;
    }

    public @NotNull Key getHandlerKey() {
        return handlerKey;
    }

    public void setHandlerKey(@NotNull Key handlerKey) {
        this.handlerKey = handlerKey;
    }

    public static @NotNull RequiredPlugin ofPlugin(@NotNull JavaPlugin plugin, @NotNull Key handler) {
        return new RequiredPlugin(plugin.getName(), handler);
    }
}
