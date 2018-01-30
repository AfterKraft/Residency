package com.gabizou.residency.permissions;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.PlayerGroup;
import com.gabizou.residency.protection.FlagPermissions;
import com.gabizou.residency.vaultinterface.ResidenceVaultAdapter;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.platymuus.bukkit.permissions.PermissionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PermissionManager {

    protected static PermissionsInterface perms;
    protected LinkedHashMap<String, PermissionGroup> groups;
    protected Map<String, String> playersGroup;
    protected FlagPermissions globalFlagPerms;

    protected HashMap<String, PlayerGroup> groupsMap = new HashMap<String, PlayerGroup>();
    private PermissionGroup defaultGroup = null;
    private Residence plugin;

    public PermissionManager(Residence plugin) {
        this.plugin = plugin;
        try {
            this.groups = new LinkedHashMap<String, PermissionGroup>();
            this.playersGroup = Collections.synchronizedMap(new HashMap<String, String>());
            this.globalFlagPerms = new FlagPermissions();
            this.readConfig();
            checkPermissions();
        } catch (Exception ex) {
            Logger.getLogger(PermissionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void readConfig() {

        FileConfiguration groupsFile = YamlConfiguration.loadConfiguration(new File(this.plugin.dataFolder, "groups.yml"));
        FileConfiguration flags = YamlConfiguration.loadConfiguration(new File(this.plugin.dataFolder, "flags.yml"));

        String defaultGroup = this.plugin.getConfigManager().getDefaultGroup().toLowerCase();
        this.globalFlagPerms = FlagPermissions.parseFromConfigNode("FlagPermission", flags.getConfigurationSection("Global"));
        ConfigurationSection nodes = groupsFile.getConfigurationSection("Groups");
        if (nodes != null) {
            Set<String> entrys = nodes.getKeys(false);
            int i = 0;
            for (String key : entrys) {
                try {
                    i++;
                    this.groups
                        .put(key.toLowerCase(), new PermissionGroup(key.toLowerCase(), nodes.getConfigurationSection(key), this.globalFlagPerms, i));
                    List<String> mirrors = nodes.getConfigurationSection(key).getStringList("Mirror");
                    for (String group : mirrors) {
                        this.groups
                            .put(group.toLowerCase(),
                                new PermissionGroup(group.toLowerCase(), nodes.getConfigurationSection(key), this.globalFlagPerms, i));
                    }
                } catch (Exception ex) {
                    Bukkit.getConsoleSender().sendMessage(this.plugin.getPrefix() + " Error parsing group from config:" + key + " Exception:" + ex);
                }
            }
        }

        if (!this.groups.containsKey(defaultGroup)) {
            this.groups.put(defaultGroup, new PermissionGroup(defaultGroup));
        }
        if (groupsFile.isConfigurationSection("GroupAssignments")) {
            Set<String> keys = groupsFile.getConfigurationSection("GroupAssignments").getKeys(false);
            if (keys != null) {
                for (String key : keys) {
                    this.playersGroup.put(key.toLowerCase(), groupsFile.getString("GroupAssignments." + key, defaultGroup).toLowerCase());
                }
            }
        }
    }

    private void checkPermissions() {
        Server server = this.plugin.getServ();
        Plugin p = server.getPluginManager().getPlugin("Vault");
        if (p != null) {
            ResidenceVaultAdapter vault = new ResidenceVaultAdapter(server);
            if (vault.permissionsOK()) {
                perms = vault;
                Bukkit.getConsoleSender()
                    .sendMessage(this.plugin.getPrefix() + " Found Vault using permissions plugin:" + vault.getPermissionsName());
                return;
            }
            Bukkit.getConsoleSender().sendMessage(this.plugin.getPrefix() + " Found Vault, but Vault reported no usable permissions system...");
        }
        p = server.getPluginManager().getPlugin("PermissionsBukkit");
        if (p != null) {
            perms = new PermissionsBukkitAdapter((PermissionsPlugin) p);
            Bukkit.getConsoleSender().sendMessage(this.plugin.getPrefix() + " Found PermissionsBukkit Plugin!");
            return;
        }

        p = server.getPluginManager().getPlugin("LuckPerms");
        if (p != null) {
            perms = new LuckPerms4Adapter();
            Bukkit.getConsoleSender().sendMessage(this.plugin.getPrefix() + " Found LuckPerms Plugin!");
            return;
        }

        p = server.getPluginManager().getPlugin("bPermissions");
        if (p != null) {
            perms = new BPermissionsAdapter();
            Bukkit.getConsoleSender().sendMessage(this.plugin.getPrefix() + " Found bPermissions Plugin!");
            return;
        }
        p = server.getPluginManager().getPlugin("Permissions");
        if (p != null) {
            if (this.plugin.getConfigManager().useLegacyPermissions()) {
                perms = new LegacyPermissions(((Permissions) p).getHandler());
                Bukkit.getConsoleSender().sendMessage(this.plugin.getPrefix() + " Found Permissions Plugin!");
                Bukkit.getConsoleSender().sendMessage(this.plugin.getPrefix() + "Permissions running in Legacy mode!");
            } else {
                perms = new OriginalPermissions(((Permissions) p).getHandler());
                Bukkit.getConsoleSender().sendMessage(this.plugin.getPrefix() + " Found Permissions Plugin!");
            }
            return;
        }
        Bukkit.getConsoleSender().sendMessage(this.plugin.getPrefix() + " Permissions plugin NOT FOUND!");
    }

    public FlagPermissions getAllFlags() {
        return this.globalFlagPerms;
    }

    public Map<String, String> getPlayersGroups() {
        return this.playersGroup;
    }

    public Map<String, PermissionGroup> getGroups() {
        return this.groups;
    }

    public PermissionGroup getGroupByName(String group) {
        group = group.toLowerCase();
        if (!this.groups.containsKey(group)) {
            return getDefaultGroup();
        }
        return this.groups.get(group);
    }

    public PermissionGroup getDefaultGroup() {
        if (this.defaultGroup == null) {
            this.defaultGroup = this.groups.get(Residence.getInstance().getConfigManager().getDefaultGroup().toLowerCase());
        }
        return this.defaultGroup;
    }

    public String getPermissionsGroup(Player player) {
        return this.getPermissionsGroup(player.getName(), player.getWorld().getName()).toLowerCase();
    }

    public String getPermissionsGroup(String player, String world) {
        if (perms == null) {
            return this.plugin.getConfigManager().getDefaultGroup().toLowerCase();
        }
        try {
            return perms.getPlayerGroup(player, world).toLowerCase();
        } catch (Exception e) {
            return this.plugin.getConfigManager().getDefaultGroup().toLowerCase();
        }
    }

    public boolean isResidenceAdmin(CommandSender sender) {
        return (sender.hasPermission("residence.admin") || (sender.isOp() && this.plugin.getConfigManager().getOpsAreAdmins()));
    }

    public boolean hasGroup(String group) {
        group = group.toLowerCase();
        return this.groups.containsKey(group);
    }

    public PermissionsInterface getPermissionsPlugin() {
        return perms;
    }
}
