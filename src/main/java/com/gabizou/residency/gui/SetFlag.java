package com.gabizou.residency.gui;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.Flags;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.protection.ClaimedResidence;
import com.gabizou.residency.protection.FlagPermissions.FlagState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class SetFlag {

    private ClaimedResidence residence;
    private Player player;
    private String targetPlayer = null;
    private Inventory inventory;
    private LinkedHashMap<String, Object> permMap = new LinkedHashMap<String, Object>();
    private LinkedHashMap<String, List<String>> description = new LinkedHashMap<String, List<String>>();
    private List<String> flags = null;
    private boolean admin = false;
    private int page = 1;
    private int pageCount = 1;

    public SetFlag(ClaimedResidence residence, Player player, boolean admin) {
        this.residence = residence;
        this.player = player;
        this.admin = admin;
        fillFlagDescriptions();
    }

    private void fillFlagDescriptions() {
        Set<String> list = Residence.getInstance().getLM().getKeyList("CommandHelp.SubCommands.res.SubCommands.flags.SubCommands");
        for (String onelist : list) {
            String onelisttemp = Residence.getInstance().msg("CommandHelp.SubCommands.res.SubCommands.flags.SubCommands." + onelist + ".Description");
            List<String> lore = new ArrayList<String>();
            int i = 0;
            String sentence = "";
            for (String oneWord : onelisttemp.split(" ")) {
                sentence += oneWord + " ";
                if (i > 4) {
                    lore.add(ChatColor.YELLOW + sentence);
                    sentence = "";
                    i = 0;
                }
                i++;
            }
            lore.add(ChatColor.YELLOW + sentence);
            this.description.put(onelist, lore);
        }
    }

    public boolean isAdmin() {
        return this.admin;
    }

    public void setAdmin(boolean state) {
        this.admin = state;
    }

    public void setTargetPlayer(String player) {
        this.targetPlayer = player;
    }

    public ClaimedResidence getResidence() {
        return this.residence;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void toggleFlag(int slot, ClickType click, InventoryAction action) {
        ItemStack item = this.inventory.getItem(slot);
        if (item == null) {
            return;
        }
        String command = "true";
        if (click.isLeftClick() && action != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            command = "true";
        } else if (click.isRightClick() && action != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            command = "false";
        } else if (click.isLeftClick() && action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            command = "remove";
        } else if (click.isRightClick() && action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            return;
        }

        if (slot == 53) {
            if (this.page < this.pageCount) {
                this.page++;
            }
            recalculateInv();
            return;
        } else if (slot == 45) {
            if (this.page > 1) {
                this.page--;
            }
            recalculateInv();
            return;
        }

        String flag = "";
        int i = 0;
        for (Entry<String, Object> one : this.permMap.entrySet()) {
            flag = one.getKey();
            if (i == slot) {
                break;
            }
            i++;
        }

        String targetPlayerName = this.targetPlayer == null ? "" : " " + this.targetPlayer;
        String actionType = this.targetPlayer == null ? "set" : "pset";
        String cmdPrefix = this.admin ? "resadmin" : "res";

        Bukkit.dispatchCommand(this.player,
            cmdPrefix + " " + actionType + " " + this.residence.getName() + targetPlayerName + " " + flag + " " + command);
        if (Residence.getInstance().getConfigManager().isConsoleLogsShowFlagChanges()) {
            Residence.getInstance().consoleMessage(
                this.player.getName() + " issued server command: /" + cmdPrefix + " " + actionType + " " + this.residence.getName() + targetPlayerName
                + " " + flag + " "
                + command);
        }

    }

    public void recalculateInv() {
        if (this.targetPlayer == null) {
            recalculateResidence(this.residence);
        } else {
            recalculatePlayer(this.residence);
        }
    }

    @SuppressWarnings("incomplete-switch")
    public void recalculateResidence(ClaimedResidence res) {

        if (this.flags == null) {
            this.flags = res.getPermissions().getPosibleFlags(this.player, true, this.admin);
        }

        Map<String, Boolean> resFlags = new HashMap<String, Boolean>();
        Map<String, Object> TempPermMap = new LinkedHashMap<String, Object>();

        Map<String, Boolean> globalFlags = Residence.getInstance().getPermissionManager().getAllFlags().getFlags();

        for (Entry<String, Boolean> one : res.getPermissions().getFlags().entrySet()) {
            if (this.flags.contains(one.getKey())) {
                resFlags.put(one.getKey(), one.getValue());
            }
        }

        for (Entry<String, Boolean> one : globalFlags.entrySet()) {
            if (!this.flags.contains(one.getKey())) {
                continue;
            }

            if (resFlags.containsKey(one.getKey())) {
                TempPermMap.put(one.getKey(), resFlags.get(one.getKey()) ? FlagState.TRUE : FlagState.FALSE);
            } else {
                TempPermMap.put(one.getKey(), FlagState.NEITHER);
            }
        }

        String title = "";
        if (this.targetPlayer == null) {
            title = Residence.getInstance().msg(lm.Gui_Set_Title, res.getName());
        } else {
            title = Residence.getInstance().msg(lm.Gui_Pset_Title, this.targetPlayer, res.getName());
        }

        if (title.length() > 32) {
            title = title.substring(0, Math.min(title.length(), 32));
        }

        Inventory GuiInv = Bukkit.createInventory(null, 54, title);
        int i = 0;

        if (this.targetPlayer == null) {
            TempPermMap.remove("admin");
        }

        TempPermMap = Residence.getInstance().getSortingManager().sortByKeyASC(TempPermMap);

        FlagData flagData = Residence.getInstance().getFlagUtilManager().getFlagData();

        this.pageCount = (int) Math.ceil((double) TempPermMap.size() / (double) 45);

        int start = this.page * 45 - 45;
        int end = this.page * 45;

        int count = -1;
        this.permMap.clear();
        for (Entry<String, Object> one : TempPermMap.entrySet()) {
            count++;
            if (count >= end) {
                break;
            }
            if (count < start) {
                continue;
            }
            this.permMap.put(one.getKey(), one.getValue());
        }

        for (Entry<String, Object> one : this.permMap.entrySet()) {

            ItemStack MiscInfo = Residence.getInstance().getConfigManager().getGuiRemove();

            switch ((FlagState) one.getValue()) {
                case FALSE:
                    MiscInfo = Residence.getInstance().getConfigManager().getGuiFalse();
                    break;
                case TRUE:
                    MiscInfo = Residence.getInstance().getConfigManager().getGuiTrue();
                    break;
            }

            if (flagData.contains(one.getKey())) {
                MiscInfo = flagData.getItem(one.getKey());
            }

            if ((FlagState) one.getValue() == FlagState.TRUE) {
                ItemMeta im = MiscInfo.getItemMeta();
                im.addEnchant(Enchantment.LUCK, 1, true);
                MiscInfo.setItemMeta(im);
            } else {
                MiscInfo.removeEnchantment(Enchantment.LUCK);
            }

            ItemMeta MiscInfoMeta = MiscInfo.getItemMeta();
            MiscInfoMeta.setDisplayName(ChatColor.GREEN + one.getKey());

            List<String> lore = new ArrayList<String>();

            String variable = "";
            switch ((FlagState) one.getValue()) {
                case FALSE:
                    variable = Residence.getInstance().msg(lm.General_False);
                    break;
                case TRUE:
                    variable = Residence.getInstance().msg(lm.General_True);
                    break;
                case NEITHER:
                    variable = Residence.getInstance().msg(lm.General_Removed);
                    break;
            }
            lore.add(Residence.getInstance().msg(lm.General_FlagState, variable));

            if (this.description.containsKey(one.getKey())) {
                lore.addAll(this.description.get(one.getKey()));
            }

            lore.addAll(Residence.getInstance().msgL(lm.Gui_Actions));

            MiscInfoMeta.setLore(lore);

            MiscInfo.setItemMeta(MiscInfoMeta);
            GuiInv.setItem(i, MiscInfo);
            i++;
            if (i > 53) {
                break;
            }
        }

        ItemStack Item = new ItemStack(Material.ARROW);

        ItemMeta meta = Item.getItemMeta();
        if (this.page > 1) {
            meta.setDisplayName(Residence.getInstance().msg(lm.General_PrevInfoPage));
            Item.setItemMeta(meta);
            GuiInv.setItem(45, Item);
        }

        if (this.page < this.pageCount) {
            meta.setDisplayName(Residence.getInstance().msg(lm.General_NextInfoPage));
            Item.setItemMeta(meta);
            GuiInv.setItem(53, Item);
        }

        this.inventory = GuiInv;
    }

    @SuppressWarnings("incomplete-switch")
    public void recalculatePlayer(ClaimedResidence res) {
        Map<String, Boolean> globalFlags = new HashMap<String, Boolean>();
        for (Flags oneFlag : Flags.values()) {
            globalFlags.put(oneFlag.getName(), oneFlag.isEnabled());
        }

        if (this.flags == null) {
            this.flags = res.getPermissions().getPosibleFlags(this.player, false, this.admin);
        }
        Map<String, Boolean> resFlags = new HashMap<String, Boolean>();

        for (Entry<String, Boolean> one : res.getPermissions().getFlags().entrySet()) {
            if (this.flags.contains(one.getKey())) {
                resFlags.put(one.getKey(), one.getValue());
            }
        }

        if (this.targetPlayer != null) {
            Set<String> PosibleResPFlags = res.getPermissions().getposibleFlags();
            Map<String, Boolean> temp = new HashMap<String, Boolean>();
            for (String one : PosibleResPFlags) {
                if (globalFlags.containsKey(one)) {
                    temp.put(one, globalFlags.get(one));
                }
            }
            globalFlags = temp;

            Map<String, Boolean> pFlags = res.getPermissions().getPlayerFlags(this.targetPlayer);

            if (pFlags != null) {
                for (Entry<String, Boolean> one : pFlags.entrySet()) {
                    resFlags.put(one.getKey(), one.getValue());
                }
            }
        }

        LinkedHashMap<String, Object> TempPermMap = new LinkedHashMap<String, Object>();

        for (Entry<String, Boolean> one : globalFlags.entrySet()) {
            if (!this.flags.contains(one.getKey())) {
                continue;
            }

            if (resFlags.containsKey(one.getKey())) {
                TempPermMap.put(one.getKey(), resFlags.get(one.getKey()) ? FlagState.TRUE : FlagState.FALSE);
            } else {
                TempPermMap.put(one.getKey(), FlagState.NEITHER);
            }
        }

        String title = "";
        if (this.targetPlayer == null) {
            title = Residence.getInstance().msg(lm.Gui_Set_Title, res.getName());
        } else {
            title = Residence.getInstance().msg(lm.Gui_Pset_Title, this.targetPlayer, res.getName());
        }

        if (title.length() > 32) {
            title = title.substring(0, Math.min(title.length(), 32));
        }

        Inventory GuiInv = Bukkit.createInventory(null, 54, title);
        int i = 0;

        TempPermMap = (LinkedHashMap<String, Object>) Residence.getInstance().getSortingManager().sortByKeyASC(TempPermMap);

        FlagData flagData = Residence.getInstance().getFlagUtilManager().getFlagData();

        this.pageCount = (int) Math.ceil((double) TempPermMap.size() / (double) 45);

        int start = this.page * 45 - 45;
        int end = this.page * 45;

        int count = -1;
        this.permMap.clear();
        for (Entry<String, Object> one : TempPermMap.entrySet()) {
            count++;
            if (count >= end) {
                break;
            }
            if (count < start) {
                continue;
            }
            this.permMap.put(one.getKey(), one.getValue());
        }

        for (Entry<String, Object> one : this.permMap.entrySet()) {

            ItemStack MiscInfo = Residence.getInstance().getConfigManager().getGuiRemove();

            switch ((FlagState) one.getValue()) {
                case FALSE:
                    MiscInfo = Residence.getInstance().getConfigManager().getGuiFalse();
                    break;
                case TRUE:
                    MiscInfo = Residence.getInstance().getConfigManager().getGuiTrue();
                    break;
            }

            if (flagData.contains(one.getKey())) {
                MiscInfo = flagData.getItem(one.getKey());
            }

            if ((FlagState) one.getValue() == FlagState.TRUE) {
                ItemMeta im = MiscInfo.getItemMeta();
                im.addEnchant(Enchantment.LUCK, 1, true);
                MiscInfo.setItemMeta(im);
            } else {
                MiscInfo.removeEnchantment(Enchantment.LUCK);
            }

            ItemMeta MiscInfoMeta = MiscInfo.getItemMeta();
            MiscInfoMeta.setDisplayName(ChatColor.GREEN + one.getKey());

            List<String> lore = new ArrayList<String>();

            String variable = "";
            switch ((FlagState) one.getValue()) {
                case FALSE:
                    variable = Residence.getInstance().msg(lm.General_False);
                    break;
                case TRUE:
                    variable = Residence.getInstance().msg(lm.General_True);
                    break;
                case NEITHER:
                    variable = Residence.getInstance().msg(lm.General_Removed);
                    break;
            }
            lore.add(Residence.getInstance().msg(lm.General_FlagState, variable));

            if (this.description.containsKey(one.getKey())) {
                lore.addAll(this.description.get(one.getKey()));
            }

            lore.addAll(Residence.getInstance().msgL(lm.Gui_Actions));

            MiscInfoMeta.setLore(lore);

            MiscInfo.setItemMeta(MiscInfoMeta);
            GuiInv.setItem(i, MiscInfo);
            i++;
            if (i > 53) {
                break;
            }
        }
        ItemStack Item = new ItemStack(Material.ARROW);

        ItemMeta meta = Item.getItemMeta();
        if (this.page > 1) {
            meta.setDisplayName(Residence.getInstance().msg(lm.General_PrevInfoPage));
            Item.setItemMeta(meta);
            GuiInv.setItem(45, Item);
        }
        if (this.page < this.pageCount) {
            meta.setDisplayName(Residence.getInstance().msg(lm.General_NextInfoPage));
            Item.setItemMeta(meta);
            GuiInv.setItem(53, Item);
        }

        this.inventory = GuiInv;
    }
}
