package com.mattmx.safestart;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.jetbrains.annotations.NotNull;

public class PluginDisableListener implements Listener {

    private final @NotNull SafeStart plugin;

    public PluginDisableListener(@NotNull SafeStart plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        // We only care if they disable during runtime.
        if (!Bukkit.isTickingWorlds()) return;

        plugin.performChecks(true);
    }

}
