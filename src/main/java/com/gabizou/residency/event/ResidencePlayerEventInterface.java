package com.gabizou.residency.event;

import org.bukkit.entity.Player;

public interface ResidencePlayerEventInterface {

    public boolean isAdmin();

    public boolean isPlayer();

    public Player getPlayer();
}
