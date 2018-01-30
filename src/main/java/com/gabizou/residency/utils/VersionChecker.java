package com.gabizou.residency.utils;

import com.gabizou.residency.Residence;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class VersionChecker {

    Residence plugin;
    private int resource = 11480;
    private Version version = Version.v1_11_R1;

    public VersionChecker(Residence plugin) {
        this.plugin = plugin;
        this.version = getCurrent();
    }

    public static Version getCurrent() {
        String[] v = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
        String vv = v[v.length - 1];
        for (Version one : Version.values()) {
            if (one.name().equalsIgnoreCase(vv)) {
                return one;
            }
        }
        return null;
    }

    public Version getVersion() {
        return this.version;
    }

    public boolean isLower(Version version) {
        return this.version.getValue() < version.getValue();
    }

    public boolean isLowerEquals(Version version) {
        return this.version.getValue() <= version.getValue();
    }

    public boolean isHigher(Version version) {
        return this.version.getValue() > version.getValue();
    }

    public boolean isHigherEquals(Version version) {
        return this.version.getValue() >= version.getValue();
    }

    public void VersionCheck(final Player player) {
		if (!this.plugin.getConfigManager().versionCheck()) {
			return;
		}

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {
                String currentVersion = VersionChecker.this.plugin.getDescription().getVersion();
                String newVersion = getNewVersion();
				if (newVersion == null || newVersion.equalsIgnoreCase(currentVersion)) {
					return;
				}
                List<String> msg = Arrays.asList(
                    ChatColor.GREEN + "*********************** " + VersionChecker.this.plugin.getDescription().getName()
                    + " **************************",
                    ChatColor.GREEN + "* " + newVersion + " is now available! Your version: " + currentVersion,
                    ChatColor.GREEN + "* " + ChatColor.DARK_GREEN + VersionChecker.this.plugin.getDescription().getWebsite(),
                    ChatColor.GREEN + "************************************************************");
				for (String one : msg) {
					if (player != null) {
						player.sendMessage(one);
					} else {
						VersionChecker.this.plugin.consoleMessage(one);
					}
				}
            }
        });
    }

    public String getNewVersion() {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.getOutputStream()
                .write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=" + this.resource).getBytes("UTF-8"));
            String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
			if (version.length() <= 9) {
				return version;
			}
        } catch (Exception ex) {
            this.plugin
                .consoleMessage(ChatColor.RED + "Failed to check for " + this.plugin.getDescription().getName() + " update on spigot web page.");
        }
        return null;
    }

    public enum Version {
        v1_7_R1(),
        v1_7_R2(),
        v1_7_R3(),
        v1_7_R4(),
        v1_8_R1(),
        v1_8_R2(),
        v1_8_R3(),
        v1_9_R1(),
        v1_9_R2(),
        v1_10_R1(),
        v1_11_R1(),
        v1_11_R2(),
        v1_11_R3(),
        v1_12_R1(),
        v1_12_R2(),
        v1_12_R3(),
        v1_13_R1(),
        v1_13_R2(),
        v1_13_R3();

        private Integer value = null;
        private String shortVersion = null;

        public String getShortVersion() {
			if (this.shortVersion == null) {
				this.shortVersion = this.name().split("_R")[0];
			}
            return this.shortVersion;
        }

        public boolean isHigher(Version version) {
            return getValue() > version.getValue();
        }

        public Integer getValue() {
			if (this.value == null) {
				try {
					this.value = Integer.valueOf(this.name().replaceAll("[^\\d.]", ""));
				} catch (Exception e) {
				}
			}
            return this.value;
        }

        public boolean isLower(Version version) {
            return getValue() < version.getValue();
        }
    }

}
