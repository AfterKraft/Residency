package com.gabizou.residency.containers;

import org.bukkit.entity.Player;

public class StuckInfo {

    private Player player;
    private int times = 0;
    private Long lastTp = 0L;

    public StuckInfo(Player player) {
        this.player = player;
        this.times++;
        this.lastTp = System.currentTimeMillis();
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getTimesTeleported() {
        return this.times;
    }

    public Long getLastTp() {
        return this.lastTp;
    }

    public void updateLastTp() {
        if (System.currentTimeMillis() - this.lastTp > 1000) {
            this.times = 0;
        }
        addTimeTeleported();
        this.lastTp = System.currentTimeMillis();
    }

    public void addTimeTeleported() {
        this.times++;
    }

}
