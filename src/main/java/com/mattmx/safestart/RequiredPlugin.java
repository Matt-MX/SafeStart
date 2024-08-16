package com.mattmx.safestart;

import net.kyori.adventure.key.Key;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RequiredPlugin implements ConfigurationSerializable {
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

    @Override
    public String toString() {
        return getPluginId();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", this.getPluginId());
        data.put("handle", this.getHandlerKey().toString());

        return data;
    }

    public static RequiredPlugin deserialize(Map<String, Object> args) {
        String name = args.get("name").toString();
        Objects.requireNonNull(name, "Missing name entry for a plugin");

        String keyString = args.get("handle").toString();
        Objects.requireNonNull(keyString, String.format("Missing a handle key for plugin %s", name));

        Key key;
        if (!Key.parseable(keyString)) {
            SafeStart.getInstance().getLogger().warning(String.format("Can't parse key %s for plugin %s", keyString, name));
            key = SafeStart.getInstance().getFallbackKey();
        } else {
            key = Key.key(keyString);
        }

        Objects.requireNonNull(key, String.format("Handle key was null for %s. This might be because the fallback handle is also invalid.", name));

        return new RequiredPlugin(name, key);
    }
}
