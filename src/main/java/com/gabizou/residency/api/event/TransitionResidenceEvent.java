package com.gabizou.residency.api.event;

import com.gabizou.residency.protection.ClaimedResidence;

import java.util.Optional;

public interface TransitionResidenceEvent extends ResidenceEvent.Player {

    Optional<ClaimedResidence> getFrom();

    Optional<ClaimedResidence> getTo();

}
