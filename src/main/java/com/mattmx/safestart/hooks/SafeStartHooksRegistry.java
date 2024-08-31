package com.mattmx.safestart.hooks;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SafeStartHooksRegistry {
    private final Set<SafeStartHook> hooks = new HashSet<>();

    public void register(@NotNull SafeStartHook hook) {
        this.hooks.add(hook);
    }

    public <T extends SafeStartHook> @NotNull Optional<SafeStartHook> get(Class<T> clazz) {
        return hooks.stream()
            .filter(clazz::isInstance)
            .findFirst();
    }

    public @NotNull Set<SafeStartHook> getHooks() {
        return hooks;
    }

}
