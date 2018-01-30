package com.gabizou.residency.event;

import com.gabizou.residency.protection.ClaimedResidence;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class ResidenceRentEvent extends CancellableResidencePlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    RentEventType eventtype;

    public ResidenceRentEvent(ClaimedResidence resref, Player player, RentEventType type) {
        super("RESIDENCE_RENT_EVENT", resref, player);
        this.eventtype = type;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public RentEventType getCause() {
        return this.eventtype;
    }

    public enum RentEventType {
        RENT, UNRENT, RENTABLE, UNRENTABLE, RENT_EXPIRE
    }

}
