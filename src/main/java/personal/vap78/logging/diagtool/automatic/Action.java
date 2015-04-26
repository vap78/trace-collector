package personal.vap78.logging.diagtool.automatic;

public enum Action {

  START, STOP;
  
  public static Action getByName(String name) {
    if ("start".equalsIgnoreCase(name)) {
      return START;
    } else if ("stop".equalsIgnoreCase(name)) {
      return STOP;
    } else {
      throw new IllegalArgumentException("Unknown action: " + name);
    }
  }
}
