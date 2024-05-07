package net.jadedmc.cactusrush.game;

import net.jadedmc.cactusrush.CactusRushPlugin;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class GameNew {
    private final CactusRushPlugin plugin;
    private final UUID uuid;

    public GameNew(@NotNull final CactusRushPlugin plugin, @NotNull final Document document) {
        this.plugin = plugin;
        this.uuid = UUID.fromString(document.getString("uuid"));
    }


    @NotNull
    public final Document toDocument() {
        final Document document = new Document();

        return document;
    }
}