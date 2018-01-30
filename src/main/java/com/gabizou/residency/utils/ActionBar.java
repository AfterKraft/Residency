package com.gabizou.residency.utils;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.ABInterface;
import com.gabizou.residency.utils.VersionChecker.Version;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

public class ActionBar implements ABInterface {

    private Version version = Version.v1_11_R1;
    private Object packet;
    private Method getHandle;
    private Method sendPacket;
    private Field playerConnection;
    private Class<?> nmsChatSerializer;
    private Class<?> nmsIChatBaseComponent;
    private Class<?> packetType;
    private boolean simpleMessages = false;
    private boolean simpleTitleMessages = false;

    private Constructor<?> nmsPacketPlayOutTitle;
    private Class<?> enumTitleAction;
    private Method fromString;
    private Residence plugin;

    private Class<?> ChatMessageclz;
    private Class<?> sub;
    private Object[] consts;

    public ActionBar(Residence plugin) {
        this.plugin = plugin;
        this.version = this.plugin.getVersionChecker().getVersion();
        try {
            this.packetType = Class.forName(getPacketPlayOutChat());
            Class<?> typeCraftPlayer = Class.forName(getCraftPlayerClasspath());
            Class<?> typeNMSPlayer = Class.forName(getNMSPlayerClasspath());
            Class<?> typePlayerConnection = Class.forName(getPlayerConnectionClasspath());
            this.nmsChatSerializer = Class.forName(getChatSerializerClasspath());
            this.nmsIChatBaseComponent = Class.forName(getIChatBaseComponentClasspath());
            this.getHandle = typeCraftPlayer.getMethod("getHandle");
            this.playerConnection = typeNMSPlayer.getField("playerConnection");
            this.sendPacket = typePlayerConnection.getMethod("sendPacket", Class.forName(getPacketClasspath()));

            if (plugin.getVersionChecker().getVersion().isHigher(Version.v1_11_R1)) {
                this.ChatMessageclz = Class.forName(getChatMessageTypeClasspath());
                this.consts = this.ChatMessageclz.getEnumConstants();
                this.sub = this.consts[2].getClass();
            }

        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | NoSuchFieldException ex) {
            this.simpleMessages = true;
            Bukkit.getLogger().log(Level.SEVERE, "Your server can't fully suport action bar messages. They will be shown in chat instead.");
        }
        // Title
        try {
            Class<?> typePacketPlayOutTitle = Class.forName(getPacketPlayOutTitleClasspath());
            this.enumTitleAction = Class.forName(getEnumTitleActionClasspath());
            this.nmsPacketPlayOutTitle = typePacketPlayOutTitle.getConstructor(this.enumTitleAction, this.nmsIChatBaseComponent);
            this.fromString = Class.forName(getClassMessageClasspath()).getMethod("fromString", String.class);
        } catch (Exception ex) {
            this.simpleTitleMessages = true;
            Bukkit.getLogger().log(Level.SEVERE, "Your server can't fully suport title messages. They will be shown in chat instead.");
        }
    }

    private String getPacketPlayOutChat() {
        return "net.minecraft.server." + this.version + ".PacketPlayOutChat";
    }

    private String getCraftPlayerClasspath() {
        return "org.bukkit.craftbukkit." + this.version + ".entity.CraftPlayer";
    }

    private String getNMSPlayerClasspath() {
        return "net.minecraft.server." + this.version + ".EntityPlayer";
    }

    private String getPlayerConnectionClasspath() {
        return "net.minecraft.server." + this.version + ".PlayerConnection";
    }

    private String getChatSerializerClasspath() {
        if (this.plugin.getVersionChecker().isLower(Version.v1_8_R2)) {
            return "net.minecraft.server." + this.version + ".ChatSerializer";
        }
        return "net.minecraft.server." + this.version + ".IChatBaseComponent$ChatSerializer";// 1_8_R2 moved to IChatBaseComponent
    }

    private String getIChatBaseComponentClasspath() {
        return "net.minecraft.server." + this.version + ".IChatBaseComponent";
    }

