# 🔐 SafeStart

A PaperMC plugin to ensure any high risk plugins are installed before allowing player connection.

### ❓ Installation

- Grab the **latest release** from the #releases page
- Install it in your `plugins` folder
- **Restart** or **reload** the server.

You can add requirements with the following commands or via the config.

### 📘 Commands usage

- `/safestart`
  - `set <PluginName> <Handler>`
    > Set a requirement for a plugin defined by it's `PluginName`.
    > The `Handler` is how we handle the plugin if it is **not** available.
    > There are two build-in handlers: `safestart:shutdown` and `safestart:prevent_join`.
  - `del <PluginName>`
    > Remove a requirement for a given plugin.
  - `allowjoin`
    > You can use this to bypass the effects of `safestart:prevent_join`.
    > Be careful using this since anyone will be able to join unless whitelist is on.
  - `reload`
    > Reloads the config file.
  - `list` and `checks`
    > Get a list of what plugins are not available.
    > Run this with the `--runHandlers` flag to execute all requirement handlers (disabled by default).
  - `debug`
    > A list of all required servers and their respective handlers. (This will probably be changed to `list`)

### ❔ Further Configuration

The plugin comes built-in with a **discord webhook** feature, which is disabled by default.

To enable, navigate to `./plugins/SafeStart/config.yml`, set `discord.enabled` to `true`, and paste in your webhook URL.

`default`
```yml
discord:
  enabled: false
  url: "WEBHOOK-URL-HERE"
  # Useful for pinging roles/members for outages
  # This placeholder pings my discord account.
  contents: "<@312693889582759938>"
  # To quickly help identify the server that the outage is on.
  title: ":warning: SafeSpec (MyServerName)"
```

### ⌨️ Developer Usage

This is not on Maven so you will have to depend on the jar file from the #releases page.

Get the instance of the plugin with
```java
SafeStart.getInstance()
```

When making your own handlers, make sure it is done before the plugin runs the checks.

This is usually done as soon as the worlds are ticking since that is when players can join the server.

However it can also be done via the `/safestart checks` command.
```java
Key customKey = Key.key("pluginName", "customHandler")
PluginUnavailableHandler customHandler = (unavailablePlugin) -> {
    // TODO handle the unavailable plugin
};

safestart.getHandlers().register(customKey, customHandler);
```
