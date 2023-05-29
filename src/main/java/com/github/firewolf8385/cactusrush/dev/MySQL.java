package com.github.firewolf8385.cactusrush.dev;

import net.jadedmc.jadedcore.JadedAPI;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class MySQL {
    private final Plugin plugin;

    public MySQL(Plugin plugin) {
        this.plugin = plugin;
    }
}
