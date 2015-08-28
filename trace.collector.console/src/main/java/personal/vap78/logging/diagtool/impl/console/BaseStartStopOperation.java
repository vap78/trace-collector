package personal.vap78.logging.diagtool.impl.console;

import java.util.ArrayList;
import java.util.List;

import personal.vap78.logging.diagtool.LogLevel;
import personal.vap78.logging.diagtool.TraceConfiguration;
import personal.vap78.logging.diagtool.impl.console.cmd.ListLoggersCommand;
import personal.vap78.logging.diagtool.impl.console.cmd.SetLogLevelCommand;

public class BaseStartStopOperation {

  public void setLoggerLevel(ConsoleSession session, TraceConfiguration configuration, LogLevel level) throws CommandExecutionException {
    ListLoggersCommand listLoggersCommand = new ListLoggersCommand(session);
    listLoggersCommand.executeConsoleTool();
    List<String> loggersToSet = new ArrayList<>();
    for (String logger : configuration.getLoggers()) {
      List<String> matchingLoggers = listLoggersCommand.getMatchingLoggers(logger);
      loggersToSet.addAll(matchingLoggers);
    }
    
    SetLogLevelCommand command = new SetLogLevelCommand(session, loggersToSet, level);
    
    command.executeConsoleTool();
    System.out.println(command.getConsoleOutput());
  }
}
