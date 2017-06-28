package me.Fahlur.Subway;

import Handlers.MessageHandler;
import Handlers.StationHandler;
import Util.Vector2;
import org.bukkit.block.Sign;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

public class SubwayPlayer
  implements Runnable
{
  private final Player player;
  private final Station origin;
  private Station destination;
  private String code = "";
  
  public SubwayPlayer(Player player, Station origin)
  {
    this.player = player;
    this.origin = origin;
    this.destination = origin;
  }
  
  public void update(Subway plugin, Sign sign)
  {
    if (!plugin.hasPermission(this.player, "SubwaySystem.Donor")) {
      this.destination = this.origin;
    }
    Station newDestination = parseSignForDirection(sign);
    if (newDestination != null)
    {
      this.destination = newDestination;
      MessageHandler.SendMessage(this.player, "&bTransit Destination Set: &fStation " + 
        this.destination.getGrid().getX() + "," + this.destination.getGrid().getZ());
      return;
    }
    newDestination = parseSignForCustom(sign);
    if (newDestination != null)
    {
      this.destination = newDestination;
      MessageHandler.SendMessage(this.player, "&bTransit Destination Set: &f" + 
        this.destination.getRegion().getId() + " &8[Code: " + 
        this.destination.getCode()+"]");
      return;
    }
    if (!this.code.equalsIgnoreCase("")) {
      MessageHandler.SendMessage(this.player, "&bCurrent Code: &f" + 
        this.code);
    } else {
      MessageHandler.SendErrorMessage(this.player, 
        "Sorry, this destination is not in service.", false);
    }
  }
  
  public Player getPlayer()
  {
    return this.player;
  }
  
  public Station getDestination()
  {
    return this.destination;
  }
  
  public void run()
  {
    Minecart minecart = (Minecart)this.player.getWorld().spawn(
      this.destination.getReceiving(), Minecart.class);
    minecart.setPassenger(this.player);
    minecart.setVelocity(this.destination.getVelocity());
  }
  
  private Station parseSignForCustom(Sign sign)
  {
    try
    {
      if (this.code.length() == 3) {
        this.code = "";
      }
      int num = Integer.parseInt(sign.getLine(1));
      this.code += num;
      return StationHandler.GetStationByCode(this.code);
    }
    catch (NumberFormatException localNumberFormatException) {}
    return null;
  }
  
  private Station parseSignForDirection(Sign sign)
  {
    String signDest = sign.getLine(1);
    
    int x = this.destination.getGrid().getX();
    int z = this.destination.getGrid().getZ();
    if (this.destination.isCustom()) {
      this.destination = this.origin;
    }
    if(signDest.equalsIgnoreCase("NorthWest") || signDest.equalsIgnoreCase("NW")){
    	x--;z++;    	
    } else if(signDest.equalsIgnoreCase("North") || signDest.equalsIgnoreCase("N")){
    	z++;
  	} else if(signDest.equalsIgnoreCase("NorthEast") || signDest.equalsIgnoreCase("NE")){
  		x++;z++;
  	} else if(signDest.equalsIgnoreCase("East") || signDest.equalsIgnoreCase("E")){
  	  	x++;
  	}  else if(signDest.equalsIgnoreCase("SouthEast") || signDest.equalsIgnoreCase("SE")){
  	  	x++;z--;
  	}  else if(signDest.equalsIgnoreCase("South") || signDest.equalsIgnoreCase("S")){
  	  	z--;
  	}  else if(signDest.equalsIgnoreCase("SouthWest") || signDest.equalsIgnoreCase("SW")){
  	  	x--;z--;
  	}  else if(signDest.equalsIgnoreCase("West") || signDest.equalsIgnoreCase("W")){
  	  	x--;  	  
  	}
    
    if (x == this.destination.getGrid().getX() && z == this.destination.getGrid().getZ()){ //If destination hasn't changed.
    	return null;
    } else { //Return new destination
    	return StationHandler.GetStationByVector(new Vector2(x, z));
    }
  }
}
