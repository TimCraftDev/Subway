package Handlers;

import java.util.ArrayList;
import java.util.List;

import me.Fahlur.Subway.SubwayPlayer;

public class PlayerHandler
{
  private static List<SubwayPlayer> players = new ArrayList<SubwayPlayer>();
  
  public static void AddPlayer(SubwayPlayer player)
  {
    for (int i = players.size() - 1; i >= 0; i--) {
      if (((SubwayPlayer)players.get(i)).getPlayer().getName().equalsIgnoreCase(player.getPlayer().getName())) {
        players.remove(i);
      }
    }
    players.add(player);
  }
  
  public static void RemovePlayer(SubwayPlayer player)
  {
    players.remove(player);
  }
  
  public static boolean ContainsPlayer(String name)
  {
    for (SubwayPlayer player : players) {
      if (player.getPlayer().getName().equalsIgnoreCase(name)) {
        return true;
      }
    }
    return false;
  }
  
  public static SubwayPlayer GetPlayer(String name)
  {
    for (SubwayPlayer player : players) {
      if (player.getPlayer().getName().equalsIgnoreCase(name)) {
        return player;
      }
    }
    return null;
  }
}
