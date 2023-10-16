package dev.clxud.parlorhubcore.utils;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ArenaInitEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final ArenaEvent event;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }


    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public ArenaInitEvent(ArenaEvent event) {
        this.event = event;
    }

    public ArenaEvent getEvent() {
        return this.event;
    }


}
