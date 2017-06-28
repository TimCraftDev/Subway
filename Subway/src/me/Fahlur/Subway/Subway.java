package me.Fahlur.Subway;

import Commands.GenerationCommand;
import Exceptions.PluginNotFoundException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import Handlers.StationHandler;
import Listeners.BlockListener;
import Listeners.PlayerListener;
import Util.Vector2;
import Util.Vector3;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Subway
  extends JavaPlugin
{
  Subway plugin;
  private Logger log;
  private WorldEditPlugin we;
  private WorldGuardPlugin wg;
  public static Permission permissions;
  
  public void onEnable()
  {
    this.log = getLogger();
    plugin = this;
    ConfigurationSerialization.registerClass(Station.class);
    ConfigurationSerialization.registerClass(Vector2.class);
    ConfigurationSerialization.registerClass(Vector3.class);
    try
    {
      this.we = ((WorldEditPlugin)getPlugin("WorldEdit"));
      this.wg = ((WorldGuardPlugin)getPlugin("WorldGuard"));
      
      
      if(!setupPermissions()){
    	  System.out.println("SubwaySystem: Whoops! It looks like I couldn't initialize Vault! This means that permissions won't work.");
      }
      
      getCommand("Subway").setExecutor(new GenerationCommand(this));
      PluginManager pm = getServer().getPluginManager();
      
      pm.registerEvents(new PlayerListener(plugin), plugin);
      pm.registerEvents(new BlockListener(plugin), plugin);
      
      StationHandler.LoadStation(plugin);
    }
    catch (PluginNotFoundException e)
    {
      this.log.log(Level.SEVERE, e.getMessage(), e);
      getServer().getPluginManager().disablePlugin(this);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public void onDisable() {}
  
  private Plugin getPlugin(String name)
    throws PluginNotFoundException
  {
    if (name == null) {
      throw new PluginNotFoundException(name);
    }
    Plugin p = getServer().getPluginManager().getPlugin(name);
    if (p == null) {
      throw new PluginNotFoundException(name);
    }
    return p;
  }
  
  private boolean setupPermissions()
  {
      RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
      if (permissionProvider != null) {
          permissions = permissionProvider.getProvider();
      }
      return (permissions != null);
  }
  
  public boolean hasPermission(Player player, String permission)
  {
	  return permissions.playerHas(player, permission);
  }
  
  public WorldEditPlugin getWorldEdit()
  {
    return this.we;
  }
  
  public WorldGuardPlugin getWorldGuard()
  {
    return this.wg;
  }
}
