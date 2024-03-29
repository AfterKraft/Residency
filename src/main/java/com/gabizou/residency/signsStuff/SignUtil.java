package com.gabizou.residency.signsStuff;

import com.gabizou.residency.CommentedYamlConfiguration;
import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.economy.rent.RentedLand;
import com.gabizou.residency.protection.ClaimedResidence;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

public class SignUtil {

    public SignInfo Signs = new SignInfo();
    private Residence plugin;

    public SignUtil(Residence plugin) {
        this.plugin = plugin;
    }

    public int updateAllSigns() {
        List<Signs> temp = new ArrayList<Signs>();
        temp.addAll(this.Signs.GetAllSigns());
        for (Signs one : temp) {
            SignUpdate(one);
        }
        saveSigns();
        return temp.size();
    }

    public boolean SignUpdate(Signs Sign) {

        ClaimedResidence res = Sign.GetResidence();

        if (res == null) {
            return false;
        }

        boolean ForSale = res.isForSell();
        boolean ForRent = res.isForRent();

        Location nloc = Sign.GetLocation();

        if (nloc == null) {
            this.Signs.removeSign(Sign);
            return false;
        }

        Block block = nloc.getBlock();

        if (block.getType() == Material.AIR) {
            this.Signs.removeSign(Sign);
            return false;
        }

        if (!(block.getState() instanceof Sign)) {
            return false;
        }

        Sign sign = (Sign) block.getState();

        if (!ForRent && !ForSale) {
            block.breakNaturally();
            this.Signs.removeSign(Sign);
            return true;
        }
        String landName = res.getName();

        if (landName == null) {
            return false;
        }
        if (ForRent) {

            boolean rented = res.isRented();

            RentedLand rentedPlace = res.getRentedLand();
            long time = 0L;
            if (rentedPlace != null) {
                time = rentedPlace.endTime;
            }

            SimpleDateFormat formatter = new SimpleDateFormat(this.plugin.msg(lm.Sign_DateFormat));
            formatter.setTimeZone(TimeZone.getTimeZone(this.plugin.getConfigManager().getTimeZone()));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            String timeString = formatter.format(calendar.getTime());

            String endDate = timeString;
            if (time == 0L) {
                endDate = "Unknown";
            }

            if (this.plugin.getRentManager().getRentedAutoRepeats(res)) {
                endDate = this.plugin.msg(lm.Sign_RentedAutorenewTrue, endDate);
            } else {
                endDate = this.plugin.msg(lm.Sign_RentedAutorenewFalse, endDate);
            }

            String TopLine = rented ? endDate : this.plugin.msg(lm.Sign_ForRentTopLine);
            sign.setLine(0, TopLine);

            String infoLine = this.plugin.msg(lm.Sign_ForRentPriceLine, this.plugin.getRentManager().getCostOfRent(res), this.plugin
                .getRentManager().getRentDays(res), this.plugin.getRentManager().getRentableRepeatable(res));

            sign.setLine(1, infoLine);
            String shortName = fixResName(landName);
            sign.setLine(2, rented ? this.plugin.msg(lm.Sign_RentedResName, shortName)
                                   : this.plugin.msg(lm.Sign_RentedResName, shortName));
            sign.setLine(3, rented ? this.plugin.msg(lm.Sign_RentedBottomLine, this.plugin.getRentManager().getRentingPlayer(landName))
                                   : this.plugin.msg(lm.Sign_ForRentBottomLine));
            sign.update();
        }

        if (ForSale) {
            String shortName = fixResName(landName);
            String secondLine = null;
            if (shortName.contains("~")) {
                String[] lines = fixDoubleResName(landName);
                shortName = lines[0];
                secondLine = lines[1];
            }

            sign.setLine(0, this.plugin.msg(lm.Sign_ForSaleTopLine));
            String infoLine = this.plugin.msg(lm.Sign_ForSalePriceLine, res.getSellPrice());
            sign.setLine(1, infoLine);
            sign.setLine(2, this.plugin.msg(lm.Sign_RentedResName, shortName));

            if (secondLine != null) {
                sign.setLine(3, this.plugin.msg(lm.Sign_RentedResName, secondLine));
            } else {
                sign.setLine(3, this.plugin.msg(lm.Sign_ForSaleBottomLine));
            }
            sign.update();
        }

        return true;
    }

