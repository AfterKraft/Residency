package com.gabizou.residency.api.region.protection;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.VirtualAccount;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

public interface Residence<R extends Residence<R>> extends Domain<R> { ;

    LocalDateTime getCreationDate();

    R setCreationDate(LocalDateTime time);

    /**
     * Gets the requirements to purchase this {@link Residence}. Since the
     * economy API does not dictate having a singular currency, we must allow
     * for multiple currencies to be available, and therefor, accept the purchase
     * price being in multiple representations such that it qualifies the minimum
     * price.
     *
     * @return The price to purchase this residence
     */
    Map<Currency, BigDecimal> getPurchasePrice();

    R setPurchasePrice(Map<Currency, BigDecimal> price);

    VirtualAccount getAccount();

    interface Builder<R extends Residence<R>, B extends Builder<R, B>> extends Protected.Builder<R, B> {

        B setCreationDate(LocalDateTime time);

        B setCreationDate(Instant time);

        B setAccount(VirtualAccount account);

    }

    interface Cuboid extends Residence<Cuboid>, Domain.Cuboid<Cuboid> {

        static Builder builder() {
            return Sponge.getRegistry().createBuilder(Builder.class);
        }

        interface Builder extends Residence.Builder<Residence.Cuboid, Builder>, Domain.Cuboid.Builder<Residence.Cuboid, Builder> {

        }
    }

    interface Polygonal extends Residence<Polygonal>, Domain.Polygonal<Polygonal> {

        static Builder builder() {
            return Sponge.getRegistry().createBuilder(Builder.class);
        }

        interface Builder extends Residence.Builder<Residence.Polygonal, Builder>, Domain.Polygonal.Builder<Residence.Polygonal, Builder> {

        }
    }

}
