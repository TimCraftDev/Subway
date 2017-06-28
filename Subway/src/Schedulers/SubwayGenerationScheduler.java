package Schedulers;

import com.sk89q.worldedit.data.DataException;
import API.TitleAPI;
import Handlers.GenerationHandler;
import Handlers.MessageHandler;
import Handlers.StationHandler;
import Runnables.SubwayGenerationRunnable;
import me.Fahlur.Subway.Subway;
import Util.Vector2;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class SubwayGenerationScheduler
  implements Runnable
{
  private final Subway plugin;
  private final Location start;
  private final Location receivingStart;
  private final World world;
  private final int radius;
  private int total;
  private final LocationGrid[] locations;
  private final File schematic;
  private int minimumWaitPeriod = 2000;
  private long timeAtLastGeneration = 0L;
  private int currentGenerationID = -1;
  private int count = 0;
  boolean generation = true;
  int newTotal;

  
  public SubwayGenerationScheduler(Subway plugin, File schematic, Location start, Location receivingStart, int radius)
  {
    this.plugin = plugin;
    this.schematic = schematic;
    this.start = start;
    this.receivingStart = receivingStart;
    this.world = start.getWorld();
    this.radius = radius;
    this.total = ((int)Math.pow(2 * radius + 1, 2.0D));
    this.locations = new LocationGrid[this.total];
    
    calculateLocations();
  }
  
  
  public void title(String title, String subTitle){
	  for(Player p : plugin.getServer().getOnlinePlayers()) {
			TitleAPI.sendFullTitle((Player) p, 500, 5 * 20, 500, title, subTitle);
	}
  }
  
  
  public void run()
  {

    if ((!this.plugin.getServer().getScheduler().isCurrentlyRunning(this.currentGenerationID)) && 
      (System.currentTimeMillis() - this.timeAtLastGeneration > this.minimumWaitPeriod))
    {
      if (this.count == this.total && generation == true)
      {
        try
        {
          StationHandler.SaveStations(this.plugin);
          MessageHandler.SendServerMessage(this.plugin, "&aSubway generation has completed.");
          title("&dSubway Generation is now Complete!", "&eThank you for your patience!");
          GenerationHandler.ClearGeneration();
          this.plugin.getServer().getScheduler().cancelTasks(this.plugin);
          generation = false;
          return;
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
        MessageHandler.SendServerMessage(this.plugin, "&aSubway generation has completed.");
        GenerationHandler.ClearGeneration();
        this.plugin.getServer().getScheduler().cancelTasks(this.plugin);
        generation = false;
        return;
      }
      newTotal = this.total;
      if (this.count <= (newTotal -= 1) && StationHandler.GetStationByVector(this.locations[this.count].getGridLocation()) != null) {
        this.count = findFirstNullStation();
      }
      if (this.count < this.total)
      {
        
    	/*  
    	DecimalFormat format = new DecimalFormat("##.##");
        String percent = format.format((this.count + 1) / this.total * 100.0D) + "%";
        */
    	DecimalFormat format = new DecimalFormat("##");
    	float perc = ((this.count+1) * 100.0f) / this.total;
    	String percent = format.format(perc) + "%";
    	title("&dSubway Generation in Process!", "&ePlease review chat for current status!");
    	
    	
        MessageHandler.SendServerMessage(this.plugin, 
          "Subway Generation: &f" + (this.count + 1) + "/" + 
          this.total + " &7(&e" + percent + "&7)");
        try
        {
          this.currentGenerationID = this.plugin
            .getServer()
            .getScheduler()
            .scheduleSyncDelayedTask(
            this.plugin, 
            new SubwayGenerationRunnable(
            this.plugin, 
            this.schematic, 
            "Station-" + 
            this.locations[this.count]
            .getGridLocation()
            .getX() + 
            "," + 
            this.locations[this.count]
            .getGridLocation()
            .getZ() + "", 
            this.world, 
            this.locations[this.count]
            .getLocation(), 
            this.locations[this.count]
            .getReceiving(), 
            this.locations[this.count]
            .getGridLocation(), 
            false));
          StationHandler.SaveStations(this.plugin);
        }
        catch (DataException|IOException e)
        {
          e.printStackTrace();
        }
        this.timeAtLastGeneration = System.currentTimeMillis();
        this.count += 1;
      }
    }
  }
  
  private int findFirstNullStation()
  {
    for (int i = 0; i < this.total; i++) {
      if (StationHandler.GetStationByVector(this.locations[i]
        .getGridLocation()) == null) {
        return i;
      }
    }
    return this.total;
  }
  
  private void calculateLocations()
  {
    int count = 0;
    for (int i = 0; i <= this.radius; i++) {
      for (int x = -i; x <= i; x++) {
        for (int z = -i; z <= i; z++) {
          if ((x == i) || (x == -i) || (z == i) || (z == -i))
          {
            Location location = new Location(this.start.getWorld(), 
              this.start.getBlockX() + x * 1000, 
              this.start.getBlockY(), this.start.getBlockZ() + 
              z * 1000);
            Location receiving = new Location(
              this.receivingStart.getWorld(), 
              this.receivingStart.getBlockX() + x * 1000, 
              this.receivingStart.getBlockY(), 
              this.receivingStart.getBlockZ() + z * 1000);
            
            this.locations[count] = new LocationGrid(location, 
              receiving, new Vector2(x, -z));
            count++;
          }
        }
      }
    }
  }
}
