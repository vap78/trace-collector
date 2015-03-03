package personal.vap78.logging.diagtool;

import java.util.List;

public class SetLogLevelCommand extends AbstractLogCommand {
  public static final String SET_LOG_LEVEL_COMMAND = "set-log-level";
  private List<String> locations;
  private LogLevel level;


  public SetLogLevelCommand(Session session, List<String> locations, LogLevel level) {
    super(session);
    this.locations = locations;
    this.level = level;
  }

  @Override
  protected void addCommandSpecificParameters() {
    
    String loggers = locationsToString();
    System.out.println("Loggers to be changed: " + loggers);
    String levelStr = level.getName();
    System.out.println("Log level: " + levelStr);

    command.add("--loggers");
    command.add(loggers);
    command.add("--level");
    command.add(levelStr);

  }

  @Override
  protected String getCommandName() {
    return SET_LOG_LEVEL_COMMAND;
  }
  
  private String locationsToString() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < locations.size(); i++) {
      builder.append(locations.get(i));
      if (i < locations.size() - 1) {
        builder.append(",");
      }
    }
    return builder.toString();
  }
}
