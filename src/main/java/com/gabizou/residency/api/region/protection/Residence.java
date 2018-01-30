package com.gabizou.residency.api.region.protection;

import org.spongepowered.api.service.economy.account.VirtualAccount;

import java.time.Instant;
import java.time.LocalDateTime;

public interface Residence<R extends Residence<R>> extends Protected<R> {

    LocalDateTime getCreationDate();

    R setCreationDate(LocalDateTime time);

    VirtualAccount getAccount();


    interface Cuboid extends Residence<Cuboid>, Protected.Cuboid<Cuboid> {

    }

    interface Polygonal extends Residence<Polygonal>, Protected.Polygonal<Polygonal> {

    }

}
