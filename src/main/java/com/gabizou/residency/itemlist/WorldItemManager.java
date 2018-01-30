package com.gabizou.residency.itemlist;

import com.gabizou.residency.Residence;
import com.gabizou.residency.permissions.PermissionGroup;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WorldItemManager {

    protected List<WorldItemList> lists;
    private Residence plugin;

    public WorldItemManager(Residence plugin) {
        this.plugin = plugin;
        this.lists = new ArrayList<WorldItemList>();
        this.readLists();
    }

    private void readLists() {
        FileConfiguration flags = YamlConfiguration.loadConfiguration(new File(this.plugin.dataFolder, "flags.yml"));
        if (!flags.isConfigurationSection("ItemList")) {
            return;
        }
        Set<String> keys = flags.getConfigurationSection("ItemList").getKeys(false);
        if (keys != null) {
            for (String key : keys) {
                try {
                    WorldItemList list = WorldItemList.readList(flags.getConfigurationSection("ItemList." + key));
                    this.lists.add(list);
                    //System.out.println("Debug: read list " + key + " world: " + list.getWorld() + " group: " + list.getGroup() + " itemcount:" + list.getListSize());
                } catch (Exception ex) {
                    System.out.println("Failed to load item list:" + key);
                }
            }
        }
    }

    public boolean isAllowed(Material mat, PermissionGroup group, String world) {
        if (group == null) {
            return true;
        }
        return isAllowed(mat, group.getGroupName(), world);
    }

    public boolean isAllowed(Material mat, String group, String world) {
        for (WorldItemList list : this.lists) {
            if (!list.isAllowed(mat, world, group)) {
                return false;
            }
        }
        return true;
    }

    public boolean isIgnored(Material mat, PermissionGroup group, String world) {
        if (group == null) {
            return false;
        }
        return isIgnored(mat, group.getGroupName(), world);
    }

    public boolean isIgnored(Material mat, String group, String world) {
        for (WorldItemList list : this.lists) {
            if (list.isIgnored(mat, world, group)) {
                return true;
            }
        }
        return false;
    }
}
