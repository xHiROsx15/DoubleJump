package com.nova.code.doublejump;

//////////////////////////////////////////////
//Created by NOVA Development Team
//Copyright 2013 All Rigths Reserved
//
//Developers:
//HiROs15
//Cheesie112
//Devilx10
//
// Want to join the NOVA Development Team?
// Just give one of us a message.
//////////////////////////////////////////////


import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.h31ix.anticheat.api.AnticheatAPI;
import net.h31ix.anticheat.manage.CheckType;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DoubleJump extends JavaPlugin
  implements Listener
{
  private final Map<Player, Long> jumps = new HashMap<Player, Long>();

  public void onEnable()
  {
    getServer().getPluginManager().registerEvents(this, this);
    makedir();
    createConfigFile();
    checkAdvertConfig();
  }
public void makedir() {
	new File("plugins/").mkdir();
	new File("plugins/DoubleJump/").mkdir();
	new File("plugins/DoubleJump/settings/").mkdir();
}
//Config.yml
String configFileName = "settings.yml";
File configFile = new File("plugins/DoubleJump/settings/" + configFileName);
FileConfiguration configConfig = YamlConfiguration.loadConfiguration(configFile);

public void createConfigFile() {
	if(!configFile.exists()) {
		try {
			configFile.createNewFile();
			setupConfig();
		} catch(Exception ex) {}
	}
}
public FileConfiguration getConfigConfig() {
	return configConfig;
}
public void reloadConfigConfig() {
	InputStream defConfigStream = this.getResource(configFileName);
	if(defConfigStream != null) {
		YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
		configConfig.setDefaults(defConfig);
	}
}

String advert = "none";
boolean advertenabled = false;

public void setupConfig() {
	this.getConfigConfig().set("enabled", false);
	this.getConfigConfig().set("ops-perms", false);
	this.getConfigConfig().set("advert-enabled", advertenabled);
	this.getConfigConfig().set("ad", advert);
	try {
		this.getConfigConfig().save(configFile);
	} catch(Exception ex) {}
}
public void checkAdvertConfig() {
	if(!(this.getConfigConfig().getString("ad") == advert)) {
		this.getConfigConfig().set("ad", advert);
		try {
			this.getConfigConfig().save(configFile);
		} catch(Exception ex) {}
		this.getLogger().info("Advert has been updated! Please do not change this!");
	}
	if(!(this.getConfigConfig().getBoolean("advert-enabled") == advertenabled)) {
		this.getConfigConfig().set("advert-enabled", advertenabled);
		try {
			this.getConfigConfig().save(configFile);
		} catch(Exception ex) {}
	}
}
//End

  private boolean hasPermission(Player player, String permission) {
    return (player.isOp()) || (player.hasPermission(permission));
  }

  @EventHandler
  public strictfp void onPlayerMove(PlayerMoveEvent event) {
    if (event.getTo().getY() <= event.getFrom().getY()) {
      Player player = event.getPlayer();
      if (this.jumps.get(player) == null) this.jumps.put(player, Long.valueOf(System.currentTimeMillis()));
      if ((player.getFoodLevel() > 6) && (hasPermission(player, "doublejump.use")) && (System.currentTimeMillis() - ((Long)this.jumps.get(player)).longValue() > 750L) && 
        (player.getVelocity().getY() < -0.5D) && (player.isSneaking())) {
        if (!hasPermission(player, "doublejump.nohunger")) {
          player.setFoodLevel(player.getFoodLevel() - 1);
        }
        if (Bukkit.getServer().getPluginManager().getPlugin("AntiCheat") != null) {
          AnticheatAPI.exemptPlayer(player, CheckType.FLY);
        }
        player.setVelocity(player.getVelocity().setY(0.7F));
        player.setFallDistance(player.getFallDistance() - 0.7F);
        this.jumps.put(player, Long.valueOf(System.currentTimeMillis()));
        if (Bukkit.getServer().getPluginManager().getPlugin("AntiCheat") != null)
          AnticheatAPI.unexemptPlayer(player, CheckType.FLY);
      }
    }
  }
}
