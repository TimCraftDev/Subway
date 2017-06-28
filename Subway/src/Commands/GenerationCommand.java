package Commands;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import Handlers.GenerationHandler;
import Handlers.MessageHandler;
import Handlers.StationHandler;
import me.Fahlur.Subway.Station;
import me.Fahlur.Subway.Subway;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class GenerationCommand
  implements CommandExecutor
{
  private final Subway plugin;
  
  public GenerationCommand(Subway plugin)
  {
    this.plugin = plugin;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (!(sender instanceof Player)) {
      MessageHandler.SendErrorMessage(sender, 
        "Sorry, you must be ingame to complete this command.", 
        false);
    }
    Player player = (Player)sender;
    if (args.length < 1)
    {
      MessageHandler.SendErrorMessage(sender, 
        "You formatted the command incorrectly", false);
      return false;
    }
    if (args[0].equalsIgnoreCase("search")) {
      return searchCommand(player, command, label, args);
    }
    if (args[0].equalsIgnoreCase("generate")) {
      return generateCommand(player, command, label, args);
    }
    if (args[0].equalsIgnoreCase("custom")) {
      return customGenerateCommand(player, command, label, args);
    }
    if (args[0].equalsIgnoreCase("remove")) {
      return removeCommand(player, command, label, args);
    }
    if (args[0].equalsIgnoreCase("code")) {
      return codeCommand(player, command, label, args);
    }
    if (args[0].equalsIgnoreCase("list")) {
      return listCommand(player, command, label, args);
    }
    if (args[0].equalsIgnoreCase("help")) {
      return helpCommand(player, command, label, args);
    }
    return false;
  }
  
  private boolean helpCommand(Player player, Command command, String label, String[] args)
  {
    if (!this.plugin.hasPermission(player, "SubwaySystem.Help"))
    {
      MessageHandler.SendErrorMessage(player, 
        "Sorry, you do not have permission to do this.", false);
      return true;
    }
    if (args.length > 1)
    {
      MessageHandler.SendErrorMessage(player, 
        "You formatted the command incorrectly", false);
      return false;
    }
    MessageHandler.SendMessage(player, "Subway Help: ");
    
    MessageHandler.SendMessage(
      player, 
      "/subway search <region> allows you to search for the closest station to a given WorldGuard protection. (Case-Sensitive)");
    
    MessageHandler.SendMessage(
      player, 
      "/subway list shows you all the stations that have been given unique codes to enter in at any given station.");
    
    MessageHandler.SendMessage(
      player, 
      "To go to a coded station, press the corresponding digits in the departure bay.");
    
    MessageHandler.SendMessage(player, 
      "If at any point you wish to reset your destination, press the reset button.");
    
    return true;
  }
  
  private boolean listCommand(Player player, Command command, String label, String[] args)
  {
    if (!this.plugin.hasPermission(player, "SubwaySystem.List"))
    {
      MessageHandler.SendErrorMessage(player, 
        "Sorry, you do not have permission to do this.", false);
      return true;
    }
    if (args.length > 1)
    {
      MessageHandler.SendErrorMessage(player, 
        "You formatted the command incorrectly", false);
      return false;
    }
    List<Station> stations = StationHandler.GetCodedStations();
    if (stations.size() == 0) {
      MessageHandler.SendErrorMessage(player, 
        "Sorry, there are no coded stations.", false);
    } else {
      MessageHandler.SendMessage(player, "Coded Stations: ");
    }
    for (Station station : stations) {
      MessageHandler.SendMessage(player, 
        "Station: " + station.getRegion().getId() + " Code: " + 
        station.getCode());
    }
    return true;
  }
  
  private boolean codeCommand(Player player, Command command, String label, String[] args)
  {
    if (!this.plugin.hasPermission(player, "SubwaySystem.Code"))
    {
      MessageHandler.SendErrorMessage(player, 
        "Sorry, you do not have permission to do this.", false);
      return true;
    }
    if (args.length < 3)
    {
      MessageHandler.SendErrorMessage(player, 
        "You formatted the command incorrectly", false);
      return false;
    }
    if (StationHandler.GetStationByName(args[1]) == null)
    {
      MessageHandler.SendErrorMessage(player, 
        "No station exists with that name.", false);
      return true;
    }
    if (args[2].length() > 3)
    {
      MessageHandler.SendErrorMessage(player, 
        "The code must be less than 3 digits in length.", false);
      return true;
    }
    if (StationHandler.GetStationByCode(args[2]) != null)
    {
      MessageHandler.SendErrorMessage(player, 
        "A station already exists with that code.", false);
      return true;
    }
    try
    {
      int code = Integer.parseInt(args[2]);
      if (code < 0)
      {
        MessageHandler.SendErrorMessage(player, 
          "The code must be a positive integer.", false);
      }
      else
      {
        Station station = StationHandler.GetStationByName(args[1]);
        station.setCode(args[2]);
        StationHandler.SaveStations(this.plugin);
        MessageHandler.SendMessage(player, "Station code set.");
      }
      return true;
    }
    catch (NumberFormatException e)
    {
      MessageHandler.SendErrorMessage(player, 
        "The code must be a positive integer.", false);
      return true;
    }
    catch (IOException e)
    {
      MessageHandler.SendErrorMessage(player, 
        "Stations could not be saved. Please contact developer.", 
        false);
    }
    return true;
  }
  
  private boolean removeCommand(Player player, Command command, String label, String[] args)
  {
    if (!this.plugin.hasPermission(player, "SubwaySystem.Remove"))
    {
      MessageHandler.SendErrorMessage(player, 
        "Sorry, you do not have permission to do this.", false);
      return true;
    }
    if (args.length < 2)
    {
      MessageHandler.SendErrorMessage(player, 
        "You formatted the command incorrectly", false);
      return false;
    }
    String combine = "";
    for (int i = 1; i < args.length; i++)
    {
      combine = combine + args[i];
      if (i != args.length - 1) {
        combine = combine + " ";
      }
    }
    String[] split = combine.split("\"");
    String region = "";
    if (split.length == 2)
    {
      region = split[1];
    }
    else
    {
      MessageHandler.SendErrorMessage(player, 
        "You formatted the command incorrectly", false);
      return false;
    }
    Station station = StationHandler.GetStationByName(region);
    if (station == null)
    {
      MessageHandler.SendErrorMessage(player, 
        "No station exists with that name.", false);
      return false;
    }
    StationHandler.RemoveStation(station);
    try
    {
      StationHandler.SaveStations(this.plugin);
    }
    catch (IOException e)
    {
      MessageHandler.SendErrorMessage(player, 
        "Stations could not be saved. Please contact developer.", 
        false);
      return true;
    }
    MessageHandler.SendMessage(player, "Station removed.");
    return true;
  }
  
  private boolean customGenerateCommand(Player player, Command command, String label, String[] args)
  {
    if (!this.plugin.hasPermission(player, "SubwaySystem.Custom"))
    {
      MessageHandler.SendErrorMessage(player, 
        "Sorry, you do not have permission to do this.", false);
      return true;
    }
    if ((args.length < 3) || (args.length > 3))
    {
      MessageHandler.SendErrorMessage(player, 
        "You formatted the command incorrectly", false);
      return false;
    }
    File schematic = new File(this.plugin.getWorldEdit().getDataFolder(), "schematics/" + args[2] + ".schematic");
    if (!schematic.exists())
    {
      MessageHandler.SendErrorMessage(player, 
        "Sorry, that schematic does not exist.", false);
      return true;
    }
    BukkitWorld bukkitWorld = new BukkitWorld(player.getWorld());
    EditSession session = new EditSession(bukkitWorld, 1000000);
    session.enableQueue();
    if (GenerationHandler.SetGeneration(player, schematic, player.getLocation(), args[1]))
    {
    
      try
      {
        CuboidClipboard clipboard = SchematicFormat.MCEDIT
          .load(schematic);
        clipboard.paste(session, new Vector(player.getLocation()
          .getBlockX(), player.getLocation().getBlockY(), player
          .getLocation().getBlockZ()), false, false);
        session.flushQueue();
      }
      catch (MaxChangedBlocksException e)
      {
        e.printStackTrace();
      }
      catch (IOException|DataException e1)
      {
        e1.printStackTrace();
      }
      

      MessageHandler.SendMessage(
        player, 
        "&bPlease select (Right click) the powered rail that will be used as the receiving location.");
      
      MessageHandler.SendMessage(
        player, 
        "&dNote: Once clicked, the subway will be generated relative to this and cannot be stopped.");
      return true;
    }
    MessageHandler.SendErrorMessage(player, 
      "Sorry, there is already a subway generation active.", false);
    return true;
  }
  
  private boolean searchCommand(Player player, Command command, String label, String[] args)
  {
    if (!this.plugin.hasPermission(player, "SubwaySystem.Search"))
    {
      MessageHandler.SendErrorMessage(player, 
        "Sorry, you do not have permission to do this.", false);
      return true;
    }
    if ((args.length < 2) || (args.length > 2))
    {
      MessageHandler.SendErrorMessage(player, 
        "You formatted the command incorrectly", false);
      return false;
    }
    ProtectedRegion region = this.plugin.getWorldGuard()
      .getRegionManager(player.getWorld()).getRegion(args[1]);
    if (region == null)
    {
      MessageHandler.SendErrorMessage(player, 
        "No region exists with that name.", false);
      return true;
    }
    Location location = new Location(player.getWorld(), region
      .getMinimumPoint().getBlockX(), region.getMinimumPoint()
      .getBlockY(), region.getMinimumPoint().getBlockZ());
    
    Station station = StationHandler.GetNearestStation(location);
    if (station == null)
    {
      MessageHandler.SendErrorMessage(player, "Something went wrong...", 
        false);
      return true;
    }
    MessageHandler.SendMessage(player, 
      "&fClosest station to &b" + region.getId() + "&f is &b" + 
      station.getGrid().getX() + "&f,&b"+ station.getGrid().getZ() + "");
    return true;
  }
  
  private boolean generateCommand(Player player, Command command, String label, String[] args)
  {
	boolean regenSpawn = true;
    if (!this.plugin.hasPermission(player, "SubwaySystem.Generate"))
    {
      MessageHandler.SendErrorMessage(player, 
        "Sorry, you do not have permission to do this.", false);
      return true;
    }
    if ((args.length < 3) || (args.length > 4))
    {
      MessageHandler.SendErrorMessage(player, 
        "You formatted the command incorrectly", false);
      return false;
    }
    int radius = -1;
    try
    {
      radius = Integer.parseInt(args[1]);
    }
    catch (NumberFormatException e)
    {
      MessageHandler.SendErrorMessage(player, 
        "You did not enter an integer as a radius.", false);
      return false;
    }
    if (radius < 0)
    {
      MessageHandler.SendErrorMessage(player, 
        "You cannot set a radius smaller than 0.", false);
      return true;
    }
    File schematic = new File(this.plugin.getWorldEdit().getDataFolder(), "schematics/" + args[2] + ".schematic");
    if (!schematic.exists())
    {
      MessageHandler.SendErrorMessage(player, 
        "Sorry, that schematic does not exist.", false);
      return true;
    }
    BukkitWorld bukkitWorld = new BukkitWorld(player.getWorld());
    EditSession session = new EditSession(bukkitWorld, 1000000);
    session.enableQueue();
    if (GenerationHandler.SetGeneration(player, schematic, player.getLocation(), radius))
    {
    
    	//TODO expand
    	if (args.length == 4){
    		if (args[3].equalsIgnoreCase("expand") || args[3].equalsIgnoreCase("-e")){
    			regenSpawn = false;
    		}		
    	}
    	
    	if (regenSpawn == true){
    		try
    	      {
    	        CuboidClipboard clipboard = SchematicFormat.MCEDIT
    	          .load(schematic);
    	        clipboard.paste(session, new Vector(player.getLocation()
    	          .getBlockX(), player.getLocation().getBlockY(), player
    	          .getLocation().getBlockZ()), false, false);
    	        session.flushQueue();
    	      }
    	      catch (MaxChangedBlocksException e)
    	      {
    	        e.printStackTrace();
    	      }
    	      catch (IOException|DataException e1)
    	      {
    	        e1.printStackTrace();
    	      }
    	}
    		
    		
    	
     
      
      MessageHandler.SendMessage(
        player, 
        "&bPlease select (Right click) the powered rail that will be used as the receiving location.");
      
      MessageHandler.SendMessage(
        player, 
        "&dNote: Once clicked, all subsequent subways will be generated relative to this and cannot be stopped.");
      return true;
    }
    MessageHandler.SendErrorMessage(player, 
      "Sorry, there is already a subway generation active.", false);
    return true;
  }
}
