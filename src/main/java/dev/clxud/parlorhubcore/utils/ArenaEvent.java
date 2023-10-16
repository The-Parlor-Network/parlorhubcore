package dev.clxud.parlorhubcore.utils;

import org.bukkit.Bukkit;

import java.time.Duration;

public class ArenaEvent {

    private ArenaEventEnum eventType;

    private Duration duration = Duration.ZERO;

    private final ArenaInitEvent initEvent = new ArenaInitEvent(this);
    private final ArenaEndEvent endEvent = new ArenaEndEvent(this);

    private Boolean isStarting = false;



    public ArenaEventEnum getEventType() {
        return this.eventType;
    }

    public Duration getDuration() {
        return this.duration;
    }


    public String getFormattedDuration() {
        if (this.duration.toHours() > 0) {
            return String.format("%02d:%02d:%02d", this.duration.toHours(), this.duration.toMinutesPart(), this.duration.toSecondsPart());
        } else {
            return String.format("%02d:%02d", this.duration.toMinutesPart(), this.duration.toSecondsPart());
        }
    }

    public Boolean isRunning() {
        return this.duration.toSeconds() > 0;
    }

    public Boolean isStarting() {
        return this.isStarting;
    }

    public void setStarting(Boolean isStarting) {
        this.isStarting = isStarting;
    }

    public void start(ArenaEventEnum event, int minutes) {
        if (this.isRunning()) {
            return;
        }
        this.eventType = event;
        this.duration = Duration.ofMinutes(minutes);
        this.isStarting = false;
        Bukkit.getPluginManager().callEvent(this.initEvent);

        Bukkit.getScheduler().runTaskTimer(Bukkit.getPluginManager().getPlugin("ParlorHubCore"), task -> {
            if (this.duration.toSeconds() < 1) {
                Bukkit.getPluginManager().callEvent(this.endEvent);
                task.cancel();
            }
            this.duration = this.duration.minusSeconds(1);
        }, 0, 20L);

    }

    public void stop() {
        if (!this.isRunning()) {
            return;
        }
        this.duration = Duration.ZERO;
    }


}
