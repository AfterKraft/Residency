package com.gabizou.residency.event;

import com.gabizou.residency.api.event.ResidenceEvent;
import com.gabizou.residency.protection.ClaimedResidence;
import org.bukkit.event.Cancellable;
import org.spongepowered.api.event.Cancellable;

public class CancellableResidenceEvent extends ResidenceEvent implements Cancellable {

    protected boolean cancelled;

    public CancellableResidenceEvent(String eventName, ClaimedResidence resref) {
        super(eventName, resref);
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
