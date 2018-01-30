package com.gabizou.residency.api.event;

import com.gabizou.residency.protection.ClaimedResidence;
import org.spongepowered.api.event.Event;

public interface ResidenceEvent extends Event {

    String getMessage();

    ClaimedResidence getResidence();

    interface Player extends ResidenceEvent {

        boolean isAdmin();

        Player getPlayer();

    }
}
