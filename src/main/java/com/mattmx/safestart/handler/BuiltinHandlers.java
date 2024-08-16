package com.mattmx.safestart.handler;

import com.mattmx.safestart.MessageHelper;
import com.mattmx.safestart.SafeStart;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
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

        Component kickMessage = Component.text(
            "SafeStart prevented you from logging in.\n\nIf you believe this is an error please contact an\nadministrator for this server."
        ).color(NamedTextColor.RED);

        Bukkit.getOnlinePlayers()
            .stream().filter((player -> !player.hasPermission("safestart.handler.prevent-join-bypass")))
            .forEach((player -> player.kick(kickMessage)));

        PreventJoinListener.register(SafeStart.getInstance(), kickMessage);
    };

    public static class PreventJoinListener implements Listener {
        private static PreventJoinListener instance = null;

        private final Component kickMessage;

        public static void register(@NotNull SafeStart plugin, @NotNull Component kickMessage) {
            if (instance != null) {
                return;
            }
            instance = new PreventJoinListener(kickMessage);
            Bukkit.getPluginManager().registerEvents(instance, plugin);
        }

        public static void unregister() {
            if (instance == null) {
                return;
            }

            HandlerList.unregisterAll(instance);
            instance = null;
        }

        public PreventJoinListener(@NotNull Component kickMessage) {
            this.kickMessage = kickMessage;
        }

        @EventHandler(ignoreCancelled = true)
        public void onPrePlayerLogin(AsyncPlayerPreLoginEvent event) {

            boolean isOperator = Bukkit.getOperators()
                .stream()
                .anyMatch((offlinePlayer) ->
                    offlinePlayer.getUniqueId()
                        .toString()
                        .equals(event.getUniqueId().toString())
                );

            if (isOperator) {
                return;
            }

            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.kickMessage(kickMessage);
        }

        @EventHandler
        public void onJoin(PlayerJoinEvent event) {
            if (event.getPlayer().hasPermission("safestart.handler.prevent-join-bypass")) {
                MessageHelper.sendError(event.getPlayer(), "SafeStart join prevention is active but you bypassed it.");
            }
        }
    }

}
