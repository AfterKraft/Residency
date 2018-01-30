package com.gabizou.residency.api.event;

import com.gabizou.residency.protection.CuboidArea;
import org.spongepowered.api.event.Cancellable;

public interface AddAreaEvent extends ResidenceEvent, Cancellable {

    String getResidenceName();

    CuboidArea getPhysicalArea();

}
