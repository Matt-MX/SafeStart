package com.mattmx.safestart.hooks;

import com.mattmx.safestart.handler.PluginUnavailableHandler;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

public interface SafeStartHook {

    public @Nullable Plugin requires();

    public boolean isSafe();


    default @Nullable PluginUnavailableHandler getUnavailableHandler() {
        return null;
    }
}
