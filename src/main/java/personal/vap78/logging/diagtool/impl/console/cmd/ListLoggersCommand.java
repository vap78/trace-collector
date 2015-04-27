package personal.vap78.logging.diagtool.impl.console.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CharSequenceReader;

import personal.vap78.logging.diagtool.http.Session;
import personal.vap78.logging.diagtool.impl.console.CommandExecutionException;

public class ListLoggersCommand extends AbstractLogCommand {

  private static final String LIST_LOGGERS = "list-loggers";
  private List<String> loggers = new ArrayList<>();
  
  public ListLoggersCommand(Session session) {
    super(session);
  }

  @Override
  protected String getCommandName() {
    return LIST_LOGGERS;
  }
  
  @Override
  public void executeConsoleTool() throws IOException, CommandExecutionException {
    super.executeConsoleTool();
    if (!isExecutionSuccessful()) {
      throw new CommandExecutionException(this, "Failed to list the loggers");
    }
    parseLoggerList();
  }
  
  @Override
  protected void addCommandSpecificParameters() {
  }

  @Override
  public boolean isExecutionSuccessful() {
    return consoleOutput.indexOf("[list-loggers] operation is successful.") > -1;
  }

  public List<String> getMatchingLoggers(String prefix) {
    List<String> matchingLoggers = new ArrayList<>();
    for (String logger : loggers) {
      if (logger.startsWith(prefix))  {
        matchingLoggers.add(logger);
      }
    }
    return matchingLoggers;
  }

  
  private void parseLoggerList() throws IOException {
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new CharSequenceReader(consoleOutput));
      String line = null;
      boolean loggersStarted = false;
      while ((line = reader.readLine()) != null) {
        if (line.equals("[list-loggers] operation is successful.")) {
          loggersStarted = true;
          continue;
        }
        if (loggersStarted && isLoggerLine(line)) {
          line = line.trim();
          int lastSpace = line.lastIndexOf(" ");
          if (lastSpace > -1) {
            String traceLocation = line.substring(lastSpace + 1);
            loggers.add(traceLocation);
          }
        }
      }
    } finally {
      IOUtils.closeQuietly(reader);
    }
  }

  private boolean isLoggerLine(String line) {
    return line.startsWith("OFF") ||
           line.startsWith("ERROR") ||
           line.startsWith("WARNING") ||
           line.startsWith("INFO") ||
           line.startsWith("DEBUG") ||
           line.startsWith("TRACE") ||
           line.startsWith("ALL");
  }
}