package Exceptions;

public class SubwayInitializationException
  extends Exception
{
  private static final long serialVersionUID = -126289381987592734L;
  private String message;
  private String id;
  
  public SubwayInitializationException() {}
  
  public SubwayInitializationException(String id, String message)
  {
    this.id = id;
    this.message = message;
  }
  
  public String getMessage()
  {
    return "Region name: " + this.id + "." + this.message;
  }
}
