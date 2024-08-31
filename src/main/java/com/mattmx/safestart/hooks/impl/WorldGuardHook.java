package com.mattmx.safestart.hooks.impl;

import com.mattmx.safestart.hooks.SafeStartHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class WorldGuardHook implements SafeStartHook {

    @Override
    public Plugin requires() {
        return Bukkit.getPluginManager().getPlugin("WorldGuard");
    }

    @Override
    public boolean isSafe() {
        return Objects.requireNonNull(requires()).isEnabled();
    }
}
