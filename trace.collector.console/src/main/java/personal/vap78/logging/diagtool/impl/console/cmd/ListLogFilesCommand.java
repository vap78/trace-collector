package personal.vap78.logging.diagtool.impl.console.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import personal.vap78.logging.diagtool.LogFileDescriptor;
import personal.vap78.logging.diagtool.impl.console.ConsoleSession;

public class ListLogFilesCommand extends AbstractLogCommand {

  public static final String LIST_LOGS_COMMAND = "list-logs";
  
  
  public ListLogFilesCommand(ConsoleSession session) {
    super(session);
  }

  @Override
  protected String getCommandName() {
    return LIST_LOGS_COMMAND;
  }

  @Override
  protected void addCommandSpecificParameters() {
  }
  
  @Override
  public boolean isExecutionSuccessful() {
    return consoleOutput != null && consoleOutput.indexOf("[list-logs] operation is successful.") != -1;
  }
  
  public Map<String, LogFileDescriptor> parseListLogsOutput() {
    Map<String, LogFileDescriptor> files = new HashMap<String, LogFileDescriptor>();
    BufferedReader reader = new BufferedReader(new StringReader(getConsoleOutput()));
    String line = null;
    boolean success = false;
    try {
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.startsWith("[list-logs] operation is successful")) {
          success = true;
          continue;
        }

        if (success && line.endsWith(".log")) {
          String[] parts = line.split(" ");
          String fileName = parts[parts.length - 1];
          long time = getFileTime(fileName);
          String type = getType(fileName);
          LogFileDescriptor lfd = files.get(type);
          if (lfd == null) {
            lfd = new LogFileDescriptor();
            lfd.setTime(time);
            lfd.setType(type);
            lfd.setName(fileName);
            files.put(lfd.getType(), lfd);
          } else if (lfd.getType().equals(type) && lfd.getTime() < time) {
            lfd.setName(fileName);
          }
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return files;
  }
  
  private String getType(String fileName) {
    if (fileName.startsWith(LJS_TRACE)) {
      return LJS_TRACE;
    } else if (fileName.startsWith(HTTP_TRACE)) {
      return HTTP_TRACE;
    }
    return "other";
  }

  private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
  
  private long getFileTime(String fileName) {
    String[] fileNameParts = fileName.split("_");
    String dateStr = fileNameParts[fileNameParts.length - 1];
    if (dateStr.endsWith(".log")) {
      dateStr = dateStr.substring(0, dateStr.indexOf(".log"));
    }
    
    Date date = null;
    try {
      date = FORMAT.parse(dateStr);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    
    return date.getTime();
  }
}
