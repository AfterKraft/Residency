package com.gabizou.residency.event;

import com.gabizou.residency.protection.ClaimedResidence;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class CancellableResidencePlayerEvent extends ResidencePlayerEvent implements Cancellable {

    protected boolean cancelled;

    public CancellableResidencePlayerEvent(String eventName, ClaimedResidence resref, Player player) {
        super(eventName, resref, player);
        this.cancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean bln) {
        this.cancelled = bln;
    }

}
