package com.gabizou.residency.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RawMessage {

    List<String> parts = new ArrayList<String>();
    List<String> cleanParts = new ArrayList<String>();
    String combined = "";
    String combinedClean = "";

    public void clear() {
        this.parts = new ArrayList<String>();
        this.cleanParts = new ArrayList<String>();
        this.combined = "";
        this.combinedClean = "";
    }

    public RawMessage add(String text) {
        return add(text, null, null, null);
    }

    public RawMessage add(String text, String hoverText, String command, String suggestion) {
		if (text == null) {
			return this;
		}
        String f = "{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', text) + "\"";

        String last = ChatColor.getLastColors(text);
        if (last != null && !last.isEmpty()) {
            ChatColor color = ChatColor.getByChar(last.replace("�", ""));
            if (color != null) {
                f += ",\"color\":\"" + color.name().toLowerCase() + "\"";
            }
        }
		if (hoverText != null) {
			f +=
				",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + ChatColor
					.translateAlternateColorCodes('&', hoverText) + "\"}]}}";
		}
		if (suggestion != null) {
			f += ",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"" + suggestion + "\"}";
		}
        if (command != null) {
			if (!command.startsWith("/")) {
				command = "/" + command;
			}
            f += ",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + command + "\"}";
        }
        f += "}";
        this.parts.add(f);
        this.cleanParts.add(ChatColor.translateAlternateColorCodes('&', text));
        return this;
    }

    public RawMessage add(String text, String hoverText) {
        return add(text, hoverText, null, null);
    }

    public RawMessage add(String text, String hoverText, String command) {
        return add(text, hoverText, command, null);
    }

    public RawMessage showClean(Player player) {
		if (this.combinedClean.isEmpty()) {
			combineClean();
		}
        player.sendMessage(this.combined);
        return this;
    }

    public RawMessage combineClean() {
        String f = "";
        for (String part : this.cleanParts) {
            f += part;
        }
        this.combinedClean = f;
        return this;
    }

    public RawMessage show(CommandSender sender) {
		if (this.combined.isEmpty()) {
			combine();
		}
		if (sender instanceof Player) {
			show((Player) sender);
		} else {
			sender.sendMessage(this.combineClean().combinedClean);
		}
        return this;
    }

    public RawMessage combine() {
        String f = "";
        for (String part : this.parts) {
			if (f.isEmpty()) {
				f = "[\"\",";
			} else {
				f += ",";
			}
            f += part;
        }
		if (!f.isEmpty()) {
			f += "]";
		}
        this.combined = f;
        return this;
    }

    public RawMessage show(Player player) {
		if (this.combined.isEmpty()) {
			combine();
		}
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + this.combined);
        return this;
    }

    public String getRaw() {
		if (this.combined.isEmpty()) {
			combine();
		}
        return this.combined;
    }

}
