package com.mattmx.safestart.event;

import com.mattmx.safestart.RequiredPlugin;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PluginsUnavailableEvent extends Event {
    private final static HandlerList handlers = new HandlerList();

    private final List<RequiredPlugin> plugins;

    public PluginsUnavailableEvent(@NotNull List<RequiredPlugin> plugins) {
        super(false);

        this.plugins = plugins;
    }

    public @NotNull List<RequiredPlugin> getPlugins() {
        return this.plugins;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
