package Exceptions;

public class PluginNotFoundException
  extends Exception
{
  private static final long serialVersionUID = 7531009059740357427L;
  private final String name;
  
  public PluginNotFoundException(String name)
  {
    this.name = name;
  }
  
  public String getMessage()
  {
    return "Plugin not found: " + this.name + ".";
  }
}
