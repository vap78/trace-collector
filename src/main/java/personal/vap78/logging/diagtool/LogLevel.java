package personal.vap78.logging.diagtool;

public enum LogLevel {

  ALL("ALL"), TRACE("TRACE"), DEBUG("DEBUG"), 
  INFO("INFO"), WARNING("WARNING"), ERROR("ERROR"), FATAL("FATAL");
  
  private String name;

  LogLevel(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
}
