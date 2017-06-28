package me.Fahlur.Subway;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import Util.Vector2;
import Util.Vector3;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.Vector;

public class Station
  implements ConfigurationSerializable
{
  private final Vector2 grid;
  private final Vector3 receiving;
  private final String region;
  private final boolean custom;
  private Vector velocity;
  private String code;
  private Location receivingLocation;
  private ProtectedRegion protectedRegion;
  private boolean operational = true;
  
  public Station(String region, Vector2 grid, Vector3 receiving, String code, boolean custom)
  {
    this.region = region;
    this.grid = grid;
    this.receiving = receiving;
    this.code = code;
    this.custom = custom;
  }
  
  public void initialize(Subway plugin)
  {
    World world = plugin.getServer().getWorld(this.receiving.getWorld());
    this.receivingLocation = new Location(world, this.receiving.getX(), 
      this.receiving.getY(), this.receiving.getZ());
    
    this.protectedRegion = plugin.getWorldGuard().getRegionManager(world)
      .getRegion(this.region);
    if ((this.protectedRegion == null) || (this.receivingLocation == null)) {
      this.operational = false;
    }
    if (verifyReceivingLocation()) {
      setVelocity();
    }
  }
  
  private boolean verifyReceivingLocation()
  {
    Block block = this.receivingLocation.getWorld().getBlockAt(
      this.receivingLocation);
    if (!block.getType().equals(Material.POWERED_RAIL)) {
      return false;
    }
    return true;
  }
  
  private boolean setVelocity()
  {
    Block receivingBlock = this.receivingLocation.getWorld().getBlockAt(
      new Location(this.receivingLocation.getWorld(), 
      this.receivingLocation.getX(), this.receivingLocation
      .getY(), this.receivingLocation.getZ()));
    
    Location[] locations = {
      receivingBlock.getRelative(1, 0, 0).getLocation(), 
      receivingBlock.getRelative(-1, 0, 0).getLocation(), 
      receivingBlock.getRelative(0, 0, 1).getLocation(), 
      receivingBlock.getRelative(0, 0, -1).getLocation() };
    for (Location location : locations) {
      if (location.getBlock().getType().equals(Material.POWERED_RAIL))
      {
        this.velocity = location.toVector().subtract(
          this.receivingLocation.toVector());
        return true;
      }
    }
    return false;
  }
  
  public void setCode(String code)
  {
    this.code = code;
  }
  
  public ProtectedRegion getRegion()
  {
    return this.protectedRegion;
  }
  
  public Vector2 getGrid()
  {
    return this.grid;
  }
  
  public Location getReceiving()
  {
    return this.receivingLocation;
  }
  
  public Vector getVelocity()
  {
    return this.velocity;
  }
  
  public boolean isOperational()
  {
    return this.operational;
  }
  
  public String getCode()
  {
    return this.code;
  }
  
  public boolean isCustom()
  {
    return this.custom;
  }
  
  public Map<String, Object> serialize()
  {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("custom", Boolean.valueOf(this.custom));
    map.put("code", this.code);
    map.put("region", this.region);
    map.put("grid", this.grid.serialize());
    map.put("receiving", this.receiving.serialize());
    return map;
  }
  
  public static Station deserialize(Map<String, Object> map)
  {
    String region = (String)map.get("region");
    String code = map.get("code") != null ? (String)map.get("code") : "-1";
    boolean custom = map.get("custom") != null ? 
      ((Boolean)map.get("custom")).booleanValue() : false;
    

	@SuppressWarnings({ "unchecked", "rawtypes" }) //TODO
	Vector2 grid = Vector2.deserialize((Map)map.get("grid"));
    

    @SuppressWarnings({ "unchecked", "rawtypes" }) //TODO
	Vector3 receiving = Vector3.deserialize((Map)map.get("receiving"));
    
    return new Station(region, grid, receiving, code, custom);
  }
}
