package Util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Vector2
  implements ConfigurationSerializable
{
  private final int x;
  private final int z;
  
  public Vector2(int x, int z)
  {
    this.x = x;
    this.z = z;
  }
  
  public int getX()
  {
    return this.x;
  }
  
  public int getZ()
  {
    return this.z;
  }
  
  public boolean equals(Object object)
  {
    if (!(object instanceof Vector2)) {
      return false;
    }
    Vector2 compare = (Vector2)object;
    if ((this.x == compare.getX()) && (this.z == compare.getZ())) {
      return true;
    }
    return false;
  }
  
  public Map<String, Object> serialize()
  {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("x", Integer.valueOf(this.x));
    map.put("z", Integer.valueOf(this.z));
    
    return map;
  }
  
  public static Vector2 deserialize(Map<String, Object> map)
  {
    int x = ((Integer)map.get("x")).intValue();
    int z = ((Integer)map.get("z")).intValue();
    
    return new Vector2(x, z);
  }
}
