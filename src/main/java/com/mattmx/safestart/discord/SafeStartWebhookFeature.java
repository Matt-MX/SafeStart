package com.mattmx.safestart.discord;

import com.mattmx.safestart.SafeStart;
import com.mattmx.safestart.event.PluginsUnavailableEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

public class SafeStartWebhookFeature implements Listener {
    private final SafeStart plugin;

    public SafeStartWebhookFeature(@NotNull SafeStart plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginsUnavailable(PluginsUnavailableEvent event) {
        if (!plugin.getConfig().getBoolean("discord.enabled")) return;

        String url = plugin.getConfig().getString("discord.url");
        Objects.requireNonNull(url, "Discord embed URL cannot be null! Please disable discord.embed feature.");

        String contents = plugin.getConfig().getString("discord.contents", null);
        String title = plugin.getConfig().getString("discord.title", "SafeStart");

        Bukkit.getAsyncScheduler().runNow(plugin, (task) -> {
            DiscordWebhook discord = new DiscordWebhook(url);

            discord.setContent(contents);

            discord.addEmbed(new DiscordWebhook.EmbedObject()
                .setColor(Color.RED)
                .setTitle(title)
                .setDescription(
                    "**The following plugins failed to load**\\n\\n" +
                        event.getPlugins()
                            .stream()
                            .map(plugin -> "> :x: " + plugin.getPluginId())
                            .collect(Collectors.joining("\\n"))
                )
            );

            try {
                discord.execute();
            } catch (IOException e) {
                plugin.getLogger().warning(String.format("Failed to send discord URL (%s)", e.getMessage()));
                e.printStackTrace();
            }
        });
    }
}
