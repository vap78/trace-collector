package com.sap.cloud.logging.diagtool;

import java.io.BufferedReader;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class ListLogFilesCommand extends AbstractLogCommand {

  public static final String LIST_LOGS_COMMAND = "list-logs";
  
  
  public ListLogFilesCommand(Properties props) {
    super(props);
  }

  @Override
  protected String getCommandName() {
    return LIST_LOGS_COMMAND;
  }

  @Override
  protected void addCommandSpecificParameters() {
  }
  
  public Map<String, LogFileDescriptor> parseListLogsOutput(BufferedReader reader) throws Exception {
    Map<String, LogFileDescriptor> files = new HashMap<String, LogFileDescriptor>();
    consoleOutput = new StringBuilder();
    String line = null;
    boolean success = false;
    while ((line = reader.readLine()) != null) {
      consoleOutput.append(line).append("\n");
      System.out.print(">> ");
      System.out.println(line);
      line = line.trim();
      if (line.startsWith("[list-logs] operation is successful")) {
        success = true;
        continue;
      }

      if (success && line.endsWith(".log")) {
        String[] parts = line.split(" ");
        String fileName = parts[parts.length - 1];
        long time = getFileTime(parts);
        String type = getType(fileName);
        LogFileDescriptor lfd = files.get(type);
        if (lfd == null) {
          lfd = new LogFileDescriptor();
          lfd.time = time;
          lfd.type = type;
          lfd.name = fileName;
          files.put(lfd.type, lfd);
        } else if (lfd.type.equals(type) && lfd.time < time) {
          lfd.name = fileName;
        }
      }
    }

    if (!success) {
      throw new CommandExecutionException(this, "Failed to execute command "+ LIST_LOGS_COMMAND);
    }
    
    return files;
  }
  
  private String getType(String fileName) {
    // TODO Auto-generated method stub
    if (fileName.startsWith(LJS_TRACE)) {
      return LJS_TRACE;
    } else if (fileName.startsWith(HTTP_TRACE)) {
      return HTTP_TRACE;
    }
    return "other";
  }

  private long getFileTime(String[] parts) throws Exception {
    String dateStr = "";
    for (int i = 0; i < parts.length - 1; i++) {
      dateStr += parts[i] + " ";
    }

    Date date = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault()).parse(dateStr);

    return date.getTime();
  }


}
