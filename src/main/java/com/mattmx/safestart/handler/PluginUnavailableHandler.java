package com.mattmx.safestart.handler;

import com.mattmx.safestart.RequiredPlugin;

@FunctionalInterface
public interface PluginUnavailableHandler {

    void onPluginUnavailable(RequiredPlugin plugin);

}
