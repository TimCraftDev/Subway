package Util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Vector3
  implements ConfigurationSerializable
{
  private final String world;
  private final int x;
  private final int y;
  private final int z;
  
  public Vector3(String world, int x, int y, int z)
  {
    this.world = world;
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public Map<String, Object> serialize()
  {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("world", this.world);
    map.put("x", Integer.valueOf(this.x));
    map.put("y", Integer.valueOf(this.y));
    map.put("z", Integer.valueOf(this.z));
    return map;
  }
  
  public static Vector3 deserialize(Map<String, Object> map)
  {
    String world = (String)map.get("world");
    int x = ((Integer)map.get("x")).intValue();
    int y = ((Integer)map.get("y")).intValue();
    int z = ((Integer)map.get("z")).intValue();
    
    return new Vector3(world, x, y, z);
  }
  
  public String getWorld()
  {
    return this.world;
  }
  
  public int getX()
  {
    return this.x;
  }
  
  public int getY()
  {
    return this.y;
  }
  
  public int getZ()
  {
    return this.z;
  }
}
