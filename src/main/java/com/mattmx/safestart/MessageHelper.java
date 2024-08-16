package com.mattmx.safestart;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class MessageHelper {
    public final static TextColor PRIMARY = TextColor.fromCSSHexString("#59bdff");
    public final static TextColor SECONDARY = TextColor.fromCSSHexString("#6696e3");
    public final static TextColor SUCCESS = TextColor.fromCSSHexString("#81e366");
    public final static TextColor ERROR = TextColor.fromCSSHexString("#e36666");


    public static void sendError(Audience audience, String message) {
        audience.sendMessage(Component.text(message).color(ERROR));
    }

    public static void sendSuccess(Audience audience, String message) {
        audience.sendMessage(Component.text(message).color(SUCCESS));
    }

    public static void sendInfo(Audience audience, String message) {
        audience.sendMessage(Component.text(message).color(SECONDARY));
    }

}
