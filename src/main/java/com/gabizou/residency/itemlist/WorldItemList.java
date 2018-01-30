package com.gabizou.residency.itemlist;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class WorldItemList extends ItemList {

    protected String world;
    protected String group;

    public WorldItemList(ListType listType) {
        super(listType);
    }

    protected WorldItemList() {

    }

    public static WorldItemList readList(ConfigurationSection node) {
        WorldItemList list = new WorldItemList();
        ItemList.readList(node, list);
        list.world = node.getString("World", null);
        list.group = node.getString("Group", null);
        return list;
    }

    public String getWorld() {
        return this.world;
    }

    public String getGroup() {
        return this.group;
    }

    public boolean isAllowed(Material mat, String inworld, String ingroup) {
        if (!listApplicable(inworld, ingroup)) {
            return true;
        }
        return super.isAllowed(mat);
    }

    public boolean listApplicable(String inworld, String ingroup) {
        if (this.world != null) {
            if (!this.world.equals(inworld)) {
                return false;
            }
        }
        if (this.group != null) {
            if (!this.group.equals(ingroup)) {
                return false;
            }
        }
        return true;
    }

    public boolean isIgnored(Material mat, String inworld, String ingroup) {
        if (!listApplicable(inworld, ingroup)) {
            return false;
        }
        return super.isIgnored(mat);
    }

    public boolean isListed(Material mat, String inworld, String ingroup) {
        if (!listApplicable(inworld, ingroup)) {
            return false;
        }
        return super.isListed(mat);
    }
}
