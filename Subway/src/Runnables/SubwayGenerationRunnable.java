package Runnables;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import Handlers.MessageHandler;
import Handlers.StationHandler;
import me.Fahlur.Subway.Station;
import me.Fahlur.Subway.Subway;
import Util.Vector2;
import Util.Vector3;

import java.io.File;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.World;

@SuppressWarnings("deprecation")
public class SubwayGenerationRunnable
  implements Runnable
{
  private final Subway plugin;
  private final World world;
  private final Location location;
  private final Location receiving;
  private final Vector2 grid;
  private final CuboidClipboard clipboard;
  private final EditSession session;
  private final String name;
  private final boolean custom;
  
  public SubwayGenerationRunnable(Subway plugin, File schematic, String name, World world, Location location, Location receiving, Vector2 grid, boolean custom)
    throws DataException, IOException
  {
    this.plugin = plugin;
    this.name = name;
    this.world = world;
    this.location = location;
    this.receiving = receiving;
    this.grid = grid;
    this.custom = custom;
    BukkitWorld bukkitWorld = new BukkitWorld(world);
    this.session = new EditSession(bukkitWorld, 1000000);
    this.session.enableQueue();
    
    this.clipboard = SchematicFormat.MCEDIT.load(schematic);
  }
  
  private ProtectedCuboidRegion setupProtection()
    throws Exception
  {
    BlockVector lower = new BlockVector(new Vector(
      this.location.getBlockX() + 
      this.clipboard.getOffset().getBlockX(), 0, 
      this.location.getBlockZ() + 
      this.clipboard.getOffset().getBlockZ()));
    BlockVector upper = new BlockVector(new Vector(
      this.location.getBlockX() + 
      this.clipboard.getOffset().getBlockX() + (
      this.clipboard.getSize().getBlockX() - 1), 256, 
      this.location.getBlockZ() + 
      this.clipboard.getOffset().getBlockZ() + (
      this.clipboard.getSize().getBlockZ() - 1)));
    DefaultDomain domain = new DefaultDomain();
    domain.addPlayer("Server");
    ProtectedCuboidRegion protection = new ProtectedCuboidRegion(this.name, 
      lower, upper);
    protection.setPriority(100);
    protection.setOwners(domain);
    
    if (this.custom){
    	protection
        .setFlag(DefaultFlag.GREET_MESSAGE, "&f- Welcome to Station (&b" + this.name.substring(8)+"&f)");
    }else{
    	protection
        .setFlag(DefaultFlag.GREET_MESSAGE, "&f- Welcome to Station (&b" + this.grid.getX() + "&f,&b" + this.grid.getZ()+"&f)");	
    }
    protection.setFlag(DefaultFlag.PLACE_VEHICLE, StateFlag.State.ALLOW);
    protection.setFlag(DefaultFlag.DESTROY_VEHICLE, StateFlag.State.ALLOW);
    protection.setFlag(DefaultFlag.USE, StateFlag.State.ALLOW);
    protection.setFlag(DefaultFlag.MOB_SPAWNING, StateFlag.State.DENY);
    protection.setFlag(DefaultFlag.INTERACT, StateFlag.State.ALLOW);
    
    
    this.plugin.getWorldGuard().getGlobalRegionManager().get(this.world)
      .addRegion(protection);
    this.plugin.getWorldGuard().getGlobalRegionManager().get(this.world)
      .save();
    return protection;
  }
  
  public void run()
  {
    try
    {
      this.location.getWorld().loadChunk(this.location.getBlockX(), 
        this.location.getBlockZ());
      this.clipboard.paste(
        this.session, 
        new Vector(this.location.getBlockX(), this.location
        .getBlockY(), this.location.getBlockZ()), false, 
        false);
      this.session.flushQueue();
      if (this.receiving != null)
      {
        Station station = new Station(setupProtection().getId(), 
          this.grid, new Vector3(this.receiving.getWorld()
          .getName(), this.receiving.getBlockX(), 
          this.receiving.getBlockY(), 
          this.receiving.getBlockZ()), "-1", this.custom);
        station.initialize(this.plugin);
        StationHandler.AddStation(station);
      }
    }
    catch (MaxChangedBlocksException e)
    {
    	e.printStackTrace();
      MessageHandler.SendErrorMessage(null, "Failed to generate subway.", 
        true);
    }
    catch (Exception e)
    {
    	e.printStackTrace();
      MessageHandler.SendErrorMessage(null, "Failed to generate subway.", 
        true);
    }
  }
}
