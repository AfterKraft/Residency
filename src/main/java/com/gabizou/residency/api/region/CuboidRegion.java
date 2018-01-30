package com.gabizou.residency.api.region;

import com.flowpowered.math.vector.Vector2i;

import java.util.Collection;

public interface CuboidRegion extends Region.Expandable {

    Collection<Vector2i> getPoints();

}
