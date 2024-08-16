package com.mattmx.safestart.handler;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class HandlerRegistry {
    private final Map<Key, PluginUnavailableHandler> registered;

    public HandlerRegistry() {
        this.registered = new ConcurrentHashMap<>();
    }

    public void register(@NotNull Key key, @NotNull PluginUnavailableHandler handler) {
        this.registered.put(key, handler);
    }

    public @NotNull Optional<PluginUnavailableHandler> getHandler(Key key) {
        return Optional.ofNullable(registered.get(key));
    }

    public Map<Key, PluginUnavailableHandler> getAll() {
        return registered;
    }
}
