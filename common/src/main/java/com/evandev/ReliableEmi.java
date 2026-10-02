package com.evandev;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

public class ReliableEmi {
    public static final String MOD_ID = "remi";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static Identifier res(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static MutableComponent text(String type, String path) {
        return Component.translatable(type + "." + MOD_ID + "." + path);
    }

    public static MutableComponent text(String type, String path, Object... args) {
        return Component.translatable(type + "." + MOD_ID + "." + path, args);
    }

    public static MutableComponent text(String path) {
        return Component.translatable(MOD_ID + "." + path);
    }
}