    // Signs save file
    public void saveSigns() {

        File f = new File(this.plugin.getDataFolder(), "Signs.yml");
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

        CommentedYamlConfiguration writer = new CommentedYamlConfiguration();
        conf.options().copyDefaults(true);

        writer.addComment("Signs", "DO NOT EDIT THIS FILE BY HAND!");

        if (!conf.isConfigurationSection("Signs")) {
            conf.createSection("Signs");
        }

        for (Signs one : this.Signs.GetAllSigns()) {
            String path = "Signs." + String.valueOf(one.GetCategory());
            writer.set(path + ".Residence", one.GetResidence().getName());
            writer.set(path + ".World", one.GetLocation().getWorld().getName());
            writer.set(path + ".X", one.GetLocation().getBlockX());
            writer.set(path + ".Y", one.GetLocation().getBlockY());
            writer.set(path + ".Z", one.GetLocation().getBlockZ());
        }

        try {
            writer.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    public String fixResName(String name) {
        if (name.length() > 15 && !name.contains(".")) {
            name = "~" + name.substring(name.length() - 14);
        } else if (name.length() > 15 && name.contains(".")) {
            String[] splited = name.split("\\.");
            name = "";
            for (int i = 0; i < splited.length; i++) {
                String tempName = name + "." + splited[i];
                if (tempName.length() < 15) {
                    name = tempName;
                } else {
                    name = "~" + tempName.substring(tempName.length() - 14);
                }
            }
        }
        return name;
    }

    public String[] fixDoubleResName(String name) {
        String SecondLine = name.substring(name.length() - 15);
        String FirstLine = name.replace(SecondLine, "");
        if (FirstLine.length() > 15 && !FirstLine.contains(".")) {
            FirstLine = "~" + FirstLine.substring(name.length() - 14);
        } else if (FirstLine.length() > 15 && FirstLine.contains(".")) {
            String[] splited = FirstLine.split("\\.");
            FirstLine = "";
            for (int i = 0; i < splited.length; i++) {
                String tempName = FirstLine + "." + splited[i];
                if (tempName.length() < 15) {
                    FirstLine = tempName;
                } else {
                    FirstLine = "~" + tempName.substring(tempName.length() - 14);
                }
            }
        }
        String[] lines = new String[2];
        lines[0] = FirstLine;
        lines[1] = SecondLine;
        return lines;
    }

    // Sign file
    public void LoadSigns() {

        this.Signs.GetAllSigns().clear();
        File file = new File(this.plugin.getDataFolder(), "Signs.yml");
        YamlConfiguration f = YamlConfiguration.loadConfiguration(file);

        if (!f.isConfigurationSection("Signs")) {
            return;
        }

        ConfigurationSection ConfCategory = f.getConfigurationSection("Signs");
        ArrayList<String> categoriesList = new ArrayList<String>(ConfCategory.getKeys(false));
        if (categoriesList.size() == 0) {
            return;
        }
        for (String category : categoriesList) {
            ConfigurationSection NameSection = ConfCategory.getConfigurationSection(category);
            Signs newTemp = new Signs();
            newTemp.setCategory(Integer.valueOf(category));

            ClaimedResidence res = this.plugin.getResidenceManager().getByName(NameSection.getString("Residence"));

            if (res == null) {
                continue;
            }

            newTemp.setResidence(res);

            World w = Bukkit.getWorld(NameSection.getString("World"));

            if (w == null) {
                continue;
            }

            double x = NameSection.getDouble("X");
            double y = NameSection.getDouble("Y");
            double z = NameSection.getDouble("Z");

            Location loc = new Location(w, x, y, z);
            newTemp.setLocation(loc);
            this.Signs.addSign(newTemp);
        }
        return;
    }

    public Signs getSignFromLoc(Location loc) {
        if (loc == null) {
            return null;
        }
        List<Signs> signList = new ArrayList<Signs>();
        signList.addAll(this.getSigns().GetAllSigns());
        for (Signs one : signList) {
            if (one == null) {
                continue;
            }
            if (one.GetLocation() == null) {
                continue;
            }
            if (one.GetLocation().getWorld() == null) {
                continue;
            }
            if (!one.GetLocation().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())) {
                continue;
            }
            if (one.GetLocation().getBlockX() != loc.getBlockX()) {
                continue;
            }
            if (one.GetLocation().getBlockY() != loc.getBlockY()) {
                continue;
            }
            if (one.GetLocation().getBlockZ() != loc.getBlockZ()) {
                continue;
            }
            return one;
        }
        return null;
    }

    public SignInfo getSigns() {
        return this.Signs;
    }

    public void CheckSign(final ClaimedResidence res, int time) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
            @Override
            public void run() {
                CheckSign(res);
            }
        }, time * 1L);
    }

    public void CheckSign(ClaimedResidence res) {
        List<Signs> signList = new ArrayList<Signs>();
        signList.addAll(this.getSigns().GetAllSigns());
        for (Signs one : signList) {
            if (res != one.GetResidence()) {
                continue;
            }
            this.SignUpdate(one);
        }
        saveSigns();
    }

    public void removeSign(ClaimedResidence res) {
        if (res != null) {
            removeSign(res.getName());
        }
    }

    public void removeSign(String res) {
        List<Signs> signList = new ArrayList<Signs>();
        signList.addAll(this.getSigns().GetAllSigns());

        for (Signs one : signList) {
            if (!res.equals(one.GetResidence())) {
                continue;
            }
            this.SignUpdate(one);
        }
    }

    public void updateSignResName(ClaimedResidence res) {
        for (Signs one : this.getSigns().GetAllSigns()) {
            if (res != one.GetResidence()) {
                continue;
            }
            this.SignUpdate(one);
            saveSigns();
            break;
        }
    }

    public void convertSigns(CommandSender sender) {
        File file = new File("plugins/ResidenceSigns/signs.yml");
        if (!file.exists()) {
            sender.sendMessage(ChatColor.GOLD + "Can't find ResidenceSign file");
            return;
        }
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);

        if (!conf.contains("signs")) {
            sender.sendMessage(ChatColor.GOLD + "Incorrect format of signs file");
            return;
        }

        Set<String> sectionname = conf.getConfigurationSection("signs").getKeys(false);
        ConfigurationSection section = conf.getConfigurationSection("signs");

        int category = 1;
        if (this.getSigns().GetAllSigns().size() > 0) {
            category = this.getSigns().GetAllSigns().get(this.getSigns().GetAllSigns().size() - 1).GetCategory() + 1;
        }

        long time = System.currentTimeMillis();

        int i = 0;
        for (String one : sectionname) {
            Signs signs = new Signs();
            String resname = section.getString(one + ".resName");
            signs.setCategory(category);

            ClaimedResidence res = this.plugin.getResidenceManager().getByName(resname);

            if (res == null) {
                continue;
            }

            signs.setResidence(res);

            List<String> loc = section.getStringList(one + ".loc");

            if (loc.size() != 4) {
                continue;
            }

            World world = Bukkit.getWorld(loc.get(0));
            if (world == null) {
                continue;
            }

            int x = 0;
            int y = 0;
            int z = 0;

            try {
                x = Integer.parseInt(loc.get(1));
                y = Integer.parseInt(loc.get(2));
                z = Integer.parseInt(loc.get(3));
            } catch (Exception ex) {
                continue;
            }

            signs.setLocation(new Location(world, x, y, z));
            boolean found = false;

            for (Signs onesigns : this.getSigns().GetAllSigns()) {
                if (!onesigns.GetLocation().getWorld().getName().equalsIgnoreCase(signs.GetLocation().getWorld().getName())) {
                    continue;
                }
                if (onesigns.GetLocation().getBlockX() != signs.GetLocation().getBlockX()) {
                    continue;
                }
                if (onesigns.GetLocation().getBlockY() != signs.GetLocation().getBlockY()) {
                    continue;
                }
                if (onesigns.GetLocation().getBlockZ() != signs.GetLocation().getBlockZ()) {
                    continue;
                }
                found = true;
            }

            if (found) {
                continue;
            }

            Location nloc = signs.GetLocation();
            Block block = nloc.getBlock();

            if (!(block.getState() instanceof Sign)) {
                continue;
            }

            this.getSigns().addSign(signs);
            this.SignUpdate(signs);
            category++;
            i++;
        }

        this.saveSigns();

        sender
            .sendMessage(ChatColor.GOLD + "" + i + ChatColor.YELLOW + " signs have being converted to new format! It took " + ChatColor.GOLD + (System
                                                                                                                                                    .currentTimeMillis()
                                                                                                                                                - time)
                         + ChatColor.YELLOW + " ms!");
    }
}
