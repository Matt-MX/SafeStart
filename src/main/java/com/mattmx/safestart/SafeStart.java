package com.mattmx.safestart;

import com.mattmx.safestart.command.SafeStartCommand;
import com.mattmx.safestart.discord.SafeStartWebhookFeature;
import com.mattmx.safestart.event.PluginsUnavailableEvent;
import com.mattmx.safestart.handler.HandlerRegistry;
import com.mattmx.safestart.handler.PluginUnavailableHandler;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SafeStart extends JavaPlugin {
    private static SafeStart instance;
    private @Nullable PluginUnavailableHandler fallback;
    private @Nullable Key fallbackKey;
    private ArrayList<RequiredPlugin> required;
    private final HandlerRegistry handlers = new HandlerRegistry();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        ConfigurationSerialization.registerClass(RequiredPlugin.class, "plugin");

        String fallbackKeyString = getConfig().getString("fallback-handle");
        fallbackKey = Key.key(fallbackKeyString);
        Optional<PluginUnavailableHandler> fallbackHandler = handlers.getHandler(fallbackKey);

        if (fallbackHandler.isEmpty()) {
            getLogger().warning(String.format("The fallback handle is invalid '%s'", fallbackKeyString));
        }

        this.fallback = fallbackHandler.orElse(null);

        loadRequiredPlugins();

        Bukkit.getPluginManager().registerEvents(new SafeStartWebhookFeature(this), this);
        Objects.requireNonNull(Bukkit.getPluginCommand("safestart")).setExecutor(new SafeStartCommand(this));

        // Schedule a task for the first server tick.
        Bukkit.getScheduler()
            .runTask(this, () -> {
                List<RequiredPlugin> unavailable = performChecks(true);

                if (unavailable.isEmpty()) {
                    MessageHelper.sendSuccess(Bukkit.getConsoleSender(), "All plugins are available.");
                } else {
                    MessageHelper.sendError(Bukkit.getConsoleSender(), String.format("%d Plugins unavailable", unavailable.size()));

                    for (RequiredPlugin plugin : unavailable) {
                        MessageHelper.sendError(Bukkit.getConsoleSender(), String.format(" - %s (%s)", plugin.getPluginId(), plugin.getHandlerKey()));
                    }
                }
            });
    }

    public void loadRequiredPlugins() {
        required = new ArrayList<>(
            Objects.requireNonNull(getConfig().getList("plugins", Collections.emptyList()), "Missing plugins list in config.yml")
                .stream()
                .filter(RequiredPlugin.class::isInstance)
                .map(o -> (RequiredPlugin) o)
                .toList()
        );
    }

    public @NotNull List<RequiredPlugin> performChecks(boolean runHandlers) {
        ArrayList<RequiredPlugin> invalid = new ArrayList<>();
        for (RequiredPlugin plugin : this.required) {
            @Nullable Plugin pluginInstance = Bukkit.getPluginManager().getPlugin(plugin.getPluginId());

            if (pluginInstance == null || !pluginInstance.isEnabled()) {
                Optional<PluginUnavailableHandler> handler = handlers.getHandler(plugin.getHandlerKey());

                if (runHandlers) {
                    handler.ifPresentOrElse(
                        (finalHandler) -> finalHandler.onPluginUnavailable(plugin),
                        () -> fallbackUnknownHandler(plugin)
                    );
                }

                invalid.add(plugin);
            }
        }

        if (!invalid.isEmpty()) {
            Bukkit.getPluginManager().callEvent(new PluginsUnavailableEvent(invalid.stream().toList()));
        }

        return invalid;
    }

    public List<RequiredPlugin> getRequired() {
        return required;
    }

    private void fallbackUnknownHandler(@NotNull RequiredPlugin plugin) {
        if (fallback != null) {
            fallback.onPluginUnavailable(plugin);
        }
    }

    public HandlerRegistry getHandlers() {
        return handlers;
    }

    public void writeConfigChanges() {
        getConfig().set("plugins", required);
        saveConfig();
    }

    public static SafeStart getInstance() {
        return instance;
    }

    public Key getFallbackKey() {
        return fallbackKey;
    }
}
