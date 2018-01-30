package com.gabizou.residency.itemlist;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ItemList {

    protected List<Material> list;
    protected ListType type;

    public ItemList(ListType listType) {
        this();
        this.type = listType;
    }

    protected ItemList() {
        this.list = new ArrayList<Material>();
    }

    public static ItemList readList(ConfigurationSection node) {
        return ItemList.readList(node, new ItemList());
    }

    @SuppressWarnings("deprecation")
    protected static ItemList readList(ConfigurationSection node, ItemList list) {
        ListType type = ListType.valueOf(node.getString("Type", "").toUpperCase());
        list.type = type;
        List<String> items = node.getStringList("Items");
        if (items != null) {
            for (String item : items) {
                int parse = -1;
                try {
                    parse = Integer.parseInt(item);
                } catch (Exception ex) {
                }
                if (parse == -1) {
                    try {
                        list.add(Material.valueOf(item.toUpperCase()));
                    } catch (Exception ex) {
                    }
                } else {
                    try {
                        list.add(Material.getMaterial(parse));
                    } catch (Exception ex) {
                    }
                }
            }
        }
        return list;
    }

    public void add(Material mat) {
        if (!this.list.contains(mat)) {
            this.list.add(mat);
        }
    }

    public static ItemList load(Map<String, Object> map) {
        ItemList newlist = new ItemList();
        return load(map, newlist);
    }

    protected static ItemList load(Map<String, Object> map, ItemList newlist) {
        try {
            newlist.type = ListType.valueOf((String) map.get("Type"));
            @SuppressWarnings("unchecked")
            List<String> list = (List<String>) map.get("ItemList");
            for (String item : list) {
                newlist.add(Material.valueOf(item));
            }
        } catch (Exception ex) {
        }
        return newlist;
    }

    public ListType getType() {
        return this.type;
    }

    public boolean toggle(Material mat) {
        if (this.list.contains(mat)) {
            this.list.remove(mat);
            return false;
        }
        this.list.add(mat);
        return true;
    }

    public void remove(Material mat) {
        this.list.remove(mat);
    }

    public boolean isAllowed(Material mat) {
        if (this.type == ListType.BLACKLIST) {
            if (this.list.contains(mat)) {
                return false;
            }
            return true;
        } else if (this.type == ListType.WHITELIST) {
            if (this.list.contains(mat)) {
                return true;
            }
            return false;
        }
        return true;
    }

    public boolean isIgnored(Material mat) {
        if (this.type == ListType.IGNORELIST) {
            if (this.list.contains(mat)) {
                return true;
            }
        }
        return false;
    }

    public boolean isListed(Material mat) {
        return this.contains(mat);
    }

    public boolean contains(Material mat) {
        return this.list.contains(mat);
    }

    public int getListSize() {
        return this.list.size();
    }

    public void printList(Player player) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Material mat : this.list) {
            if (!first) {
                builder.append(", ");
            } else {
                builder.append(ChatColor.YELLOW);
            }
            builder.append(mat);
            first = false;
        }
        player.sendMessage(builder.toString());
    }

    public Material[] toArray() {
        Material mats[] = new Material[this.list.size()];
        int i = 0;
        for (Material mat : this.list) {
            mats[i] = mat;
            i++;
        }
        return mats;
    }

    public Map<String, Object> save() {
        Map<String, Object> saveMap = new LinkedHashMap<String, Object>();
        if (this.list.isEmpty()) {
            return saveMap;
        }
        saveMap.put("Type", this.type.toString());
        List<String> saveList = new ArrayList<String>();
        for (Material mat : this.list) {
            saveList.add(mat.toString());
        }
        saveMap.put("ItemList", saveList);
        return saveMap;
    }

    public static enum ListType {
        BLACKLIST, WHITELIST, IGNORELIST, OTHER
    }
}
