package com.gabizou.residency.event;

import com.gabizou.residency.protection.ClaimedResidence;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class ResidenceDeleteEvent extends CancellableResidencePlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    DeleteCause cause;

    public ResidenceDeleteEvent(Player player, ClaimedResidence resref, DeleteCause delcause) {
        super("RESIDENCE_DELETE", resref, player);
        this.cause = delcause;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public DeleteCause getCause() {
        return this.cause;
    }

    public enum DeleteCause {
        LEASE_EXPIRE, PLAYER_DELETE, OTHER
    }

}
