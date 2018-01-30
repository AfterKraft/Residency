package com.gabizou.residency.api.region.protection;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public final class Flags {

    // sortfields:ON

    public static final Flag<Boolean> NOTIFY_ENTER = DummyObjectProvider.createExtendedFor(Flag.class, "NOTIFY_ENTER");
    public static final Flag<Boolean> NOTIFY_LEAVE = DummyObjectProvider.createExtendedFor(Flag.class, "NOTIFY_LEAVE");
    public static final Flag<Map<ItemType, Tristate>> ALLOWED_ITEMS = DummyObjectProvider.createExtendedFor(Flag.class, "ALLOWED_ITEMS");
    public static final Flag<Double> PER_BLOCK_SELL_PRICE = DummyObjectProvider.createExtendedFor(Flag.class, "PER_BLOCK_SELL_PRICE");
    public static final Flag<Vector3d> TELEPORT_LOCATION = DummyObjectProvider.createExtendedFor(Flag.class, "TELEPORT_LOCATION");
    public static final Flag<Text> ENTER_MESSAGE = DummyObjectProvider.createExtendedFor(Flag.class, "ENTER_MESSAGE");
    public static final Flag<Text> LEAVE_MESSAGE = DummyObjectProvider.createExtendedFor(Flag.class, "LEAVE_MESSAGE");
    public static final Flag<Text> SHOP_DESCRIPTION = DummyObjectProvider.createExtendedFor(Flag.class, "SHOP_DESCRIPTION");
    public static final Flag<Text> CHANNEL_PREFIX = DummyObjectProvider.createExtendedFor(Flag.class, "CHAT_PREFIX");
    public static final Flag<List<ItemType>> ITEM_BLACKLIST = DummyObjectProvider.createExtendedFor(Flag.class, "ITEM_BLACKLIST");
    public static final Flag<List<ItemType>> ITEM_WHITELIST = DummyObjectProvider.createExtendedFor(Flag.class, "ITEM_WHITELIST");
    public static final Flag<List<String>> COMMAND_WHITELIST = DummyObjectProvider.createExtendedFor(Flag.class, "COMMAND_WHITELIST");
    public static final Flag<List<String>> COMMAND_BLACKLIST = DummyObjectProvider.createExtendedFor(Flag.class, "COMMAND_BLACKLIST");
    public static final Flag<TextColor> CHANNEL_COLOR = DummyObjectProvider.createExtendedFor(Flag.class, "CHANNEL_COLOR");
    // sortfields:OFF

    private Flags() {
        throw new UnsupportedOperationException("should not instantiate this class");
    }

}
