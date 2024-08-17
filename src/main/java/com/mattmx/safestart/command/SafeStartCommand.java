package com.mattmx.safestart.command;

import com.mattmx.safestart.MessageHelper;
import com.mattmx.safestart.RequiredPlugin;
import com.mattmx.safestart.SafeStart;
import com.mattmx.safestart.handler.BuiltinHandlers;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SafeStartCommand implements CommandExecutor, TabCompleter {
    private final SafeStart plugin;

    public SafeStartCommand(SafeStart plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {

            MessageHelper.sendInfo(sender, String.format("You are running SafeStart v%s", plugin.getPluginMeta().getVersion()));

            return true;
        }

        switch (args[0].toLowerCase()) {
            case "set" -> {
                if (args.length < 3) {
                    MessageHelper.sendError(sender, "Provide a plugin name and handler key.");
                    return false;
                }

                String pluginId = args[1];
                String handlerKey = args[2];

                if (!Key.parseable(handlerKey)) {
                    MessageHelper.sendError(sender, "That key is not valid.");
                    return false;
                }

                Key key = Key.key(handlerKey);

                if (plugin.getHandlers().getHandler(key).isEmpty()) {
                    MessageHelper.sendError(sender, "There are no registered handlers matching that key.");
                    return false;
                }

                boolean existing = plugin.getRequired()
                    .removeIf((r) -> r.getPluginId().equals(pluginId));

                plugin.getRequired().add(new RequiredPlugin(pluginId, key));

                plugin.writeConfigChanges();

                String prefix = existing ? "Replaced" : "Added";
                MessageHelper.sendSuccess(sender, String.format("%s %s as a required plugin with handler %s.", prefix, pluginId, handlerKey));
            }
            case "del" -> {
                if (args.length < 2) {
                    MessageHelper.sendError(sender, "Provide a plugin name.");
                    return false;
                }

                String pluginId = args[1];

                boolean existing = plugin.getRequired()
                    .removeIf((r) -> r.getPluginId().equals(pluginId));

                if (!existing) {
                    MessageHelper.sendError(sender, "That plugin isn't registered as a required plugin.");
                    return false;
                }

                plugin.writeConfigChanges();

                MessageHelper.sendSuccess(sender, String.format("Deleted %s as a required plugin.", pluginId));
            }
            case "reload" -> {
                try {
                    plugin.reloadConfig();
                    plugin.loadRequiredPlugins();
                    MessageHelper.sendSuccess(sender, "Reloaded configuration successfully!");
                } catch (Exception error) {
                    MessageHelper.sendError(sender, "There was an error reloading the config! Check console for further details.");
                    error.printStackTrace();
                }
            }
            case "allowjoin" -> {
                BuiltinHandlers.PreventJoinListener.unregister();
                MessageHelper.sendSuccess(sender, "Join prevention is now disabled.");
            }
            case "test" -> {
                boolean runWithHandlersCallback = Arrays.asList(args).contains("--runHandlers");
                List<RequiredPlugin> unavailable = plugin.performChecks(runWithHandlersCallback);

                if (unavailable.isEmpty()) {
                    MessageHelper.sendSuccess(sender, "All required plugins are installed!");
                    return true;
                } else {
                    MessageHelper.sendError(sender, String.format("%d Plugins unavailable", unavailable.size()));

                    for (RequiredPlugin plugin : unavailable) {
                        MessageHelper.sendError(sender, String.format(" - %s (%s)", plugin.getPluginId(), plugin.getHandlerKey()));
                    }

                    if (!runWithHandlersCallback) {
                        MessageHelper.sendInfo(sender, "Run with --runHandlers to execute handler callbacks.");
                    }
                }
            }
            case "debug" -> {
                MessageHelper.sendInfo(sender, "All required plugins");
                for (RequiredPlugin plugin : plugin.getRequired()) {
                    MessageHelper.sendInfo(sender, String.format(" - %s (%s)", plugin.getPluginId(), plugin.getHandlerKey()));
                }
            }
            default -> {
                MessageHelper.sendError(sender, "Unrecognized sub-command.");
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        String lastArg = args.length == 0 ? "" : args[args.length - 1];

        switch (args.length) {
            case 0, 1 -> {
                return tabCompleteHelper(lastArg, "list", "test", "set", "del", "reload", "allowjoin");
            }
            case 2 -> {
                switch (args[0].toLowerCase()) {
                    case "set" -> {
                        return tabCompleteHelper(lastArg, Arrays.stream(
                                Bukkit.getPluginManager().getPlugins()
                            ).map(Plugin::getName)
                            .toArray(String[]::new));
                    }
                    case "del" -> {
                        return tabCompleteHelper(lastArg, plugin.getRequired()
                            .stream()
                            .map(RequiredPlugin::getPluginId)
                            .toArray(String[]::new));
                    }
                    case "test" -> {
                        return tabCompleteHelper(lastArg, "--runHandlers");
                    }
                }
            }
            case 3 -> {
                if (args[0].equalsIgnoreCase("set")) {
                    return tabCompleteHelper(lastArg, plugin.getHandlers()
                        .getAll()
                        .keySet()
                        .stream()
                        .map(Key::toString)
                        .toArray(String[]::new)
                    );
                }
            }
        }

        return null;
    }

    private List<String> tabCompleteHelper(String lastArgument, String... options) {
        return Stream.of(options)
            .filter(a -> a.toLowerCase().startsWith(lastArgument.toLowerCase()))
            .toList();
    }
}
