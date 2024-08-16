package com.mattmx.safestart.handler;

import com.mattmx.safestart.SafeStart;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.jetbrains.annotations.NotNull;

public class BuiltinHandlers {

    public static final Key SHUTDOWN_KEY = Key.key("safestart", "shutdown");
    public static final PluginUnavailableHandler SHUTDOWN_HANDLE = (plugin) -> {
        SafeStart.getInstance()
            .getLogger()
            .warning(String.format("Required plugin was not available! Ensure %s is installed before continuing", plugin));

        Bukkit.getServer().shutdown();
    };

    public static final Key PREVENT_JOIN_KEY = Key.key("safestart", "prevent_join");
    public static final PluginUnavailableHandler PREVENT_JOIN_HANDLE = (plugin) -> {
        SafeStart.getInstance()
            .getLogger()
            .warning(String.format("Required plugin was not available! Ensure %s is installed before running /safestart allowjoin.", plugin));

        PreventJoinListener.register(SafeStart.getInstance());

        Bukkit.getServer().shutdown();
    };

    public static class PreventJoinListener implements Listener {
        private static PreventJoinListener instance = null;

        public static void register(@NotNull SafeStart plugin) {
            if (instance != null) {
                return;
            }
            instance = new PreventJoinListener();
            Bukkit.getPluginManager().registerEvents(instance, plugin);
        }

        public static void unregister(@NotNull SafeStart plugin) {
            if (instance == null) {
                return;
            }

            HandlerList.unregisterAll(instance);
            instance = null;
        }

        @EventHandler(ignoreCancelled = true)
        public void onJoin(AsyncPlayerPreLoginEvent event) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.kickMessage(Component.text(
                "SafeStart prevented you from logging in.\n\nIf you believe this is an error please contact an\nadministrator for this server."
            ).color(NamedTextColor.RED));
        }
    }

}
