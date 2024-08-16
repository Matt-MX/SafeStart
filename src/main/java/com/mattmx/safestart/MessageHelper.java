package com.mattmx.safestart;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class MessageHelper {

    public static void sendError(Audience audience, String message) {
        audience.sendMessage(Component.text(message).color(NamedTextColor.RED));
    }

    public static void sendSuccess(Audience audience, String message) {
        audience.sendMessage(Component.text(message).color(NamedTextColor.GREEN));
    }

    public static void sendInfo(Audience audience, String message) {
        audience.sendMessage(Component.text(message).color(NamedTextColor.WHITE));
    }

}
