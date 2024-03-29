package com.gabizou.residency.text.help;

import com.gabizou.residency.Residence;
import com.gabizou.residency.ResidenceCommandListener;
import com.gabizou.residency.containers.Flags;
import com.gabizou.residency.containers.HelpLines;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.protection.ClaimedResidence;
import com.gabizou.residency.protection.FlagPermissions;
import com.gabizou.residency.utils.RawMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HelpEntry {

    protected static int linesPerPage = 8;
    protected String name;
    protected String desc;
    protected String[] lines;
    protected List<HelpEntry> subentrys;

    public HelpEntry(String entryname) {
        this.name = entryname;
        this.subentrys = new ArrayList<HelpEntry>();
        this.lines = new String[0];
    }

    public static HelpEntry parseHelp(FileConfiguration node, String key) {
        String split[] = key.split("\\.");
        String thisname = split[split.length - 1];
        HelpEntry entry = new HelpEntry(thisname);
        ConfigurationSection keysnode = node.getConfigurationSection(key);

        Set<String> keys = null;
        if (keysnode != null) {
            keys = keysnode.getKeys(false);
        }
        if (keys != null) {
            if (keys.contains("Info")) {
                List<String> stringList = node.getStringList(key + ".Info");
                if (stringList != null) {
                    entry.lines = new String[stringList.size()];
                    for (int i = 0; i < stringList.size(); i++) {
                        entry.lines[i] = ChatColor.translateAlternateColorCodes('&', stringList.get(i));
                    }
                }
            }
            if (keys.contains("Description")) {
                entry.desc = node.getString(key + ".Description");
            }
            if (keys.contains("SubCommands")) {
                Set<String> subcommandkeys = node.getConfigurationSection(key + ".SubCommands").getKeys(false);
                if (key.equalsIgnoreCase("CommandHelp.SubCommands.res")) {
                    subcommandkeys.clear();
                    for (String one : Residence.getInstance().getCommandFiller().getCommands()) {
                        subcommandkeys.add(one);
                    }
                }
                for (String subkey : subcommandkeys) {
                    entry.subentrys.add(HelpEntry.parseHelp(node, key + ".SubCommands." + subkey));
                }
            }
        }
        return entry;
    }

    public String getDescription() {
        if (this.desc == null) {
            return "";
        }
        return this.desc;
    }

    public void setDescription(String description) {
        this.desc = description;
    }

    public void printHelp(CommandSender sender, int page, boolean resadmin, String path) {
        List<HelpLines> helplines = this.getHelpData(sender, resadmin);
        path = "/" + path.replace(".", " ") + " ";

        PageInfo pi = new PageInfo(linesPerPage, helplines.size(), page);

        if (!pi.isPageOk()) {
            Residence.getInstance().msg(sender, lm.Invalid_Help);
            return;
        }

        String separator = Residence.getInstance().msg(lm.InformationPage_SmallSeparator);

        sender
            .sendMessage(separator + " " + Residence.getInstance().msg(lm.General_HelpPageHeader, path, page, pi.getTotalPages()) + " " + separator);
        for (int i = pi.getStart(); i <= pi.getEnd(); i++) {
            if (helplines.get(i).getCommand() != null) {
                HelpEntry sub = this.getSubEntry(helplines.get(i).getCommand());

                String desc = "";
                int y = 0;
                for (String one : sub.lines) {
                    desc += one;
                    y++;
                    if (y < sub.lines.length) {
                        desc += "\n";
                    }
                }

                if (resadmin) {
                    path = path.replace("/res ", "/resadmin ");
                }

                RawMessage rm = new RawMessage();
                rm.add(helplines.get(i).getDesc(), desc, null, path + helplines.get(i).getCommand());
                rm.show(sender);

            } else {
                sender.sendMessage(helplines.get(i).getDesc());
            }

        }

        String baseCmd = resadmin ? "resadmin" : "res";
        String cmd = !this.name.equalsIgnoreCase("res") ? "/" + baseCmd + " " + this.name + " ? " : "/" + baseCmd + " ? ";
        Residence.getInstance().getInfoPageManager().ShowPagination(sender, pi.getTotalPages(), page, cmd);
    }

    public void printHelp(CommandSender sender, int page, String path, boolean resadmin) {
        HelpEntry subEntry = this.getSubEntry(path);
        if (subEntry != null) {
            subEntry.printHelp(sender, page, resadmin, path);
        } else {
            Residence.getInstance().msg(sender, lm.Invalid_Help);
        }
    }

    private List<HelpLines> getHelpData(CommandSender sender, boolean resadmin) {
        List<HelpLines> helplines = new ArrayList<HelpLines>();

        for (String one : this.lines) {
            helplines.add(new HelpLines(null, one));
        }

        FlagPermissions GlobalFlags = Residence.getInstance().getPermissionManager().getAllFlags();

        Map<String, String> unsortMap = new HashMap<String, String>();

        for (HelpEntry entry : this.subentrys) {

            if (!this.name.equalsIgnoreCase("flags")) {
                if (ResidenceCommandListener.getAdminCommands().contains(entry.getName().toLowerCase()) && !resadmin) {
                    continue;
                }

                if (!ResidenceCommandListener.getAdminCommands().contains(entry.getName().toLowerCase()) && resadmin) {
                    continue;
                }

            } else {
                if (GlobalFlags.getFlags().containsKey(entry.getName().toLowerCase())) {
                    Boolean state = GlobalFlags.getFlags().get(entry.getName().toLowerCase());
                    if (!state && !resadmin && !sender.hasPermission("residence.flag." + entry.getName().toLowerCase())) {
                        continue;
                    }

                    String desc = entry.getDescription();

                    switch (entry.getName().toLowerCase()) {
                        case "wspeed1":
                            desc = desc.replace("%1", Residence.getInstance().getConfigManager().getWalkSpeed1() + "");
                            break;
                        case "wspeed2":
                            desc = desc.replace("%1", Residence.getInstance().getConfigManager().getWalkSpeed2() + "");
                            break;
                    }

                    // adding flag name and description for later sorting
                    unsortMap.put(entry.getName(), Residence.getInstance().msg(lm.InformationPage_FlagsList, entry.getName(), desc));
                    continue;
                }
            }

            helplines.add(
                new HelpLines(entry.getName(), Residence.getInstance().msg(lm.InformationPage_GeneralList, entry.getName(), entry.getDescription())));
        }

        if (!unsortMap.isEmpty()) {
            // Sorting flags help page by alphabet
            unsortMap = Residence.getInstance().getSortingManager().sortStringByKeyASC(unsortMap);
            // Converting HashMap to helplines
            for (Entry<String, String> one : unsortMap.entrySet()) {
                helplines.add(new HelpLines(one.getKey(), one.getValue()));
            }
        }

        return helplines;
    }

    public boolean containesEntry(String name) {
        return this.getSubEntry(name) != null;
    }

    public HelpEntry getSubEntry(String name) {
        String[] split = name.split("\\.");
        HelpEntry entry = this;
        for (String entryname : split) {
            entry = entry.findSubEntry(entryname);
            if (entry == null) {
                return null;
            }
        }
        return entry;
    }

    private HelpEntry findSubEntry(String name) {
        for (HelpEntry entry : this.subentrys) {
            if (entry.getName().equalsIgnoreCase(name)) {
                return entry;
            }
        }
        return null;
    }

    public String getName() {
        if (this.name == null) {
            return "";
        }
        return this.name;
    }

    public void setName(String inname) {
        this.name = inname;
    }

    public void addSubEntry(HelpEntry entry) {
        if (!this.subentrys.contains(entry)) {
            this.subentrys.add(entry);
        }
    }

    public void removeSubEntry(HelpEntry entry) {
        if (this.subentrys.contains(entry)) {
            this.subentrys.remove(entry);
        }
    }

    public int getSubEntryCount() {
        return this.subentrys.size();
    }

    @SuppressWarnings("deprecation")
    public Set<String> getSubCommands(CommandSender sender, String[] args) {
        File langFile = new File(new File(Residence.getInstance().getDataLocation(), "Language"), "English.yml");
        Set<String> subCommands = new HashSet<String>();

        if (langFile.isFile()) {
            FileConfiguration node = new YamlConfiguration();
            try {
                node.load(langFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
            }

            ConfigurationSection meinPath = node.getConfigurationSection("CommandHelp.SubCommands.res.SubCommands");
            ConfigurationSection tempmeinPath = node.getConfigurationSection("CommandHelp.SubCommands.res.SubCommands");

            if (args.length == 1) {
                return meinPath.getKeys(false);
            }

            boolean ok = true;
            int i = 0;
            while (ok) {

                if (args[i].equalsIgnoreCase("")) {
                    return tempmeinPath.getKeys(false);
                }

                if (!tempmeinPath.isConfigurationSection(args[i] + ".SubCommands")) {
                    break;
                }

                tempmeinPath = tempmeinPath.getConfigurationSection(args[i] + ".SubCommands");

                i++;
            }

            int neededArgPlace = args.length - 2 - i;

            boolean subCommand = true;
            if (tempmeinPath.isConfigurationSection(args[i])) {
                subCommand = false;
                tempmeinPath = tempmeinPath.getConfigurationSection(args[i]);
            }

            List<String> ArgsList = new ArrayList<String>();

            int ii = 0;
            for (Entry<List<String>, List<String>> one : Residence.getInstance().getLocaleManager().CommandTab.entrySet()) {
                List<String> list = one.getKey();
                if (list.size() > ii && args.length > ii && list.get(ii).equalsIgnoreCase(args[ii])) {
                    ArgsList = one.getValue();
                }
                i++;
            }

            String NeededArg = null;
            if (neededArgPlace < ArgsList.size() && neededArgPlace >= 0) {
                NeededArg = ArgsList.get(neededArgPlace);
            }

            Player playerSender = null;
            if (sender instanceof Player) {
                playerSender = (Player) sender;
            }

            if (NeededArg != null) {

                List<String> list = new ArrayList<String>();

                if (NeededArg.contains("%%")) {
                    list.addAll(Arrays.asList(NeededArg.split("%%")));
                } else {
                    list.add(NeededArg);
                }

                for (String oneArg : list) {
                    switch (oneArg) {
                        case "[playername]":
                            for (Player one : Bukkit.getOnlinePlayers()) {
                                if (playerSender == null || one.canSee(playerSender)) {
                                    subCommands.add(one.getName());
                                }
                            }
                            break;
                        case "[residence]":
                            if (sender instanceof Player) {
                                ClaimedResidence res = Residence.getInstance().getResidenceManager().getByLoc(((Player) sender).getLocation());
                                if (res != null) {
                                    String resName = res.getName();
                                    if (resName != null) {
                                        subCommands.add(resName);
                                    }
                                }
                                List<ClaimedResidence>
                                    resList =
                                    Residence.getInstance().getPlayerManager().getResidencePlayer(((Player) sender)).getResList();
                                for (ClaimedResidence oneRes : resList) {
                                    subCommands.add(oneRes.getName());
                                }
                            } else {
                                ArrayList<String>
                                    resList =
                                    Residence.getInstance().getResidenceManager()
                                        .getResidenceList(Residence.getInstance().getServerLandname(), true, false, false);
                                if (resList.size() > 0) {
                                    subCommands.addAll(resList);
                                }
                            }
                            break;
                        case "[cresidence]":
                            if (sender instanceof Player) {
                                ClaimedResidence res = Residence.getInstance().getResidenceManager().getByLoc(((Player) sender).getLocation());
                                if (res != null) {
                                    String resName = res.getName();
                                    if (resName != null) {
                                        subCommands.add(resName);
                                    }
                                }
                            }
                            break;
                        case "[residenceshop]":
                            for (ClaimedResidence one : Residence.getInstance().getResidenceManager().getShops()) {
                                subCommands.add(one.getName());
                            }
                            break;
                        case "[flag]":
                            for (Flags one : Flags.values()) {
                                subCommands.add(one.getName());
                            }
                            break;
                        case "[material]":
                            for (Material one : Material.values()) {
                                subCommands.add(one.name().toLowerCase());
                            }
                            break;
                        case "[materialId]":
                            for (Material one : Material.values()) {
                                subCommands.add(String.valueOf(one.getId()));
                            }
                            break;
                        case "[worldname]":
                            for (World one : Bukkit.getWorlds()) {
                                subCommands.add(one.getName());
                            }
                            break;
                        default:
                            subCommands.add(oneArg);
                            break;
                    }
                }
            }

            String command = tempmeinPath.getCurrentPath().replace("CommandHelp.SubCommands.", "").replace(".SubCommands.", " ");
            if (subCommands.size() > 0) {
                return subCommands;
            }

            if (subCommand) {
                return tempmeinPath.getKeys(false);
            }
            Bukkit.dispatchCommand(sender, command + " ?");
        }
        return new HashSet<String>(Arrays.asList("?"));
    }
}
