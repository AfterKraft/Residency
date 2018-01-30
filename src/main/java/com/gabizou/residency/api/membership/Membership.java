package com.gabizou.residency.api.membership;

import org.spongepowered.api.entity.living.player.User;

import java.util.UUID;

public interface Membership {

    boolean contains(User user);

    boolean contains(UUID uuid);

    boolean contains(String playerName);

    int size();

    Membership clear();

    Membership add(User user);

    Membership add(UUID userID);

    Membership add(String playerName);

    Membership remove(User user);

    Membership remove(UUID userID);

    Membership remove(String playerName);

    Membership addAll(Membership membership);

    Membership removeAll(Membership membership);

}