    private String getPacketClasspath() {
        return "net.minecraft.server." + this.version + ".Packet";
    }

    private String getChatMessageTypeClasspath() {
        return "net.minecraft.server." + this.version + ".ChatMessageType";
    }

    private String getPacketPlayOutTitleClasspath() {
        return "net.minecraft.server." + this.version + ".PacketPlayOutTitle";
    }

    private String getEnumTitleActionClasspath() {
        return getPacketPlayOutTitleClasspath() + "$EnumTitleAction";
    }

    private String getClassMessageClasspath() {
        return "org.bukkit.craftbukkit." + this.version + ".util.CraftChatMessage";
    }

    @Override
    public void send(CommandSender sender, String msg) {
        if (sender instanceof Player) {
            send((Player) sender, msg);
        } else {
            sender.sendMessage(msg);
        }
    }

    @Override
    public void send(Player receivingPacket, String msg) {
        if (msg != null) {
            msg = msg.replace("%subtitle%", "");
        }
        if (this.simpleMessages) {
            receivingPacket.sendMessage(msg);
            return;
        }
        try {
            Object serialized = this.nmsChatSerializer
                .getMethod("a", String.class).invoke(null, "{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', JSONObject
                    .escape(msg)) + "\"}");
            if (this.plugin.getVersionChecker().getVersion().isHigher(Version.v1_11_R1)) {
                this.packet = this.packetType.getConstructor(this.nmsIChatBaseComponent, this.sub).newInstance(serialized, this.consts[2]);
            } else if (this.version.isHigher(Version.v1_7_R4)) {
                this.packet = this.packetType.getConstructor(this.nmsIChatBaseComponent, byte.class).newInstance(serialized, (byte) 2);
            } else {
                this.packet = this.packetType.getConstructor(this.nmsIChatBaseComponent, int.class).newInstance(serialized, 2);
            }
            Object player = this.getHandle.invoke(receivingPacket);
            Object connection = this.playerConnection.get(player);
            this.sendPacket.invoke(connection, this.packet);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException ex) {
            this.simpleMessages = true;
            Bukkit.getLogger().log(Level.SEVERE, "Your server can't fully suport action bar messages. They will be shown in chat instead.");
        }
    }

    @Override
    public void sendTitle(Player receivingPacket, Object title, Object subtitle) {
        if (this.simpleTitleMessages) {
            receivingPacket.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(title)));
            receivingPacket.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(subtitle)));
            return;
        }
        try {
            if (title != null) {
                Object packetTitle = this.nmsPacketPlayOutTitle.newInstance(this.enumTitleAction.getField("TITLE").get(null),
                    ((Object[]) this.fromString.invoke(null, ChatColor.translateAlternateColorCodes('&', String.valueOf(title))))[0]);
                sendPacket(receivingPacket, packetTitle);
            }
            if (subtitle != null) {
                Object packetSubtitle = this.nmsPacketPlayOutTitle.newInstance(this.enumTitleAction.getField("SUBTITLE").get(null),
                    ((Object[]) this.fromString.invoke(null, ChatColor.translateAlternateColorCodes('&', String.valueOf(subtitle))))[0]);
                sendPacket(receivingPacket, packetSubtitle);
            }
        } catch (Exception ex) {
            this.simpleTitleMessages = true;
            Bukkit.getLogger().log(Level.SEVERE, "Your server can't fully support title messages. They will be shown in chat instead.");
        }
    }

    @Override
    public void sendTitle(Player receivingPacket, Object title) {
        String t = (String) title;
        if (t.contains("%subtitle%")) {
            sendTitle(receivingPacket, t.split("%subtitle%")[0], t.split("%subtitle%")[1]);
        } else {
            sendTitle(receivingPacket, t, "");
        }
    }

    private void sendPacket(Player player, Object packet) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Object handle = this.getHandle.invoke(player);
        Object connection = this.playerConnection.get(handle);
        this.sendPacket.invoke(connection, packet);
    }
}
