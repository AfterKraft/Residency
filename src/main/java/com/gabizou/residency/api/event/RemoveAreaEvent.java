package com.gabizou.residency.api.event;

import com.gabizou.residency.protection.CuboidArea;
import org.spongepowered.api.event.Cancellable;

public interface RemoveAreaEvent extends ResidenceEvent, Cancellable {

    String getResidenceName();

    CuboidArea getPhysicalArea();

    RemoveType getType();

    enum RemoveType {
        LEASE_EXPIRE,
        PLAYER_DELETE,
        OTHER
    }

}
