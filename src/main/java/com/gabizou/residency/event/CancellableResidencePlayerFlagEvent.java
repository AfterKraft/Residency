package com.gabizou.residency.event;

import com.gabizou.residency.protection.ClaimedResidence;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class CancellableResidencePlayerFlagEvent extends ResidencePlayerFlagEvent implements Cancellable {

    protected boolean cancelled;

    public CancellableResidencePlayerFlagEvent(String eventName, ClaimedResidence resref, Player player, String flag, FlagType type, String target) {
        super(eventName, resref, player, flag, type, target);
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
