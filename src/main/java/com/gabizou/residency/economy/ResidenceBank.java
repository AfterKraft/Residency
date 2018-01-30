package com.gabizou.residency.economy;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.Flags;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.protection.ClaimedResidence;
import com.gabizou.residency.protection.FlagPermissions.FlagCombo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResidenceBank {

    Double storedMoney;
    ClaimedResidence res;

    public ResidenceBank(ClaimedResidence parent) {
        this.storedMoney = 0D;
        this.res = parent;
    }

    @Deprecated
    public int getStoredMoney() {
        return this.storedMoney.intValue();
    }

    public void setStoredMoney(double amount) {
        this.storedMoney = amount;
    }

    public Double getStoredMoneyD() {
        return this.storedMoney;
    }

    public String getStoredMoneyFormated() {
        try {
            return Residence.getInstance().getEconomyManager().format(this.storedMoney);
        } catch (Exception e) {
            return String.valueOf(this.storedMoney);
        }
    }

    @Deprecated
    public void withdraw(CommandSender sender, int amount, boolean resadmin) {
        withdraw(sender, (double) amount, resadmin);
    }

    public void withdraw(CommandSender sender, double amount, boolean resadmin) {
        if (!(sender instanceof Player)) {
            return;
        }
        Player player = (Player) sender;
        if (!Residence.getInstance().getConfigManager().enableEconomy()) {
            Residence.getInstance().msg(sender, lm.Economy_MarketDisabled);
        }
        if (!resadmin && !this.res.getPermissions().playerHas(player, Flags.bank, FlagCombo.OnlyTrue)) {
            Residence.getInstance().msg(sender, lm.Bank_NoAccess);
            return;
        }
        if (!hasEnough(amount)) {
            Residence.getInstance().msg(sender, lm.Bank_NoMoney);
            return;
        }
        if (sender instanceof Player && Residence.getInstance().getEconomyManager().add(sender.getName(), amount) || !(sender instanceof Player)) {
            this.subtract(amount);
            Residence.getInstance().msg(sender, lm.Bank_Withdraw, String.format("%d", amount));
        }
    }

    public boolean hasEnough(double amount) {
        if (this.storedMoney >= amount) {
            return true;
        }
        return false;
    }

    public void subtract(double amount) {
        this.storedMoney = this.storedMoney - amount;
        if (this.storedMoney < 0) {
            this.storedMoney = 0D;
        }
    }

    @Deprecated
    public void deposit(CommandSender sender, int amount, boolean resadmin) {
        deposit(sender, (double) amount, resadmin);
    }

    public void deposit(CommandSender sender, double amount, boolean resadmin) {
        if (!(sender instanceof Player)) {
            return;
        }
        Player player = (Player) sender;
        if (!Residence.getInstance().getConfigManager().enableEconomy()) {
            Residence.getInstance().msg(sender, lm.Economy_MarketDisabled);
        }
        if (!resadmin && !this.res.getPermissions().playerHas(player, Flags.bank, FlagCombo.OnlyTrue)) {
            Residence.getInstance().msg(sender, lm.Bank_NoAccess);
            return;
        }
        if (sender instanceof Player && !Residence.getInstance().getEconomyManager().canAfford(sender.getName(), amount)) {
            Residence.getInstance().msg(sender, lm.Economy_NotEnoughMoney);
            return;
        }
        if (sender instanceof Player && Residence.getInstance().getEconomyManager().subtract(sender.getName(), amount)
            || !(sender instanceof Player)) {
            this.add(amount);
            Residence.getInstance().msg(sender, lm.Bank_Deposit, Residence.getInstance().getEconomyManager().format(amount));
        }
    }

    public void add(double amount) {
        this.storedMoney = this.storedMoney + amount;
    }
}
