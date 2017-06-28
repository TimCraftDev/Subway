package Handlers;


import me.Fahlur.Subway.Subway;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageHandler
{
  public static void SendMessage(CommandSender sender, String message)
  {
    if (sender != null) {
      String newString = ChatColor.translateAlternateColorCodes('&', message);
      sender.sendMessage(newString);
    }
  }
  
  public static void SendErrorMessage(CommandSender sender, String message, boolean toConsole)
  {
    if (sender != null) {
      sender.sendMessage(ChatColor.RED + "ERROR: " + ChatColor.translateAlternateColorCodes('&', message));
    }
    if (toConsole) {
      System.out.println(ChatColor.RED + "ERROR: " + ChatColor.translateAlternateColorCodes('&', message));
    }
  }
  
  public static void SendDebugMessage(CommandSender sender, String message, boolean toConsole)
  {
    if (sender != null) {
      sender.sendMessage(ChatColor.BLUE + "DEBUG: " + ChatColor.translateAlternateColorCodes('&', message));
    }
    if (toConsole) {
      System.out.println(ChatColor.BLUE + "DEBUG: " + ChatColor.translateAlternateColorCodes('&', message));
    }
  }
  
  public static void SendServerMessage(Subway plugin, String message)
  {
    plugin.getServer().broadcastMessage(
      ChatColor.GOLD + "[SubwayGeneration]: " + ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', message));
  }
}
