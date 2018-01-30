package com.gabizou.residency.gui;

import com.gabizou.residency.CommentedYamlConfiguration;
import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.ConfigReader;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Set;

public class FlagUtil {

    private FlagData flagData = new FlagData();
    private Residence plugin;

    public FlagUtil(Residence plugin) {
        this.plugin = plugin;
    }

    public void load() {
        File f = new File(this.plugin.getDataFolder(), "flags.yml");
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);
        CommentedYamlConfiguration writer = new CommentedYamlConfiguration();
        conf.options().copyDefaults(true);

        ConfigReader c = new ConfigReader(conf, writer);

		if (!conf.isConfigurationSection("Global.FlagPermission")) {
			return;
		}

        Set<String> allFlags = conf.getConfigurationSection("Global.FlagPermission").getKeys(false);

        for (String oneFlag : allFlags) {
			if (!c.getC().contains("Global.FlagGui." + oneFlag)) {
				continue;
			}

			if (!c.getC().contains("Global.FlagGui." + oneFlag + ".Id")) {
				continue;
			}

			if (!c.getC().contains("Global.FlagGui." + oneFlag + ".Data")) {
				continue;
			}

            int id = c.get("Global.FlagGui." + oneFlag + ".Id", 35);
            int data = c.get("Global.FlagGui." + oneFlag + ".Data", 0);

            @SuppressWarnings("deprecation")
            Material Mat = Material.getMaterial(id);
			if (Mat == null) {
				Mat = Material.STONE;
			}
            ItemStack item = new ItemStack(Mat, 1, (short) data);
            this.flagData.addFlagButton(oneFlag.toLowerCase(), item);
        }
    }

    public FlagData getFlagData() {
        return this.flagData;
    }
}
