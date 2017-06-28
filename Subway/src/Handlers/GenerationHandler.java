package Handlers;

import java.io.File;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GenerationHandler
{
  private static Player player;
  private static File schematic;
  private static Location location;
  private static int radius;
  private static boolean active = false;
  private static String name;
  private static boolean custom = false;
  
  public static boolean SetGeneration(Player player, File schematic, Location location, int radius)
  {
    if (player == null) {
      return false;
    }
    GenerationHandler.player = player;
    GenerationHandler.schematic = schematic;
    GenerationHandler.location = location;
    GenerationHandler.radius = radius;
    name = null;
    return true;
  }
  
  public static boolean SetGeneration(Player player, File schematic, Location location, String name)
  {
    if (player == null) {
      return false;
    }
    GenerationHandler.player = player;
    GenerationHandler.schematic = schematic;
    GenerationHandler.location = location;
    radius = -1;
    GenerationHandler.name = name;
    custom = true;
    return true;
  }
  
  public static Player GetPlayer()
  {
    return player;
  }
  
  public static File GetSchematic()
  {
    return schematic;
  }
  
  public static Location GetLocation()
  {
    return location;
  }
  
  public static int GetRadius()
  {
    return radius;
  }
  
  public static String GetName()
  {
    return name;
  }
  
  public static boolean IsActive()
  {
    return active;
  }
  
  public static boolean IsCustom()
  {
    return custom;
  }
  
  public static void SetActive(boolean active)
  {
    GenerationHandler.active = active;
  }
  
  public static void ClearGeneration()
  {
    player = null;
    schematic = null;
    location = null;
    radius = -1;
    name = null;
    active = false;
    custom = false;
  }
}
