package com.gabizou.residency.api.chat;

import com.gabizou.residency.api.region.protection.Domain;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextFormat;

public interface DomainChannel extends MessageChannel, DataSerializable {

    Domain<?> getOwningDomain();

    Text getPrefix();

    TextFormat getChannelFormat();

}
