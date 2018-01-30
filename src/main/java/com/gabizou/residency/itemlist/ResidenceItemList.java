package com.gabizou.residency.itemlist;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.ResidencePlayer;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.permissions.PermissionGroup;
import com.gabizou.residency.protection.ClaimedResidence;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;

public class ResidenceItemList extends ItemList {

    ClaimedResidence res;
    private Residence plugin;

    public ResidenceItemList(Residence plugin, ClaimedResidence parent, ListType type) {
        super(type);
        this.plugin = plugin;
        this.res = parent;
    }

    private ResidenceItemList(Residence plugin) {
        this.plugin = plugin;
    }

    public static ResidenceItemList load(Residence plugin, ClaimedResidence parent, Map<String, Object> map) {
        ResidenceItemList newlist = new ResidenceItemList(plugin);
        newlist.res = parent;
        return (ResidenceItemList) ItemList.load(map, newlist);
    }

    public void playerListChange(Player player, Material mat, boolean resadmin) {

        ResidencePlayer rPlayer = this.plugin.getPlayerManager().getResidencePlayer(player);
        PermissionGroup group = rPlayer.getGroup();
        if (resadmin || (this.res.getPermissions().hasResidencePermission(player, true) && group.itemListAccess())) {
            if (super.toggle(mat)) {
                this.plugin.msg(player, lm.General_ListMaterialAdd, mat.toString(), this.type.toString().toLowerCase());
            } else {
                this.plugin.msg(player, lm.General_ListMaterialRemove, mat.toString(), this.type.toString().toLowerCase());
            }
        } else {
            this.plugin.msg(player, lm.General_NoPermission);
        }
    }
}
