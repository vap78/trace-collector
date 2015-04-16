package personal.vap78.logging.diagtool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.management.RuntimeErrorException;

public class SetLogLevelCommand extends AbstractLogCommand {
  public static final String SET_LOG_LEVEL_COMMAND = "set-log-level";
  private List<String> locations;
  private LogLevel level;
  private int locationsPart;
  private boolean finishedAllLocations = false;


  public SetLogLevelCommand(Session session, List<String> locations, LogLevel level) {
    super(session);
    this.locations = locations;
    this.level = level;
    locationsPart = 0;
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
  
  public void getNextTenLocations() {
    
  }

  @Override
  protected String getCommandName() {
    return SET_LOG_LEVEL_COMMAND;
  }
  
  @Override
  public void executeConsoleTool() throws IOException, CommandExecutionException {
    consoleOutput = new StringBuilder();
    do {
      addCommonCommandParameters();
      
      addCommandSpecificParameters();
      
      ProcessBuilder pb = new ProcessBuilder();
      pb.directory(new File("."));
      command.add(0, getCommandName());
      command.add(0, executablePath);
  
      printCommandParameters();
  
      pb.command(command);
      pb.redirectErrorStream(true);
      Process p = pb.start();
  
      System.out.println("=================================================");
      System.out.println("Executing neo console tool operation " + getCommandName());
      System.out.println("=================================================");
      BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
      
      String line = null;
      while ((line = input.readLine()) != null) {
        consoleOutput.append(line).append("\n");
      }
    } while (finishedAllLocations == false);
  }
  
  @Override
  public boolean isExecutionSuccessful() {
    return consoleOutput != null && consoleOutput.indexOf("[set-log-level] operation is successful.") != -1;
  }
  
  private String locationsToString() {
    if (finishedAllLocations) {
      throw new RuntimeException("Already iterated all locations");
    }
    StringBuilder builder = new StringBuilder();
    for (int i = locationsPart * 50; i < locationsPart * 50 + 50; i++) {
      if (i < locations.size()) {
        builder.append(locations.get(i));
        if (i < locationsPart * 50 + 49 && i < locations.size() - 1) {
          builder.append(",");
        }
      } else {
        finishedAllLocations = true;
        break;
      }
    }
    locationsPart++;
    return builder.toString();
  }
  
  public static void main(String[] args) {
    List<String> locations = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      locations.add("location" + i);
    }
    SetLogLevelCommand cmd = new SetLogLevelCommand(new Session("", new Properties()), locations,LogLevel.ALL);
    System.out.println(cmd.locationsToString());
    System.out.println(cmd.locationsToString());
    System.out.println(cmd.locationsToString());
    System.out.println(cmd.locationsToString());
  }
  
}
