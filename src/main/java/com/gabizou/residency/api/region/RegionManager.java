package com.gabizou.residency.api.region;

import java.util.Optional;
import java.util.UUID;

public interface RegionManager {

    Optional<Region.Identifiable> getRegion(String name);

    Optional<Region.Identifiable> getRegion(UUID id);


}
