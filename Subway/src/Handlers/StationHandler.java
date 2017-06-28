package Handlers;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.Fahlur.Subway.Station;
import me.Fahlur.Subway.Subway;
import Util.Vector2;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class StationHandler
{
  private static List<Station> stations = new ArrayList<Station>();
  
  public static void AddStation(Station station)
  {
    if (!stations.contains(station)) {
      stations.add(station);
    }
  }
  
  public static void RemoveStation(Station station)
  {
    if (stations.contains(station)) {
      stations.remove(station);
    }
  }
  
  public static Station GetStationByVector(Vector2 gridLocation)
  {
    for (Station station : stations) {
      if (station.getGrid().equals(gridLocation)) {
        return station;
      }
    }
    return null;
  }
  
  public static void LoadStation(Subway plugin)
    throws IOException
  {
    if (!plugin.getDataFolder().exists()) {
      plugin.getDataFolder().mkdir();
    }
    File save = new File(plugin.getDataFolder(), "stations.yml");
    if (!save.exists()) {
      save.createNewFile();
    }
    FileConfiguration config = YamlConfiguration.loadConfiguration(save);
    
    Set<String> keys = config.getKeys(true);
    for (String key : keys)
    {
      Station station = (Station)config.get(key);
      station.initialize(plugin);
      AddStation(station);
    }
  }
  
  public static void SaveStations(Subway plugin)
    throws IOException
  {
    if (!plugin.getDataFolder().exists()) {
      plugin.getDataFolder().mkdir();
    }
    File save = new File(plugin.getDataFolder(), "stations.yml");
    save.delete();
    save.createNewFile();
    FileConfiguration config = YamlConfiguration.loadConfiguration(save);
    for (Station station : stations) {
      config.set(station.getRegion().getId(), station);
    }
    config.save(save);
  }
  
  public static List<Station> GetCodedStations()
  {
    List<Station> toReturn = new ArrayList<Station>();
    for (Station station : stations) {
      if (!station.getCode().equalsIgnoreCase("-1")) {
        toReturn.add(station);
      }
    }
    return toReturn;
  }
  
  public static Station GetStationByRegion(ProtectedRegion region)
  {
    for (Station station : stations) {
      if (station.getRegion().equals(region)) {
        return station;
      }
    }
    return null;
  }
  
  public static Station GetStationByCode(String code)
  {
    for (Station station : stations) {
      if (station.getCode().equalsIgnoreCase(code)) {
        return station;
      }
    }
    return null;
  }
  
  public static Station GetStationByName(String name)
  {
    for(Station station: stations){
    	if(station.getRegion().getId().contains(name)){
    		return station;
    	}
    }
    return null;
  }
  
  public static Station GetStationByLocation(Location location)
  {
    for (Station station : stations) {
      if (station.getRegion().contains(location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
        return station;
      }
    }
    return null;
  }
  
  public static Station GetNearestStation(Location location)
  {
    double closestDistance = -1.0D;
    Station closest = null;
    for (Station station : stations) {
      if (!station.isCustom()) {
    	  if (station.getReceiving().getWorld().getName() == location.getWorld().getName()){
    		  if (closest == null)
    	        {
    	          closest = station;
    	           closestDistance = location.distance(station.getReceiving());
    	          
    	        }
    	        else if (location.distance(station.getReceiving()) < closestDistance)
    	        {
    	          
    	          closest = station;
    	          closestDistance = location.distance(station.getReceiving());
    	        }  
    	  }
        
      }
    }
    return closest;
  }
}
